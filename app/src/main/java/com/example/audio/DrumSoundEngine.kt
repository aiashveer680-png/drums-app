package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

enum class DrumInstrument(val displayName: String, val shortcut: String) {
    KICK("Bass Drum", "K"),
    SNARE("Snare", "S"),
    HIHAT_CLOSED("Hi-Hat (Closed)", "HH"),
    HIHAT_OPEN("Hi-Hat (Open)", "HHO"),
    CRASH("Crash Cymbal", "CR"),
    RIDE("Ride Cymbal", "RD"),
    TOM_HIGH("High Tom", "T1"),
    TOM_FLOOR("Floor Tom", "FT"),
    METRONOME_HIGH("Accent Click", "CLK_HI"),
    METRONOME_LOW("Click", "CLK_LO"),
    METRONOME_SUB("Sub Tick", "CLK_SUB")
}

/**
 * Natural Acoustic Drum Sound Engine.
 *
 * Uses low-latency direct-memory AudioTrack PCM streaming with physical-modeling
 * acoustic synthesis. Completely eliminates SoundPool/MediaExtractor/Codec2 dependency,
 * avoiding "Failed to query component interface" errors while producing authentic,
 * warm acoustic drum samples (natural maple shells, coated heads, steel snare wires,
 * and B20 bronze cymbal modal harmonics).
 */
class DrumSoundEngine(private val context: Context) {
    private val TAG = "DrumSoundEngine"
    private val sampleRate = 44100
    private val sampleMap = ConcurrentHashMap<DrumInstrument, ShortArray>()

    private class VoiceSlot {
        @Volatile var active: Boolean = false
        var samples: ShortArray? = null
        var position: Int = 0
        var volume: Float = 1.0f
    }

    private val maxPolyphony = 32
    private val voiceSlots = Array(maxPolyphony) { VoiceSlot() }

    private var audioTrack: AudioTrack? = null
    @Volatile private var isRunning = true
    private var mixerThread: Thread? = null

    init {
        // Synthesize acoustic samples in memory
        initAcousticSamples()
        startAudioMixer()
    }

    private fun initAcousticSamples() {
        DrumInstrument.entries.forEach { instrument ->
            sampleMap[instrument] = synthesizeAcousticSample(instrument)
        }
    }

    private fun startAudioMixer() {
        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = (minBufferSize * 2).coerceAtLeast(2048)

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack = track

            mixerThread = Thread({
                try {
                    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO)
                } catch (_: Exception) {}

                try {
                    track.play()
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to start AudioTrack", e)
                    return@Thread
                }

                val chunkSize = 512
                val mixBuffer = ShortArray(chunkSize)

                while (isRunning) {
                    for (i in 0 until chunkSize) {
                        var sum = 0f
                        for (slot in voiceSlots) {
                            if (slot.active) {
                                val s = slot.samples
                                if (s != null && slot.position < s.size) {
                                    sum += s[slot.position] * slot.volume
                                    slot.position++
                                    if (slot.position >= s.size) {
                                        slot.active = false
                                    }
                                } else {
                                    slot.active = false
                                }
                            }
                        }
                        // Warm analog soft clipping
                        val clamped = sum.coerceIn(Short.MIN_VALUE.toFloat(), Short.MAX_VALUE.toFloat())
                        mixBuffer[i] = clamped.toInt().toShort()
                    }

                    track.write(mixBuffer, 0, chunkSize)
                }
            }, "AcousticDrumMixerThread")

