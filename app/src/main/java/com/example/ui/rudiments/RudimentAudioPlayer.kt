package com.example.ui.rudiments

import com.example.audio.DrumInstrument
import com.example.audio.DrumSoundEngine
import com.example.model.Rudiment
import com.example.model.RudimentNote
import com.example.model.StickingHand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RudimentPlayerState(
    val isPlaying: Boolean = false,
    val bpm: Int = 80,
    val currentNoteIndex: Int = -1,
    val currentLoopCount: Int = 0,
    val metronomeClickEnabled: Boolean = true,
    val loopModeEnabled: Boolean = true,
    val speedTrainerEnabled: Boolean = false,
    val speedTrainerIncrement: Int = 2,
    val speedTrainerIntervalLoops: Int = 4,
    val speedTrainerTargetBpm: Int = 140,
    val timerRemainingSeconds: Int = 120, // 2-min speed drill
    val timerEnabled: Boolean = false,
    val timerCompleted: Boolean = false
) {
    val formattedTimer: String
        get() {
            val m = timerRemainingSeconds / 60
            val s = timerRemainingSeconds % 60
            return String.format("%02d:%02d", m, s)
        }
}

class RudimentAudioPlayer(
    private val soundEngine: DrumSoundEngine,
    private val scope: CoroutineScope
) {
    private val _state = MutableStateFlow(RudimentPlayerState())
    val state: StateFlow<RudimentPlayerState> = _state.asStateFlow()

    private var activeRudiment: Rudiment? = null
    private var playbackJob: Job? = null
    private var timerJob: Job? = null

    fun loadRudiment(rudiment: Rudiment) {
        val wasPlaying = _state.value.isPlaying
        stop()
        activeRudiment = rudiment
        _state.update {
            it.copy(
                bpm = rudiment.defaultBpm,
                currentNoteIndex = -1,
                currentLoopCount = 0
            )
        }
        if (wasPlaying) {
            play()
        }
    }

    fun setBpm(bpm: Int) {
        val clamped = bpm.coerceIn(30, 240)
        _state.update { it.copy(bpm = clamped) }
    }

    fun adjustBpm(delta: Int) {
        setBpm(_state.value.bpm + delta)
    }

    fun toggleMetronomeClick() {
        _state.update { it.copy(metronomeClickEnabled = !it.metronomeClickEnabled) }
    }

    fun toggleLoopMode() {
        _state.update { it.copy(loopModeEnabled = !it.loopModeEnabled) }
    }

    fun toggleSpeedTrainer() {
        _state.update { it.copy(speedTrainerEnabled = !it.speedTrainerEnabled) }
    }

    fun setTimerEnabled(enabled: Boolean, durationSeconds: Int = 120) {
        _state.update {
            it.copy(
                timerEnabled = enabled,
                timerRemainingSeconds = durationSeconds,
                timerCompleted = false
            )
        }
    }

    fun togglePlay() {
        if (_state.value.isPlaying) {
            stop()
        } else {
            play()
        }
    }

    fun play() {
        val rudiment = activeRudiment ?: return
        if (_state.value.isPlaying) return

        _state.update {
            it.copy(
                isPlaying = true,
                currentNoteIndex = 0,
                currentLoopCount = 0,
                timerCompleted = false
            )
        }

        startTimerIfNeeded()

        playbackJob = scope.launch(Dispatchers.Default) {
            runPlaybackLoop(rudiment)
        }
    }

    fun stop() {
        playbackJob?.cancel()
        playbackJob = null
        timerJob?.cancel()
        timerJob = null

        _state.update {
            it.copy(
                isPlaying = false,
                currentNoteIndex = -1
            )
        }
    }

    private fun startTimerIfNeeded() {
        if (!_state.value.timerEnabled) return
        timerJob?.cancel()
        timerJob = scope.launch(Dispatchers.Default) {
            while (_state.value.isPlaying && _state.value.timerEnabled) {
                delay(1000)
                val remaining = _state.value.timerRemainingSeconds
                if (remaining > 1) {
                    _state.update { it.copy(timerRemainingSeconds = remaining - 1) }
                } else {
                    _state.update { it.copy(timerRemainingSeconds = 0, timerCompleted = true) }
                    soundEngine.play(DrumInstrument.CRASH, volume = 0.9f)
                    stop()
                    break
                }
            }
        }
    }

    private suspend fun runPlaybackLoop(rudiment: Rudiment) {
        val notes = rudiment.notes
        if (notes.isEmpty()) return

        var loopCount = 0

        while (_state.value.isPlaying) {
            val currentBpm = _state.value.bpm
            // Quarter note duration in milliseconds: 60,000 / BPM
            val quarterMs = 60000.0 / currentBpm.toDouble()

            for (i in notes.indices) {
                if (!_state.value.isPlaying) break

                val note = notes[i]
                _state.update { it.copy(currentNoteIndex = i) }

                // Play metronome click on beat 1 / quarter notes
                val isQuarterBeat = note.syllable.toIntOrNull() != null
                if (_state.value.metronomeClickEnabled && isQuarterBeat) {
                    val clickInstrument = if (note.syllable == "1") DrumInstrument.METRONOME_HIGH else DrumInstrument.METRONOME_LOW
                    soundEngine.play(clickInstrument, volume = 0.65f)
                }

                // Play acoustic snare hit with dynamic velocity
                if (note.isGraceNote) {
                    // Flam or drag grace note (soft tap right before the primary stroke)
                    soundEngine.play(DrumInstrument.SNARE, volume = 0.38f)
                    delay(22) // short grace note offset
                } else {
                    val volume = if (note.isAccented) 0.98f else 0.52f
                    soundEngine.play(DrumInstrument.SNARE, volume = volume)

                    // Note duration in ms based on its fraction of a quarter note
                    // standard durationFraction is e.g. 0.25 for 16th note (relative to whole bar, i.e. 1 quarter = 1 beat)
                    val noteDurationMs = (quarterMs * (note.durationFraction * 4.0)).coerceAtLeast(35.0).toLong()
                    delay(noteDurationMs)
                }
            }

            loopCount++
            _state.update { it.copy(currentLoopCount = loopCount) }

            // Speed trainer: increment BPM every N loops
            if (_state.value.speedTrainerEnabled && loopCount % _state.value.speedTrainerIntervalLoops == 0) {
                val nextBpm = (_state.value.bpm + _state.value.speedTrainerIncrement)
                    .coerceAtMost(_state.value.speedTrainerTargetBpm)
                _state.update { it.copy(bpm = nextBpm) }
            }

            if (!_state.value.loopModeEnabled) {
                stop()
                break
            }
        }
    }
}
