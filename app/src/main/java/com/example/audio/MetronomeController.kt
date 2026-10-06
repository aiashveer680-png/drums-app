package com.example.audio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class TimeSignature(
    val numerator: Int,
    val denominator: Int,
    val displayName: String
) {
    companion object {
        val TS_4_4 = TimeSignature(4, 4, "4/4")
        val TS_3_4 = TimeSignature(3, 4, "3/4")
        val TS_2_4 = TimeSignature(2, 4, "2/4")
        val TS_5_4 = TimeSignature(5, 4, "5/4")
        val TS_6_8 = TimeSignature(6, 8, "6/8")
        val TS_7_8 = TimeSignature(7, 8, "7/8")
        val TS_12_8 = TimeSignature(12, 8, "12/8")

        val standardList = listOf(TS_4_4, TS_3_4, TS_2_4, TS_5_4, TS_6_8, TS_7_8, TS_12_8)
    }
}

enum class MetronomeSubdivision(val displayName: String, val multiplier: Int, val symbol: String) {
    QUARTER("Quarter (1/4)", 1, "♩"),
    EIGHTH("8th (1/8)", 2, "♫"),
    TRIPLET("Triplet (1/8T)", 3, "3"),
    SIXTEENTH("16th (1/16)", 4, "♬")
}

data class SpeedTrainerConfig(
    val enabled: Boolean = false,
    val bpmIncrement: Int = 2,
    val barsInterval: Int = 4,
    val targetBpm: Int = 160
)

data class StemMixer(
    val kickVolume: Float = 0.85f,
    val snareVolume: Float = 0.85f,
    val cymbalsVolume: Float = 0.75f,
    val clickVolume: Float = 0.9f
)

data class CountdownTimerConfig(
    val enabled: Boolean = false,
    val durationSeconds: Int = 300, // default 5 minutes
    val remainingSeconds: Int = 300,
    val isCompleted: Boolean = false
) {
    val progress: Float
        get() = if (durationSeconds > 0) {
            (1f - (remainingSeconds.toFloat() / durationSeconds.toFloat())).coerceIn(0f, 1f)
        } else 0f

    val formattedRemaining: String
        get() {
            val mins = remainingSeconds / 60
            val secs = remainingSeconds % 60
            return String.format("%02d:%02d", mins, secs)
        }

    val formattedTotal: String
        get() {
            val mins = durationSeconds / 60
            val secs = durationSeconds % 60
            return String.format("%02d:%02d", mins, secs)
        }
}

data class MetronomeState(
    val isRunning: Boolean = false,
    val bpm: Int = 100,
    val timeSignature: TimeSignature = TimeSignature.TS_4_4,
    val subdivision: MetronomeSubdivision = MetronomeSubdivision.QUARTER,
    val currentBeat: Int = 1,
    val currentSubdivision: Int = 0,
    val currentStep: Int = 0,
    val currentBar: Int = 1,
    val isAccent: Boolean = false,
    val flashTimestamp: Long = 0L,
    val speedTrainer: SpeedTrainerConfig = SpeedTrainerConfig(),
    val volume: Float = 0.9f,
    val stemMixer: StemMixer = StemMixer(),
    val selectedPresetRhythm: PresetRhythm? = null,
    val countdownTimer: CountdownTimerConfig = CountdownTimerConfig(),
    val liveWaveformPeak: Float = 0f,
    val waveformPoints: List<Float> = List(32) { 0.05f }
)

