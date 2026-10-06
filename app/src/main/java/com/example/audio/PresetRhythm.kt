package com.example.audio

import com.example.model.ScheduledHit
import com.example.model.StickingHand

data class PresetRhythm(
    val id: String,
    val name: String,
    val genre: String,
    val description: String,
    val timeSignature: TimeSignature,
    val defaultBpm: Int,
    val stepsPerBar: Int = 16,
    val hits: List<ScheduledHit>
)

object PresetRhythmCatalog {
    val allRhythms: List<PresetRhythm> by lazy {
        listOf(
            // 1. Rock / Pop Standard 8th
            PresetRhythm(
                id = "rock_standard",
                name = "Classic 8th Rock",
                genre = "Rock / Pop",
                description = "Solid 4/4 driving backbeat with kick on 1 & 3, snare on 2 & 4, and steady 8th-note hi-hats.",
                timeSignature = TimeSignature.TS_4_4,
                defaultBpm = 100,
                stepsPerBar = 16,
                hits = buildHits {
                    // Hi-hat on all 8ths (steps 0, 2, 4, 6, 8, 10, 12, 14)
                    for (s in 0 until 16 step 2) hit(s, DrumInstrument.HIHAT_CLOSED)
                    // Kick on 1 and 3
                    hit(0, DrumInstrument.KICK, isAccented = true)
                    hit(8, DrumInstrument.KICK)
                    // Snare on 2 and 4
                    hit(4, DrumInstrument.SNARE, isAccented = true)
                    hit(12, DrumInstrument.SNARE, isAccented = true)
                }
            ),

            // 2. Four On The Floor (Disco / Dance)
            PresetRhythm(
                id = "four_on_floor",
                name = "Four-on-the-Floor Pop",
                genre = "Dance / Pop",
                description = "Pumping quarter-note bass drums with upbeat open hi-hat sizzles and crisp backbeat snare.",
                timeSignature = TimeSignature.TS_4_4,
                defaultBpm = 124,
                stepsPerBar = 16,
                hits = buildHits {
                    for (s in 0 until 16 step 4) hit(s, DrumInstrument.KICK, isAccented = true)
                    for (s in 0 until 16 step 4) hit(s, DrumInstrument.HIHAT_CLOSED)
                    for (s in 2 until 16 step 4) hit(s, DrumInstrument.HIHAT_OPEN, isAccented = true)
                    hit(4, DrumInstrument.SNARE, isAccented = true)
                    hit(12, DrumInstrument.SNARE, isAccented = true)
                }
            ),

            // 3. Half-Time Heavy Rock / Hip-Hop
            PresetRhythm(
                id = "half_time_rock",
                name = "Half-Time Heavy Groove",
                genre = "Rock / Hip-Hop",
                description = "Spacious half-time groove where the snare hits only on beat 3 (step 8), creating massive weight.",
                timeSignature = TimeSignature.TS_4_4,
                defaultBpm = 82,
                stepsPerBar = 16,
                hits = buildHits {
                    for (s in 0 until 16 step 2) hit(s, DrumInstrument.HIHAT_CLOSED)
                    hit(0, DrumInstrument.KICK, isAccented = true)
                    hit(5, DrumInstrument.KICK)
                    hit(6, DrumInstrument.KICK)
                    hit(8, DrumInstrument.SNARE, isAccented = true)
                    hit(13, DrumInstrument.KICK)
                }
            ),

            // 4. Funk 16th Syncopated
            PresetRhythm(
                id = "funk_16th",
                name = "16th-Note Funk Pocket",
                genre = "Funk / R&B",
                description = "Intricate 16th-note hi-hat groove with syncopated kick and ghosted snare accents.",
                timeSignature = TimeSignature.TS_4_4,
                defaultBpm = 94,
                stepsPerBar = 16,
                hits = buildHits {
                    for (s in 0 until 16) hit(s, DrumInstrument.HIHAT_CLOSED)
                    hit(0, DrumInstrument.KICK, isAccented = true)
                    hit(6, DrumInstrument.KICK)
                    hit(10, DrumInstrument.KICK)
                    hit(4, DrumInstrument.SNARE, isAccented = true)
                    hit(12, DrumInstrument.SNARE, isAccented = true)
                    hit(15, DrumInstrument.SNARE) // ghost snare pickup
                }
            ),

            // 5. Classic Jazz Swing
            PresetRhythm(
                id = "jazz_swing",
                name = "Classic Jazz Swing",
                genre = "Jazz",
                description = "Authentic jazz ride cymbal 'spang-a-lang' pattern with foot hi-hat snapping on beats 2 and 4.",
                timeSignature = TimeSignature.TS_4_4,
                defaultBpm = 135,
                stepsPerBar = 12, // triplet base
                hits = buildHits {
                    // Triplets: 0, 3, 6, 9 are beats 1, 2, 3, 4
                    hit(0, DrumInstrument.RIDE, isAccented = true)
                    hit(3, DrumInstrument.RIDE)
                    hit(5, DrumInstrument.RIDE) // swing skip
                    hit(6, DrumInstrument.RIDE, isAccented = true)
                    hit(9, DrumInstrument.RIDE)
                    hit(11, DrumInstrument.RIDE) // swing skip

                    // Foot hi-hat on 2 and 4
                    hit(3, DrumInstrument.HIHAT_CLOSED, isAccented = true)
                    hit(9, DrumInstrument.HIHAT_CLOSED, isAccented = true)

                    // Feathered kick on 1 and 3
                    hit(0, DrumInstrument.KICK)
                    hit(6, DrumInstrument.KICK)
                }
            ),

            // 6. Slow Blues 12/8 Shuffle
            PresetRhythm(
                id = "blues_12_8",
                name = "Slow Blues 12/8 Shuffle",
                genre = "Blues",
                description = "Deep 12/8 shuffle groove with rolling triplets, heavy backbeat on beats 4 and 10.",
                timeSignature = TimeSignature.TS_12_8,
                defaultBpm = 65,
                stepsPerBar = 12,
                hits = buildHits {
                    for (s in 0 until 12) hit(s, DrumInstrument.RIDE)
                    hit(0, DrumInstrument.KICK, isAccented = true)
                    hit(3, DrumInstrument.SNARE, isAccented = true)
                    hit(6, DrumInstrument.KICK)
                    hit(8, DrumInstrument.KICK)
                    hit(9, DrumInstrument.SNARE, isAccented = true)
                }
            ),

            // 7. Bossa Nova / Latin Clave
            PresetRhythm(
                id = "bossa_nova",
                name = "Bossa Nova & Clave",
                genre = "Latin / World",
                description = "Brazilian bossa nova rhythm with syncopated cross-stick snare and gentle repeating bass pulse.",
                timeSignature = TimeSignature.TS_4_4,
                defaultBpm = 118,
                stepsPerBar = 16,
                hits = buildHits {
                    for (s in 0 until 16 step 2) hit(s, DrumInstrument.HIHAT_CLOSED)
                    // Bossa kick pulse (1, and of 2, 3, and of 4)
                    hit(0, DrumInstrument.KICK)
                    hit(3, DrumInstrument.KICK)
                    hit(8, DrumInstrument.KICK)
                    hit(11, DrumInstrument.KICK)
                    // Clave rim / snare (0, 6, 10, 12)
                    hit(0, DrumInstrument.SNARE, isAccented = true)
                    hit(6, DrumInstrument.SNARE)
                    hit(10, DrumInstrument.SNARE)
                    hit(12, DrumInstrument.SNARE)
                }
            ),

            // 8. Afro-Cuban 6/8 Bembe
            PresetRhythm(
                id = "afro_cuban_6_8",
                name = "Afro-Cuban 6/8 Bembe",
                genre = "Latin / World",
                description = "Poly-rhythmic 6/8 bell pattern accompanied by low tom pulse and high tom accents.",
                timeSignature = TimeSignature.TS_6_8,
                defaultBpm = 120,
                stepsPerBar = 12,
                hits = buildHits {
                    // Bembe bell: 0, 2, 4, 5, 7, 9, 11
                    listOf(0, 2, 4, 5, 7, 9, 11).forEach { s ->
                        hit(s, DrumInstrument.RIDE, isAccented = (s == 0 || s == 7))
                    }
                    hit(0, DrumInstrument.KICK, isAccented = true)
                    hit(6, DrumInstrument.TOM_FLOOR)
                    hit(4, DrumInstrument.TOM_HIGH)
                    hit(10, DrumInstrument.TOM_HIGH)
                }
            ),

            // 9. Progressive 7/8 Meter
            PresetRhythm(
                id = "prog_7_8",
                name = "Progressive 7/8 Pulse (2+2+3)",
                genre = "Odd Time / Prog",
                description = "Energetic 7/8 time groove subdivided into 2 + 2 + 3. Driving hi-hat and displaced snare landings.",
                timeSignature = TimeSignature.TS_7_8,
                defaultBpm = 140,
                stepsPerBar = 14,
                hits = buildHits {
                    // 14 steps (2 steps per 8th note)
                    for (s in 0 until 14 step 2) hit(s, DrumInstrument.HIHAT_CLOSED)
                    hit(0, DrumInstrument.KICK, isAccented = true)
                    hit(4, DrumInstrument.SNARE, isAccented = true)
                    hit(8, DrumInstrument.KICK)
                    hit(10, DrumInstrument.SNARE)
                }
            ),

            // 10. Dave Brubeck 5/4 Swing
            PresetRhythm(
                id = "brubeck_5_4",
                name = "Dave Brubeck 5/4 Groove (3+2)",
                genre = "Odd Time / Jazz",
                description = "Famous 'Take Five' 5/4 groove: 3 beats swing + 2 beats resolution. Master limb independence in 5.",
                timeSignature = TimeSignature.TS_5_4,
                defaultBpm = 145,
                stepsPerBar = 10,
                hits = buildHits {
                    for (s in 0 until 10 step 2) hit(s, DrumInstrument.RIDE)
                    hit(0, DrumInstrument.KICK, isAccented = true)
                    hit(4, DrumInstrument.SNARE, isAccented = true)
                    hit(6, DrumInstrument.SNARE)
                    hit(2, DrumInstrument.HIHAT_CLOSED)
                    hit(6, DrumInstrument.HIHAT_CLOSED)
                }
            ),

            // 11. Waltz / 3/4 Ballad
            PresetRhythm(
                id = "waltz_3_4",
                name = "Waltz & 3/4 Ballad",
                genre = "Ballad / Waltz",
                description = "Expressive 3/4 groove (Boom-Chick-Chick). Bass drum on beat 1, delicate snare brushes on 2 & 3.",
                timeSignature = TimeSignature.TS_3_4,
                defaultBpm = 95,
                stepsPerBar = 12,
                hits = buildHits {
                    for (s in 0 until 12 step 2) hit(s, DrumInstrument.HIHAT_CLOSED)
                    hit(0, DrumInstrument.KICK, isAccented = true)
                    hit(4, DrumInstrument.SNARE)
                    hit(8, DrumInstrument.SNARE)
                }
            ),

            // 12. Paradiddle Groove Orchestration
            PresetRhythm(
                id = "paradiddle_groove",
                name = "Paradiddle Funk Groove",
                genre = "Rudiments",
                description = "Single paradiddle (R L R R L R L L) orchestrated: R on Hi-Hat/Ride, L on Snare, Kick locked to accents.",
                timeSignature = TimeSignature.TS_4_4,
                defaultBpm = 110,
                stepsPerBar = 16,
                hits = buildHits {
                    // Sticking: R L R R  L R L L  R L R R  L R L L
                    val rSteps = listOf(0, 2, 3, 5, 8, 10, 11, 13)
                    val lSteps = listOf(1, 4, 6, 7, 9, 12, 14, 15)
                    rSteps.forEach { s -> hit(s, DrumInstrument.HIHAT_CLOSED, StickingHand.RIGHT) }
                    lSteps.forEach { s -> hit(s, DrumInstrument.SNARE, StickingHand.LEFT) }
                    hit(0, DrumInstrument.KICK, StickingHand.KICK, isAccented = true)
                    hit(4, DrumInstrument.SNARE, StickingHand.LEFT, isAccented = true)
                    hit(8, DrumInstrument.KICK, StickingHand.KICK)
                    hit(12, DrumInstrument.SNARE, StickingHand.LEFT, isAccented = true)
                }
            )
        )
    }

    private fun buildHits(builder: HitBuilder.() -> Unit): List<ScheduledHit> {
        val b = HitBuilder()
        b.builder()
        return b.hits
    }

    private class HitBuilder {
        val hits = mutableListOf<ScheduledHit>()
        fun hit(
            step: Int,
            instrument: DrumInstrument,
            sticking: StickingHand? = null,
            isAccented: Boolean = false,
            bar: Int = 0
        ) {
            hits.add(ScheduledHit(bar, step, instrument, sticking, isAccented))
        }
    }
}
