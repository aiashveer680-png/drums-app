package com.example.ui.exercise

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.DrumInstrument
import com.example.audio.DrumSoundEngine
import com.example.data.DrumRepository
import com.example.data.ExerciseProgressEntity
import com.example.model.Exercise
import com.example.model.ExerciseSessionStats
import com.example.model.HitFeedback
import com.example.model.ScheduledHit
import com.example.model.TimingRating
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

data class ExerciseUiState(
    val currentExercise: Exercise? = null,
    val isPlaying: Boolean = false,
    val bpm: Int = 80,
    val currentBar: Int = 0,
    val currentStep: Int = 0,
    val stepTimestamp: Long = 0L,
    val liveWaveformPeak: Float = 0f,
    val audioBackingEnabled: Boolean = true,
    val metronomeClickEnabled: Boolean = true,
    val stats: ExerciseSessionStats = ExerciseSessionStats(""),
    val latestFeedback: HitFeedback? = null,
    val isSessionCompleted: Boolean = false,
    val loopsCompleted: Int = 0,
    val targetLoops: Int = 4,
    val userSavedProgress: ExerciseProgressEntity? = null
)

class ExerciseViewModel(
    private val repository: DrumRepository,
    private val soundEngine: DrumSoundEngine,
    context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseUiState())
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

    private val _feedbackEvents = MutableSharedFlow<HitFeedback>(extraBufferCapacity = 8)
    val feedbackEvents: SharedFlow<HitFeedback> = _feedbackEvents.asSharedFlow()

    private var playbackJob: Job? = null
    private var sessionStartTime = 0L

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    // Processed hit tracker for current loop
    private val scoredHitIndices = mutableSetOf<String>()

    fun loadExercise(exerciseId: String) {
        val exercise = repository.exercises.firstOrNull { it.id == exerciseId } ?: repository.exercises.first()
        stopPlayback()
        _uiState.update {
            it.copy(
                currentExercise = exercise,
                bpm = exercise.defaultBpm,
                currentBar = 0,
                currentStep = 0,
                loopsCompleted = 0,
                stats = ExerciseSessionStats(exercise.id),
                latestFeedback = null,
                isSessionCompleted = false
            )
        }

        viewModelScope.launch {
            repository.allExerciseProgress.collect { progressList ->
                val match = progressList.firstOrNull { it.exerciseId == exercise.id }
                _uiState.update { it.copy(userSavedProgress = match) }
            }
        }
    }

    fun setBpm(newBpm: Int) {
        _uiState.update { it.copy(bpm = newBpm.coerceIn(40, 220)) }
    }

    fun toggleAudioBacking() {
        _uiState.update { it.copy(audioBackingEnabled = !it.audioBackingEnabled) }
    }

    fun toggleMetronomeClick() {
        _uiState.update { it.copy(metronomeClickEnabled = !it.metronomeClickEnabled) }
    }

    fun togglePlayPause() {
        if (_uiState.value.isPlaying) {
            stopPlayback()
        } else {
            startPlayback()
        }
    }

    fun startPlayback() {
        val exercise = _uiState.value.currentExercise ?: return
        if (_uiState.value.isPlaying) return

        sessionStartTime = System.currentTimeMillis()
        scoredHitIndices.clear()
        _uiState.update {
            it.copy(
                isPlaying = true,
                currentBar = 0,
                currentStep = 0,
                loopsCompleted = 0,
                stats = ExerciseSessionStats(exercise.id),
                isSessionCompleted = false
            )
        }

        playbackJob = viewModelScope.launch(Dispatchers.Default) {
            runExerciseLoop(exercise)
        }
    }

    fun stopPlayback() {
        playbackJob?.cancel()
        playbackJob = null
        _uiState.update { it.copy(isPlaying = false) }
    }

    fun onPadHit(instrument: DrumInstrument) {
        // 1. Play immediate synth sound
        soundEngine.play(instrument)
        triggerHaptic()

        val state = _uiState.value
        val exercise = state.currentExercise ?: return

        if (!state.isPlaying) return

        val currentBar = state.currentBar
        val currentStep = state.currentStep

        // Check if there is an expected hit for this instrument near this step (+/- 1 step window)
        var bestHit: ScheduledHit? = null
        var bestStepDiff = Int.MAX_VALUE

        val stepWindow = listOf((currentStep - 1 + exercise.stepsPerBar) % exercise.stepsPerBar, currentStep, (currentStep + 1) % exercise.stepsPerBar)

        for (hit in exercise.hits) {
            if (hit.bar == currentBar && hit.instrument == instrument && hit.step in stepWindow) {
                val hitKey = "${hit.bar}_${hit.step}_${hit.instrument.name}_${state.loopsCompleted}"
                if (!scoredHitIndices.contains(hitKey)) {
                    val diff = abs(hit.step - currentStep)
                    if (diff < bestStepDiff) {
                        bestStepDiff = diff
                        bestHit = hit
                    }
                }
            }
        }

        val feedback: HitFeedback
        val newStats: ExerciseSessionStats

        if (bestHit != null) {
            val hitKey = "${bestHit.bar}_${bestHit.step}_${bestHit.instrument.name}_${state.loopsCompleted}"
            scoredHitIndices.add(hitKey)

            val rating = when (bestStepDiff) {
                0 -> TimingRating.PERFECT
                1 -> if (currentStep < bestHit.step) TimingRating.EARLY else TimingRating.LATE
                else -> TimingRating.GOOD
            }
            feedback = HitFeedback(rating, (bestStepDiff * 25).toLong(), instrument)

            val combo = state.stats.currentCombo + 1
            val maxCombo = maxOf(combo, state.stats.maxCombo)
            val scoreAdd = rating.points + (combo * 5)

            newStats = state.stats.copy(
                totalExpectedHits = state.stats.totalExpectedHits + 1,
                perfectHits = state.stats.perfectHits + if (rating == TimingRating.PERFECT) 1 else 0,
                goodHits = state.stats.goodHits + if (rating == TimingRating.GOOD) 1 else 0,
                earlyHits = state.stats.earlyHits + if (rating == TimingRating.EARLY) 1 else 0,
                lateHits = state.stats.lateHits + if (rating == TimingRating.LATE) 1 else 0,
                currentCombo = combo,
                maxCombo = maxCombo,
                score = state.stats.score + scoreAdd
            )
        } else {
            // Stray hit or hit wrong instrument
            feedback = HitFeedback(TimingRating.MISS, 0, instrument)
            newStats = state.stats.copy(
                missedHits = state.stats.missedHits + 1,
                currentCombo = 0
            )
        }

        _uiState.update {
            it.copy(
                stats = newStats,
                latestFeedback = feedback
            )
        }
        _feedbackEvents.tryEmit(feedback)
    }

    private suspend fun runExerciseLoop(exercise: Exercise) {
        var currentBar = 0
        var currentStep = 0
        var loops = 0

        while (true) {
            val state = _uiState.value
            val bpm = state.bpm
            val stepsPerBar = exercise.stepsPerBar

            // Step interval in nanoseconds (each step is 16th note in 4/4 = 4 steps per beat)
            val stepsPerBeat = stepsPerBar / exercise.timeSignature.numerator
            val stepIntervalNanos = ((60.0 / (bpm * stepsPerBeat)) * 1_000_000_000.0).toLong()
            val targetNextTime = System.nanoTime() + stepIntervalNanos

            var peakEnergy = 0.15f

            // 1. Play metronome click if enabled and at quarter note beat downbeat
            if (state.metronomeClickEnabled && (currentStep % stepsPerBeat == 0)) {
                val beatIndex = (currentStep / stepsPerBeat) + 1
                if (beatIndex == 1) {
                    soundEngine.play(DrumInstrument.METRONOME_HIGH, volume = 0.85f)
                    peakEnergy = 0.95f
                } else {
                    soundEngine.play(DrumInstrument.METRONOME_LOW, volume = 0.65f)
                    peakEnergy = 0.75f
                }
            }

            // 2. Play backing drums if audio backing enabled
            if (state.audioBackingEnabled) {
                val hitsAtCurrentStep = exercise.hits.filter { it.bar == currentBar && it.step == currentStep }
                for (hit in hitsAtCurrentStep) {
                    val vol = if (hit.isAccented) 1.0f else 0.75f
                    soundEngine.play(hit.instrument, volume = vol)
                    peakEnergy = if (hit.instrument == DrumInstrument.KICK || hit.instrument == DrumInstrument.SNARE) 1.0f else 0.85f
                }
            }

            // Update UI state with step timestamp and live waveform peak
            _uiState.update {
                it.copy(
                    currentBar = currentBar,
                    currentStep = currentStep,
                    stepTimestamp = System.currentTimeMillis(),
                    liveWaveformPeak = peakEnergy,
                    loopsCompleted = loops
                )
            }

            // Check if step completed, move forward
            currentStep++
            if (currentStep >= stepsPerBar) {
                currentStep = 0
                currentBar++
                if (currentBar >= exercise.totalBars) {
                    currentBar = 0
                    loops++
                    // Check if target loops reached
                    if (loops >= state.targetLoops) {
                        finishExerciseSession()
                        break
                    }
                }
            }

            val sleepNanos = targetNextTime - System.nanoTime()
            if (sleepNanos > 0) {
                val millis = sleepNanos / 1_000_000L
                val nanos = (sleepNanos % 1_000_000L).toInt()
                try {
                    Thread.sleep(millis, nanos)
                } catch (e: InterruptedException) {
                    break
                }
            }
        }
    }

    private fun finishExerciseSession() {
        stopPlayback()
        val durationSec = ((System.currentTimeMillis() - sessionStartTime) / 1000).toInt().coerceAtLeast(1)
        val finalStats = _uiState.value.stats

        _uiState.update {
            it.copy(
                isPlaying = false,
                isSessionCompleted = true
            )
        }

        viewModelScope.launch {
            repository.recordExerciseSession(
                stats = finalStats,
                bpm = _uiState.value.bpm,
                durationSeconds = durationSec
            )
        }
    }

    fun dismissCompletionDialog() {
        _uiState.update { it.copy(isSessionCompleted = false) }
    }

    private fun triggerHaptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(20)
            }
        } catch (_: Exception) {}
    }
}