class MetronomeController(
    private val soundEngine: DrumSoundEngine,
    private val scope: CoroutineScope
) {
    private val _state = MutableStateFlow(MetronomeState())
    val state: StateFlow<MetronomeState> = _state.asStateFlow()

    private var metronomeJob: Job? = null
    private var waveformDecayJob: Job? = null
    private var timerJob: Job? = null
    private val tapTimestamps = mutableListOf<Long>()

    fun setBpm(newBpm: Int) {
        val clamped = newBpm.coerceIn(30, 260)
        _state.update { it.copy(bpm = clamped) }
    }

    fun adjustBpm(delta: Int) {
        setBpm(_state.value.bpm + delta)
    }

    fun halfTime() {
        setBpm((_state.value.bpm / 2).coerceAtLeast(30))
    }

    fun doubleTime() {
        setBpm((_state.value.bpm * 2).coerceAtMost(260))
    }

    fun setTimeSignature(ts: TimeSignature) {
        _state.update {
            it.copy(
                timeSignature = ts,
                currentBeat = 1,
                currentSubdivision = 0,
                currentStep = 0,
                selectedPresetRhythm = if (it.selectedPresetRhythm?.timeSignature?.displayName == ts.displayName) it.selectedPresetRhythm else null
            )
        }
    }

    fun setSubdivision(subdivision: MetronomeSubdivision) {
        _state.update { it.copy(subdivision = subdivision, currentSubdivision = 0) }
    }

    fun setSpeedTrainer(config: SpeedTrainerConfig) {
        _state.update { it.copy(speedTrainer = config) }
    }

    fun setVolume(volume: Float) {
        _state.update { it.copy(volume = volume.coerceIn(0f, 1f)) }
    }

    fun setStemMixer(mixer: StemMixer) {
        _state.update { it.copy(stemMixer = mixer) }
    }

    fun selectPresetRhythm(rhythm: PresetRhythm?) {
        _state.update {
            if (rhythm == null) {
                it.copy(selectedPresetRhythm = null)
            } else {
                it.copy(
                    selectedPresetRhythm = rhythm,
                    timeSignature = rhythm.timeSignature,
                    bpm = rhythm.defaultBpm,
                    currentBeat = 1,
                    currentStep = 0,
                    currentSubdivision = 0
                )
            }
        }
    }

    // Countdown Timer functions
    fun setTimerEnabled(enabled: Boolean) {
        _state.update { s ->
            val updatedTimer = s.countdownTimer.copy(
                enabled = enabled,
                remainingSeconds = s.countdownTimer.durationSeconds,
                isCompleted = false
            )
            s.copy(countdownTimer = updatedTimer)
        }
    }

    fun setTimerDuration(seconds: Int) {
        val clamped = seconds.coerceIn(30, 7200) // 30 sec to 120 mins
        _state.update { s ->
            val updatedTimer = s.countdownTimer.copy(
                durationSeconds = clamped,
                remainingSeconds = clamped,
                isCompleted = false
            )
            s.copy(countdownTimer = updatedTimer)
        }
    }

    fun adjustTimerDuration(deltaSeconds: Int) {
        val newDuration = (_state.value.countdownTimer.durationSeconds + deltaSeconds).coerceIn(30, 7200)
        setTimerDuration(newDuration)
    }

    fun resetTimer() {
        _state.update { s ->
            val resetTimer = s.countdownTimer.copy(
                remainingSeconds = s.countdownTimer.durationSeconds,
                isCompleted = false
            )
            s.copy(countdownTimer = resetTimer)
        }
    }

    fun dismissTimerCompletedDialog() {
        _state.update { s ->
            val resetTimer = s.countdownTimer.copy(
                isCompleted = false,
                remainingSeconds = s.countdownTimer.durationSeconds
            )
            s.copy(countdownTimer = resetTimer)
        }
    }

    fun tapTempo() {
        val now = System.currentTimeMillis()
        tapTimestamps.add(now)
        tapTimestamps.removeAll { now - it > 2500 }
        if (tapTimestamps.size >= 2) {
            val intervals = mutableListOf<Long>()
            for (i in 1 until tapTimestamps.size) {
                intervals.add(tapTimestamps[i] - tapTimestamps[i - 1])
            }
            val avgInterval = intervals.average()
            if (avgInterval > 100) {
                val computedBpm = (60000.0 / avgInterval).roundToInt()
                setBpm(computedBpm)
            }
        }
    }

    fun toggle() {
        if (_state.value.isRunning) {
            stop()
        } else {
            start()
        }
    }

    fun start() {
        if (_state.value.isRunning) return

        // If countdown timer is completed or reached 0, reset it before starting
        if (_state.value.countdownTimer.enabled && (_state.value.countdownTimer.remainingSeconds <= 0 || _state.value.countdownTimer.isCompleted)) {
            resetTimer()
        }

        _state.update { it.copy(isRunning = true, currentBeat = 1, currentSubdivision = 0, currentStep = 0, currentBar = 1) }

        metronomeJob = scope.launch(Dispatchers.Default) {
            runAdvancedLoop()
        }

        startWaveformDecay()

        // Start countdown timer if enabled
        if (_state.value.countdownTimer.enabled) {
            startCountdownTimer()
        }
    }

    fun stop() {
        metronomeJob?.cancel()
        metronomeJob = null
        waveformDecayJob?.cancel()
        waveformDecayJob = null
        timerJob?.cancel()
        timerJob = null

        _state.update {
            it.copy(
                isRunning = false,
                currentBeat = 1,
                currentSubdivision = 0,
                currentStep = 0,
                liveWaveformPeak = 0f,
                waveformPoints = List(32) { 0.05f }
            )
        }
    }

    private fun startCountdownTimer() {
        timerJob?.cancel()
        timerJob = scope.launch(Dispatchers.Default) {
            while (_state.value.isRunning && _state.value.countdownTimer.enabled) {
                delay(1000)
                val currentRem = _state.value.countdownTimer.remainingSeconds
                if (currentRem > 1) {
                    _state.update { s ->
                        s.copy(countdownTimer = s.countdownTimer.copy(remainingSeconds = currentRem - 1))
                    }
                } else {
                    // Timer finished!
                    _state.update { s ->
                        s.copy(countdownTimer = s.countdownTimer.copy(remainingSeconds = 0, isCompleted = true))
                    }
                    // Play celebratory acoustic cymbal crash
                    soundEngine.play(DrumInstrument.CRASH, volume = 0.95f)
                    stop()
                    break
                }
            }
        }
    }

    private fun startWaveformDecay() {
        waveformDecayJob?.cancel()
        waveformDecayJob = scope.launch(Dispatchers.Default) {
            while (_state.value.isRunning) {
                delay(35)
                _state.update { s ->
                    val decayedPeak = (s.liveWaveformPeak * 0.82f).coerceAtLeast(0.04f)
                    val updatedWave = s.waveformPoints.mapIndexed { index, fl ->
                        val target = decayedPeak * (0.4f + 0.6f * kotlin.math.sin((index + System.currentTimeMillis() / 60.0) * 0.5).toFloat().coerceIn(0f, 1f))
                        (fl * 0.7f + target * 0.3f).coerceIn(0.03f, 1.0f)
                    }
                    s.copy(liveWaveformPeak = decayedPeak, waveformPoints = updatedWave)
                }
            }
        }
    }

    private suspend fun runAdvancedLoop() {
        var beat = 1
        var subIndex = 0
        var bar = 1
        var globalStep = 0

        while (true) {
            val currentState = _state.value
            val bpm = currentState.bpm
            val preset = currentState.selectedPresetRhythm
            val subMultiplier = if (preset != null) {
                (preset.stepsPerBar / currentState.timeSignature.numerator).coerceAtLeast(1)
            } else {
                currentState.subdivision.multiplier
            }
            val numerator = currentState.timeSignature.numerator
            val masterVol = currentState.volume
            val mixer = currentState.stemMixer

            val isMainBeat = (subIndex == 0)
            val isPrimaryAccent = (isMainBeat && beat == 1)

            var peakAudio = 0.2f

            // 1. Play metronome click stem
            if (mixer.clickVolume > 0.01f) {
                val effectiveClickVol = masterVol * mixer.clickVolume
                if (isPrimaryAccent) {
                    soundEngine.play(DrumInstrument.METRONOME_HIGH, volume = effectiveClickVol)
                    peakAudio = maxOf(peakAudio, 0.95f)
                } else if (isMainBeat) {
                    soundEngine.play(DrumInstrument.METRONOME_LOW, volume = effectiveClickVol * 0.85f)
                    peakAudio = maxOf(peakAudio, 0.75f)
                } else {
                    soundEngine.play(DrumInstrument.METRONOME_SUB, volume = effectiveClickVol * 0.5f)
                    peakAudio = maxOf(peakAudio, 0.45f)
                }
            }

            // 2. Play pre-set drum rhythm stems if a groove is selected
            if (preset != null) {
                val hitsAtStep = preset.hits.filter { it.step == globalStep }
                for (hit in hitsAtStep) {
                    when (hit.instrument) {
                        DrumInstrument.KICK -> {
                            if (mixer.kickVolume > 0.01f) {
                                soundEngine.play(hit.instrument, volume = masterVol * mixer.kickVolume * (if (hit.isAccented) 1.0f else 0.8f))
                                peakAudio = maxOf(peakAudio, 0.95f)
                            }
                        }
                        DrumInstrument.SNARE -> {
                            if (mixer.snareVolume > 0.01f) {
                                soundEngine.play(hit.instrument, volume = masterVol * mixer.snareVolume * (if (hit.isAccented) 1.0f else 0.8f))
                                peakAudio = maxOf(peakAudio, 0.9f)
                            }
                        }
                        DrumInstrument.HIHAT_CLOSED, DrumInstrument.HIHAT_OPEN,
                        DrumInstrument.RIDE, DrumInstrument.CRASH -> {
                            if (mixer.cymbalsVolume > 0.01f) {
                                soundEngine.play(hit.instrument, volume = masterVol * mixer.cymbalsVolume * (if (hit.isAccented) 1.0f else 0.75f))
                                peakAudio = maxOf(peakAudio, 0.7f)
                            }
                        }
                        else -> {
                            soundEngine.play(hit.instrument, volume = masterVol * 0.8f)
                            peakAudio = maxOf(peakAudio, 0.8f)
                        }
                    }
                }
            }

            // Update state with beat info and peak amplitude
            _state.update {
                it.copy(
                    currentBeat = beat,
                    currentSubdivision = subIndex,
                    currentStep = globalStep,
                    currentBar = bar,
                    isAccent = isPrimaryAccent,
                    flashTimestamp = System.currentTimeMillis(),
                    liveWaveformPeak = peakAudio
                )
            }

            // High precision sleep calculation
            val stepIntervalNanos = ((60.0 / (bpm * subMultiplier)) * 1_000_000_000.0).toLong()
            val targetNextTime = System.nanoTime() + stepIntervalNanos

            // Advance step and beat
            subIndex++
            val maxSteps = if (preset != null) preset.stepsPerBar else (numerator * subMultiplier)
            globalStep = (globalStep + 1) % maxSteps

            if (subIndex >= subMultiplier) {
                subIndex = 0
                beat++
                if (beat > numerator) {
                    beat = 1
                    bar++
                    globalStep = 0
                    // Check Speed Trainer increment
                    if (currentState.speedTrainer.enabled && (bar - 1) % currentState.speedTrainer.barsInterval == 0) {
                        val nextBpm = (currentState.bpm + currentState.speedTrainer.bpmIncrement)
                            .coerceAtMost(currentState.speedTrainer.targetBpm)
                        if (nextBpm != currentState.bpm) {
                            _state.update { it.copy(bpm = nextBpm) }
                        }
                    }
                }
            }

            val sleepNanos = targetNextTime - System.nanoTime()
            if (sleepNanos > 0) {
                val sleepMillis = sleepNanos / 1_000_000L
                val sleepRemainderNanos = (sleepNanos % 1_000_000L).toInt()
                try {
                    Thread.sleep(sleepMillis, sleepRemainderNanos)
                } catch (e: InterruptedException) {
                    break
                }
            }
        }
    }
}
