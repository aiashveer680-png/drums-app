package com.example.ui.bassletters

import com.example.audio.DrumInstrument
import com.example.audio.DrumSoundEngine
import com.example.data.BassDrumLetterCatalog
import com.example.model.BassDrumAutoAdvanceConfig
import com.example.model.BassDrumLetter
import com.example.model.BassDrumVoiceConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BassDrumPlayerState(
    val isPlaying: Boolean = false,
    val bpm: Int = 70,
    val currentLetter: BassDrumLetter = BassDrumLetterCatalog.allLetters[0],
    val currentStep: Int = -1, // 0..15
    val currentBeat: Int = 0, // 0..3
    val currentBar: Int = 1,
    val voiceConfig: BassDrumVoiceConfig = BassDrumVoiceConfig(),
    val autoAdvance: BassDrumAutoAdvanceConfig = BassDrumAutoAdvanceConfig()
)

class BassDrumLetterAudioPlayer(
    private val soundEngine: DrumSoundEngine,
    private val scope: CoroutineScope
) {
    private val _state = MutableStateFlow(BassDrumPlayerState())
    val state: StateFlow<BassDrumPlayerState> = _state.asStateFlow()

    private var playbackJob: Job? = null

    fun selectLetter(letter: BassDrumLetter) {
        _state.update {
            it.copy(
                currentLetter = letter,
                currentBar = 1
            )
        }
    }

    fun setBpm(newBpm: Int) {
        val clamped = newBpm.coerceIn(40, 160)
        _state.update { it.copy(bpm = clamped) }
    }

    fun adjustBpm(delta: Int) {
        setBpm(_state.value.bpm + delta)
    }

    fun setVoiceConfig(config: BassDrumVoiceConfig) {
        _state.update { it.copy(voiceConfig = config) }
    }

    fun toggleHiHat() {
        _state.update { it.copy(voiceConfig = it.voiceConfig.copy(playHiHat = !it.voiceConfig.playHiHat)) }
    }

    fun toggleSnare() {
        _state.update { it.copy(voiceConfig = it.voiceConfig.copy(playSnare = !it.voiceConfig.playSnare)) }
    }

    fun toggleKick() {
        _state.update { it.copy(voiceConfig = it.voiceConfig.copy(playKick = !it.voiceConfig.playKick)) }
    }

    fun toggleClick() {
        _state.update { it.copy(voiceConfig = it.voiceConfig.copy(playClick = !it.voiceConfig.playClick)) }
    }

    fun setAutoAdvance(enabled: Boolean, barsPerLetter: Int = 4) {
        _state.update {
            it.copy(autoAdvance = BassDrumAutoAdvanceConfig(enabled = enabled, barsPerLetter = barsPerLetter))
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
        if (_state.value.isPlaying) return

        _state.update {
            it.copy(
                isPlaying = true,
                currentStep = 0,
                currentBeat = 0,
                currentBar = 1
            )
        }

        playbackJob = scope.launch(Dispatchers.Default) {
            runAudioLoop()
        }
    }

    fun stop() {
        playbackJob?.cancel()
        playbackJob = null

        _state.update {
            it.copy(
                isPlaying = false,
                currentStep = -1,
                currentBeat = 0
            )
        }
    }

    private suspend fun runAudioLoop() {
        var step = 0
        var barCount = 0

        while (_state.value.isPlaying) {
            val bpm = _state.value.bpm
            // Duration of one 16th note in milliseconds = (60,000 / BPM) / 4
            val stepDurationMs = (60000.0 / bpm.toDouble() / 4.0).toLong().coerceAtLeast(35L)

            val beat = step / 4 // 0..3
            val slot = step % 4 // 0..3 (1, e, &, a)
            val letter = _state.value.currentLetter
            val voices = _state.value.voiceConfig

            _state.update {
                it.copy(
                    currentStep = step,
                    currentBeat = beat
                )
            }

            // 1. Metronome Click
            if (voices.playClick && slot == 0) {
                val isAccent = (beat == 0)
                soundEngine.play(
                    if (isAccent) DrumInstrument.METRONOME_HIGH else DrumInstrument.METRONOME_LOW,
                    volume = if (isAccent) 0.85f else 0.6f
                )
            }

            // 2. Hi-hat eighth notes (slot 0 and slot 2 on each beat)
            val hasHat = (slot == 0 || slot == 2)
            if (voices.playHiHat && hasHat) {
                soundEngine.play(DrumInstrument.HIHAT_CLOSED, volume = if (slot == 0) 0.72f else 0.58f)
            }

            // 3. Snare backbeats on 2 and 4 (beat 1 and beat 3, slot 0)
            val hasSnare = (slot == 0 && (beat == 1 || beat == 3))
            if (voices.playSnare && hasSnare) {
                soundEngine.play(DrumInstrument.SNARE, volume = 0.95f)
            }

            // 4. Bass Drum / Kick
            // Rule from Page 18: Letter A has kick on beats 1 and 3 on slot 0 (beats 2 and 4 open)
            val hasKick = if (letter.letter == "A") {
                slot == 0 && (beat == 0 || beat == 2)
            } else {
                letter.hasKickOnSlot(slot)
            }

            if (voices.playKick && hasKick) {
                soundEngine.play(DrumInstrument.KICK, volume = 0.92f)
            }

            delay(stepDurationMs)

            step++
            if (step >= 16) {
                step = 0
                barCount++
                _state.update { it.copy(currentBar = barCount + 1) }

                // Auto-advance check
                val autoAdv = _state.value.autoAdvance
                if (autoAdv.enabled && barCount >= autoAdv.barsPerLetter) {
                    barCount = 0
                    val currentIndex = BassDrumLetterCatalog.allLetters.indexOfFirst { it.letter == _state.value.currentLetter.letter }
                    val nextIndex = if (currentIndex != -1) (currentIndex + 1) % BassDrumLetterCatalog.allLetters.size else 0
                    val nextLetter = BassDrumLetterCatalog.allLetters[nextIndex]
                    _state.update {
                        it.copy(
                            currentLetter = nextLetter,
                            currentBar = 1
                        )
                    }
                }
            }
        }
    }
}