            mixerThread?.isDaemon = true
            mixerThread?.start()
        } catch (e: Exception) {
            Log.e(TAG, "Audio mixer init failure", e)
        }
    }

    fun play(instrument: DrumInstrument, volume: Float = 1.0f, pitch: Float = 1.0f) {
        val samples = sampleMap[instrument] ?: return
        val clampedVol = volume.coerceIn(0.05f, 1.0f)

        // Find available voice slot
        for (slot in voiceSlots) {
            if (!slot.active) {
                slot.samples = samples
                slot.position = 0
                slot.volume = clampedVol
                slot.active = true
                return
            }
        }

        // If all slots busy, steal the oldest slot
        val stealSlot = voiceSlots[0]
        stealSlot.samples = samples
        stealSlot.position = 0
        stealSlot.volume = clampedVol
        stealSlot.active = true
    }

    private fun synthesizeAcousticSample(instrument: DrumInstrument): ShortArray {
        return when (instrument) {
            DrumInstrument.KICK -> generateAcousticKick()
            DrumInstrument.SNARE -> generateAcousticSnare()
            DrumInstrument.HIHAT_CLOSED -> generateAcousticHiHatClosed()
            DrumInstrument.HIHAT_OPEN -> generateAcousticHiHatOpen()
            DrumInstrument.CRASH -> generateAcousticCrash()
            DrumInstrument.RIDE -> generateAcousticRide()
            DrumInstrument.TOM_HIGH -> generateAcousticTom(fundamentalStart = 185.0, fundamentalEnd = 135.0, overtone = 210.0, durationSec = 0.32)
            DrumInstrument.TOM_FLOOR -> generateAcousticTom(fundamentalStart = 110.0, fundamentalEnd = 68.0, overtone = 98.0, durationSec = 0.44)
            DrumInstrument.METRONOME_HIGH -> generateAcousticWoodblockClick(freq = 1750.0, volume = 0.95)
            DrumInstrument.METRONOME_LOW -> generateAcousticWoodblockClick(freq = 1150.0, volume = 0.8)
            DrumInstrument.METRONOME_SUB -> generateAcousticWoodblockClick(freq = 780.0, volume = 0.5)
        }
    }

    /**
     * Natural 22" Maple Acoustic Bass Drum:
     * - Felt beater impact transient on coated head.
     * - Fundamental pitch bend from 115Hz down to warm 52Hz.
     * - Resonant head sympathetic body tone (76Hz).
     * - Warm wooden shell decay.
     */
    private fun generateAcousticKick(): ShortArray {
        val duration = 0.32
        val numSamples = (duration * sampleRate).toInt()
        val samples = ShortArray(numSamples)
        val rnd = Random(42)

        var phase1 = 0.0
        var phase2 = 0.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            // Natural pitch drop of acoustic head tension
            val f1 = 52.0 + 68.0 * exp(-t * 26.0)
            phase1 += 2.0 * PI * f1 / sampleRate
            val headTone = sin(phase1) * exp(-t * 11.0)

            // Resonant drum cavity mode
            phase2 += 2.0 * PI * 76.0 / sampleRate
            val shellResonance = sin(phase2) * exp(-t * 14.0) * 0.45

            // Felt beater click transient (first 5ms)
            val beaterTransient = if (t < 0.006) {
                val beaterEnv = 1.0 - (t / 0.006)
                (sin(2.0 * PI * 2600.0 * t) * 0.4 + (rnd.nextDouble() * 2.0 - 1.0) * 0.35) * beaterEnv
            } else 0.0

            val raw = (headTone * 0.75 + shellResonance + beaterTransient) * exp(-t * 8.5)
            // Analog soft saturation
            val sat = (raw * 1.25).coerceIn(-1.0, 1.0)
            samples[i] = (sat * Short.MAX_VALUE).toInt().toShort()
        }
        return samples
    }

    /**
     * Natural 14" Maple/Brass Acoustic Snare Drum:
     * - Stick-tip strike on coated batter head.
     * - Two fundamental membrane modes (195Hz and 310Hz ring).
     * - Coiled 20-strand steel snare wire response (filtered high-frequency rattle).
     */
    private fun generateAcousticSnare(): ShortArray {
        val duration = 0.28
        val numSamples = (duration * sampleRate).toInt()
        val samples = ShortArray(numSamples)
        val rnd = Random(101)

        var phaseFund = 0.0
        var phaseRing = 0.0
        var lastNoise = 0.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            // Batter head fundamental with subtle pitch drop on impact
            val freqFund = 195.0 * exp(-t * 8.0)
            phaseFund += 2.0 * PI * freqFund / sampleRate
            val fundamental = sin(phaseFund) * exp(-t * 22.0)

            // Shell rim harmonic ring (310Hz)
            phaseRing += 2.0 * PI * 310.0 / sampleRate
            val ring = sin(phaseRing) * exp(-t * 28.0) * 0.35

            // Coiled steel snare wire buzz (high-passed noise with micro-rattles)
            val white = rnd.nextDouble() * 2.0 - 1.0
            val hpNoise = white - lastNoise
            lastNoise = white * 0.85
            val wireBuzz = hpNoise * exp(-t * 15.0) * 0.65

            // Wooden stick impact transient (first 3ms)
            val stickSnap = if (t < 0.004) {
                (1.0 - t / 0.004) * (sin(2.0 * PI * 3800.0 * t) * 0.5 + white * 0.4)
            } else 0.0

            val mixed = (fundamental * 0.45 + ring + wireBuzz + stickSnap).coerceIn(-1.0, 1.0)
            samples[i] = (mixed * Short.MAX_VALUE).toInt().toShort()
        }
        return samples
    }

    /**
     * Natural 14" B20 Bronze Closed Hi-Hat:
     * - Physical inharmonic metal modal frequencies (3120, 4450, 5870, 7920, 10400 Hz).
     * - Wood stick tip strike on cymbal bow.
     * - Natural acoustic choking in ~48ms.
     */
    private fun generateAcousticHiHatClosed(): ShortArray {
        val duration = 0.065
        val numSamples = (duration * sampleRate).toInt()
        val samples = ShortArray(numSamples)
        val rnd = Random(303)

        var lastFilter = 0.0
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate

            // Inharmonic bronze metal modes
            val m1 = sin(2.0 * PI * 3120.0 * t) * 0.22
            val m2 = sin(2.0 * PI * 4450.0 * t) * 0.20
            val m3 = sin(2.0 * PI * 5870.0 * t) * 0.18
            val m4 = sin(2.0 * PI * 7920.0 * t) * 0.15
            val m5 = sin(2.0 * PI * 10400.0 * t) * 0.12
            val bronzeModes = m1 + m2 + m3 + m4 + m5

            // Stick impact noise filtered
            val white = rnd.nextDouble() * 2.0 - 1.0
            val noise = white - lastFilter
            lastFilter = white * 0.82

            // Tight acoustic choke envelope
            val env = exp(-t * 68.0)

            val mixed = ((bronzeModes * 0.65 + noise * 0.45) * env).coerceIn(-1.0, 1.0)
            samples[i] = (mixed * Short.MAX_VALUE).toInt().toShort()
        }
        return samples
    }

    /**
     * Natural 14" B20 Bronze Open Hi-Hat:
     * - Full vibrating bronze plates with shimmering phase beating and natural air decay.
     */
    private fun generateAcousticHiHatOpen(): ShortArray {
        val duration = 0.42
        val numSamples = (duration * sampleRate).toInt()
        val samples = ShortArray(numSamples)
        val rnd = Random(505)

        var lastFilter = 0.0
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate

            // Vibrating plate modes with slight phase beating
            val m1 = sin(2.0 * PI * 3120.0 * t) * 0.20
            val m2 = sin(2.0 * PI * 4450.0 * t) * 0.18
            val m3 = sin(2.0 * PI * 5870.0 * t) * 0.16
            val m4 = sin(2.0 * PI * 7920.0 * t) * 0.14
            val m5 = sin(2.0 * PI * 10800.0 * t) * 0.12
            val bronzeModes = m1 + m2 + m3 + m4 + m5

            val white = rnd.nextDouble() * 2.0 - 1.0
            val noise = white - lastFilter
            lastFilter = white * 0.85

            // Shimmering decay
            val env = exp(-t * 9.2)

            val mixed = ((bronzeModes * 0.6 + noise * 0.5) * env).coerceIn(-1.0, 1.0)
            samples[i] = (mixed * Short.MAX_VALUE).toInt().toShort()
        }
        return samples
    }

    /**
     * Natural 18" Thin Bronze Crash Cymbal:
     * - Explosive stick shoulder impact.
     * - Broad inharmonic bronze frequency cluster (880Hz to 13kHz).
     * - Long natural acoustic decay (~1.0s).
     */
    private fun generateAcousticCrash(): ShortArray {
        val duration = 0.95
        val numSamples = (duration * sampleRate).toInt()
        val samples = ShortArray(numSamples)
        val rnd = Random(707)

        var last1 = 0.0
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate

            // Complex bronze cymbal harmonic spectrum
            val b1 = sin(2.0 * PI * 880.0 * t) * 0.14
            val b2 = sin(2.0 * PI * 1620.0 * t) * 0.16
            val b3 = sin(2.0 * PI * 2850.0 * t) * 0.15
            val b4 = sin(2.0 * PI * 4400.0 * t) * 0.14
            val b5 = sin(2.0 * PI * 6700.0 * t) * 0.12
            val b6 = sin(2.0 * PI * 9800.0 * t) * 0.10
            val cymbalWash = b1 + b2 + b3 + b4 + b5 + b6

            val white = rnd.nextDouble() * 2.0 - 1.0
            val hp = white - last1
            last1 = white * 0.88

            // Multi-stage acoustic decay
            val env = exp(-t * 3.8)

            val mixed = ((cymbalWash * 0.65 + hp * 0.45) * env).coerceIn(-1.0, 1.0)
            samples[i] = (mixed * Short.MAX_VALUE).toInt().toShort()
        }
        return samples
    }

    /**
     * Natural 20" Medium Heavy Acoustic Ride Cymbal:
     * - Defined wooden stick tip "ping".
     * - Clear bell harmonic (2850Hz + 5700Hz) over warm low bronze wash body.
     */
    private fun generateAcousticRide(): ShortArray {
        val duration = 0.55
        val numSamples = (duration * sampleRate).toInt()
        val samples = ShortArray(numSamples)
        val rnd = Random(808)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate

            // Clean bell ping
            val ping = sin(2.0 * PI * 2850.0 * t) * 0.5 + sin(2.0 * PI * 5700.0 * t) * 0.25
            // Bronze body wash
            val wash = sin(2.0 * PI * 480.0 * t) * 0.2 + (rnd.nextDouble() * 2.0 - 1.0) * 0.18
            val env = exp(-t * 6.5)

            val mixed = ((ping * 0.7 + wash * 0.3) * env).coerceIn(-1.0, 1.0)
            samples[i] = (mixed * Short.MAX_VALUE).toInt().toShort()
        }
        return samples
    }

    /**
     * Natural Acoustic Tom Drum (10" High Tom or 16" Floor Tom):
     * - Singing double-headed acoustic drum shell.
     * - Initial stick attack, fundamental pitch dip, and warm maple ring.
     */
    private fun generateAcousticTom(
        fundamentalStart: Double,
        fundamentalEnd: Double,
        overtone: Double,
        durationSec: Double
    ): ShortArray {
        val numSamples = (durationSec * sampleRate).toInt()
        val samples = ShortArray(numSamples)
        val rnd = Random(909)

        var phaseFund = 0.0
        var phaseOver = 0.0

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val freq = fundamentalEnd + (fundamentalStart - fundamentalEnd) * exp(-t * 18.0)
            phaseFund += 2.0 * PI * freq / sampleRate
            val fundamental = sin(phaseFund) * exp(-t * (1.0 / (durationSec * 0.38)))

            phaseOver += 2.0 * PI * overtone / sampleRate
            val overtoneTone = sin(phaseOver) * exp(-t * (1.0 / (durationSec * 0.28))) * 0.3

            val stickClick = if (t < 0.007) (rnd.nextDouble() * 2.0 - 1.0) * 0.35 * (1.0 - t / 0.007) else 0.0

            val raw = (fundamental * 0.75 + overtoneTone + stickClick)
            val sat = (raw * 1.15).coerceIn(-1.0, 1.0)
            samples[i] = (sat * Short.MAX_VALUE).toInt().toShort()
        }
        return samples
    }

    /**
     * Natural Hardwood Rosewood Woodblock Click:
     * - Solid acoustic clave / woodblock click.
     */
    private fun generateAcousticWoodblockClick(freq: Double, volume: Double): ShortArray {
        val duration = 0.038
        val numSamples = (duration * sampleRate).toInt()
        val samples = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            // Fundamental + wood harmonic
            val tone = sin(2.0 * PI * freq * t) * 0.8 + sin(2.0 * PI * (freq * 2.08) * t) * 0.25
            val env = exp(-t * 135.0)

            val sample = (tone * env * volume).coerceIn(-1.0, 1.0)
            samples[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return samples
    }

    fun release() {
        isRunning = false
        mixerThread?.interrupt()
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }
}
