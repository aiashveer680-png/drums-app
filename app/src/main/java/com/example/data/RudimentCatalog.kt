package com.example.data

import com.example.model.DifficultyLevel
import com.example.model.Rudiment
import com.example.model.RudimentCategory
import com.example.model.RudimentNote
import com.example.model.RudimentTempoTarget
import com.example.model.StickingHand

object RudimentCatalog {

    val allRudiments: List<Rudiment> = listOf(
        // ================= ROLL RUDIMENTS =================
        Rudiment(
            id = "single_stroke_roll",
            name = "Single Stroke Roll",
            alternativeName = "Rudiment #1",
            category = RudimentCategory.ROLLS,
            difficulty = DifficultyLevel.BEGINNER,
            stickingSequence = "R L R L R L R L",
            timeSignatureString = "4/4",
            defaultBpm = 100,
            notes = listOf(
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.25f, stepIndex = 0, syllable = "1"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.25f, stepIndex = 1, syllable = "e"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.25f, stepIndex = 2, syllable = "&"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.25f, stepIndex = 3, syllable = "a"),
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.25f, stepIndex = 4, syllable = "2"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.25f, stepIndex = 5, syllable = "e"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.25f, stepIndex = 6, syllable = "&"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.25f, stepIndex = 7, syllable = "a"),
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.25f, stepIndex = 8, syllable = "3"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.25f, stepIndex = 9, syllable = "e"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.25f, stepIndex = 10, syllable = "&"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.25f, stepIndex = 11, syllable = "a"),
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.25f, stepIndex = 12, syllable = "4"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.25f, stepIndex = 13, syllable = "e"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.25f, stepIndex = 14, syllable = "&"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.25f, stepIndex = 15, syllable = "a")
            ),
            description = "The foundation of all drumming technique. Alternating right and left single strokes with identical dynamic level and rhythmic precision.",
            technicalBreakdown = "Focus on relaxed wrists and natural fulcrum rebound. Keep both stick heights perfectly matched (e.g. 8 inches for accents, 3 inches for taps). Avoid tension in the forearms as speed increases.",
            musicalApplication = "Used across all music genres for driving fills around the toms, fast snare builds, and continuous hi-hat grooves.",
            famousExample = "John Bonham (Led Zeppelin) in 'Rock and Roll' and Neil Peart's legendary concert drum solos.",
            tempoTarget = RudimentTempoTarget(bronzeBpm = 70, silverBpm = 110, goldBpm = 145, platinumBpm = 180),
            relatedExerciseId = "ex_single_stroke_roll"
        ),

        Rudiment(
            id = "double_stroke_roll",
            name = "Double Stroke Roll",
            alternativeName = "Long Roll / Open Roll",
            category = RudimentCategory.ROLLS,
            difficulty = DifficultyLevel.INTERMEDIATE,
            stickingSequence = "R R L L R R L L",
            timeSignatureString = "4/4",
            defaultBpm = 85,
            notes = listOf(
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.25f, stepIndex = 0, syllable = "1"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.25f, stepIndex = 1, syllable = "e"),
                RudimentNote(StickingHand.LEFT, isAccented = true, durationFraction = 0.25f, stepIndex = 2, syllable = "&"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.25f, stepIndex = 3, syllable = "a"),
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.25f, stepIndex = 4, syllable = "2"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.25f, stepIndex = 5, syllable = "e"),
                RudimentNote(StickingHand.LEFT, isAccented = true, durationFraction = 0.25f, stepIndex = 6, syllable = "&"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.25f, stepIndex = 7, syllable = "a"),
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.25f, stepIndex = 8, syllable = "3"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.25f, stepIndex = 9, syllable = "e"),
                RudimentNote(StickingHand.LEFT, isAccented = true, durationFraction = 0.25f, stepIndex = 10, syllable = "&"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.25f, stepIndex = 11, syllable = "a"),
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.25f, stepIndex = 12, syllable = "4"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.25f, stepIndex = 13, syllable = "e"),
                RudimentNote(StickingHand.LEFT, isAccented = true, durationFraction = 0.25f, stepIndex = 14, syllable = "&"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.25f, stepIndex = 15, syllable = "a")
            ),
            description = "Two consecutive strokes per hand. The master key to fluid, effortless speed on drumheads and cymbals.",
            technicalBreakdown = "Play the first stroke from the wrist, and let the second stroke snap with the back fingers (middle, ring, pinky) utilizing the drumhead rebound. Make the second stroke just as loud as the first.",
            musicalApplication = "Essential for orchestral rolls, marching cadences, smooth tom transitions, and dynamic hi-hat disco & funk patterns.",
            famousExample = "Buddy Rich's lightning-fast orchestral open rolls and Steve Gadd's snare cadence textures.",
            tempoTarget = RudimentTempoTarget(bronzeBpm = 65, silverBpm = 95, goldBpm = 130, platinumBpm = 165),
            relatedExerciseId = "ex_double_stroke_roll"
        ),

        Rudiment(
            id = "five_stroke_roll",
            name = "Five Stroke Roll",
            alternativeName = "Rudiment #7",
            category = RudimentCategory.ROLLS,
            difficulty = DifficultyLevel.INTERMEDIATE,
            stickingSequence = "R R L L R  /  L L R R L",
            timeSignatureString = "2/4",
            defaultBpm = 80,
            notes = listOf(
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.125f, stepIndex = 0, syllable = "1"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.125f, stepIndex = 1, syllable = "e"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.125f, stepIndex = 2, syllable = "&"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.125f, stepIndex = 3, syllable = "a"),
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.5f, stepIndex = 4, syllable = "2"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.125f, stepIndex = 5, syllable = "3"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.125f, stepIndex = 6, syllable = "e"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.125f, stepIndex = 7, syllable = "&"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.125f, stepIndex = 8, syllable = "a"),
                RudimentNote(StickingHand.LEFT, isAccented = true, durationFraction = 0.5f, stepIndex = 9, syllable = "4")
            ),
            description = "Two doubles followed by an accented single release note, alternating lead hands every measure.",
            technicalBreakdown = "Keep the four diddle notes quiet and tight (taps), and give the final release note a solid, confident accent with an upstroke preparation.",
            musicalApplication = "Great for short punchy fills at the end of a phrase, syncopated jazz accents, and funk backbeat setups.",
            famousExample = "Philly Joe Jones and Tony Williams in classic bebop drum breaks.",
            tempoTarget = RudimentTempoTarget(bronzeBpm = 60, silverBpm = 85, goldBpm = 115, platinumBpm = 145)
        ),

        Rudiment(
            id = "six_stroke_roll",
            name = "Six Stroke Roll",
            alternativeName = "Rudiment #8",
            category = RudimentCategory.ROLLS,
            difficulty = DifficultyLevel.INTERMEDIATE,
            stickingSequence = "R L L R R L",
            timeSignatureString = "3/4",
            defaultBpm = 85,
            notes = listOf(
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.166f, stepIndex = 0, syllable = "1"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.166f, stepIndex = 1, syllable = "l"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.166f, stepIndex = 2, syllable = "l"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.166f, stepIndex = 3, syllable = "r"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.166f, stepIndex = 4, syllable = "r"),
                RudimentNote(StickingHand.LEFT, isAccented = true, durationFraction = 0.166f, stepIndex = 5, syllable = "2")
            ),
            description = "Accented single, two inner doubles, and a concluding accented single. One of the most expressive rudiments on the drum set.",
            technicalBreakdown = "Emphasize the contrast between the loud outer singles and the soft inner diddles. Often played as 16th-note sextuplets.",
            musicalApplication = "A staple for gospel chops, modern fusion fills, and R&B grooves. Place the accents on cymbals and the diddles on the snare.",
            famousExample = "Steve Gadd and Dave Weckl signature linear solo phrases.",
            tempoTarget = RudimentTempoTarget(bronzeBpm = 65, silverBpm = 95, goldBpm = 125, platinumBpm = 155)
        ),

        Rudiment(
            id = "nine_stroke_roll",
            name = "Nine Stroke Roll",
            alternativeName = "Rudiment #10",
            category = RudimentCategory.ROLLS,
            difficulty = DifficultyLevel.INTERMEDIATE,
            stickingSequence = "R R L L R R L L R",
            timeSignatureString = "4/4",
            defaultBpm = 75,
            notes = listOf(
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.125f, stepIndex = 0, syllable = "1"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.125f, stepIndex = 1, syllable = "e"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.125f, stepIndex = 2, syllable = "&"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.125f, stepIndex = 3, syllable = "a"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.125f, stepIndex = 4, syllable = "2"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.125f, stepIndex = 5, syllable = "e"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.125f, stepIndex = 6, syllable = "&"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.125f, stepIndex = 7, syllable = "a"),
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.5f, stepIndex = 8, syllable = "3")
            ),
            description = "Four double strokes (8 notes) capped by a strong accent on beat 3.",
            technicalBreakdown = "Breathe naturally through the eight rebound notes. Let the sticks dance off the head with low energy expenditure.",
            musicalApplication = "Used for 2-beat turnaround fills, military cadence transitions, and rock snare build-ups.",
            famousExample = "Mitch Mitchell (Jimi Hendrix Experience) in 'Hey Joe'.",
            tempoTarget = RudimentTempoTarget(bronzeBpm = 60, silverBpm = 85, goldBpm = 115, platinumBpm = 145)
        ),

        // ================= DIDDLE RUDIMENTS =================
        Rudiment(
            id = "single_paradiddle",
            name = "Single Paradiddle",
            alternativeName = "Rudiment #16",
            category = RudimentCategory.DIDDLES,
            difficulty = DifficultyLevel.BEGINNER,
            stickingSequence = "R L R R  L R L L",
            timeSignatureString = "4/4",
            defaultBpm = 90,
            notes = listOf(
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.25f, stepIndex = 0, syllable = "1"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.25f, stepIndex = 1, syllable = "e"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.25f, stepIndex = 2, syllable = "&"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.25f, stepIndex = 3, syllable = "a"),
                RudimentNote(StickingHand.LEFT, isAccented = true, durationFraction = 0.25f, stepIndex = 4, syllable = "2"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.25f, stepIndex = 5, syllable = "e"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.25f, stepIndex = 6, syllable = "&"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.25f, stepIndex = 7, syllable = "a")
            ),
            description = "Two alternating singles followed by a diddle. Automatically switches the leading hand on every half of the pattern.",
            technicalBreakdown = "Apply the Moeller Whip on the first accented note (downstroke), followed by a tap, and an upstroke diddle to set up the opposite hand.",
            musicalApplication = "Splitting the right hand between the hi-hat/ride and the left hand on the snare produces instant syncopated funk and rock grooves.",
            famousExample = "Bernard Purdie, Steve Jordan, and Vinnie Colaiuta funk grooves.",
            tempoTarget = RudimentTempoTarget(bronzeBpm = 65, silverBpm = 95, goldBpm = 130, platinumBpm = 160),
            relatedExerciseId = "ex_paradiddle_fundamental"
        ),

        Rudiment(
            id = "double_paradiddle",
            name = "Double Paradiddle",
            alternativeName = "Rudiment #17",
            category = RudimentCategory.DIDDLES,
            difficulty = DifficultyLevel.INTERMEDIATE,
            stickingSequence = "R L R L R R  L R L R L L",
            timeSignatureString = "6/8",
            defaultBpm = 85,
            notes = listOf(
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.166f, stepIndex = 0, syllable = "1"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.166f, stepIndex = 1, syllable = "2"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.166f, stepIndex = 2, syllable = "3"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.166f, stepIndex = 3, syllable = "4"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.166f, stepIndex = 4, syllable = "5"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.166f, stepIndex = 5, syllable = "6"),
                RudimentNote(StickingHand.LEFT, isAccented = true, durationFraction = 0.166f, stepIndex = 6, syllable = "1"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.166f, stepIndex = 7, syllable = "2"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.166f, stepIndex = 8, syllable = "3"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.166f, stepIndex = 9, syllable = "4"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.166f, stepIndex = 10, syllable = "5"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.166f, stepIndex = 11, syllable = "6")
            ),
            description = "Four alternating singles followed by a diddle. Naturally fits compound meters like 6/8 and 12/8.",
            technicalBreakdown = "Think of it as 'para-para-diddle'. Keep the four singles rolling smoothly and absorb the double on the same hand with fingertip rebound.",
            musicalApplication = "Outstanding for Afro-Cuban 6/8 rhythms, blues shuffles, and driving swing ride cymbal patterns.",
            famousExample = "Art Blakey and Elvin Jones in jazz waltzes and hard bop shuffles.",
            tempoTarget = RudimentTempoTarget(bronzeBpm = 60, silverBpm = 90, goldBpm = 120, platinumBpm = 150)
        ),

        Rudiment(
            id = "triple_paradiddle",
            name = "Triple Paradiddle",
            alternativeName = "Rudiment #18",
            category = RudimentCategory.DIDDLES,
            difficulty = DifficultyLevel.INTERMEDIATE,
            stickingSequence = "R L R L R L R R  L R L R L R L L",
            timeSignatureString = "4/4",
            defaultBpm = 80,
            notes = listOf(
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.125f, stepIndex = 0, syllable = "1"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.125f, stepIndex = 1, syllable = "e"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.125f, stepIndex = 2, syllable = "&"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.125f, stepIndex = 3, syllable = "a"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.125f, stepIndex = 4, syllable = "2"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.125f, stepIndex = 5, syllable = "e"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.125f, stepIndex = 6, syllable = "&"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.125f, stepIndex = 7, syllable = "a"),
                RudimentNote(StickingHand.LEFT, isAccented = true, durationFraction = 0.125f, stepIndex = 8, syllable = "3"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.125f, stepIndex = 9, syllable = "e"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.125f, stepIndex = 10, syllable = "&"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.125f, stepIndex = 11, syllable = "a"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.125f, stepIndex = 12, syllable = "4"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.125f, stepIndex = 13, syllable = "e"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.125f, stepIndex = 14, syllable = "&"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.125f, stepIndex = 15, syllable = "a")
            ),
            description = "Six alternating singles followed by a diddle. Exactly fills one full bar of 4/4 time in 16th notes.",
            technicalBreakdown = "Sustain the alternating single strokes and keep the final double stroke subtle and controlled.",
            musicalApplication = "Creates syncopated phrasing across bar lines, and serves as an extended build-up fill.",
            famousExample = "Gavin Harrison (Porcupine Tree) polyrhythmic displacement drills.",
            tempoTarget = RudimentTempoTarget(bronzeBpm = 60, silverBpm = 85, goldBpm = 115, platinumBpm = 145)
        ),

        Rudiment(
            id = "paradiddle_diddle",
            name = "Paradiddle-Diddle",
            alternativeName = "Rudiment #19",
            category = RudimentCategory.DIDDLES,
            difficulty = DifficultyLevel.INTERMEDIATE,
            stickingSequence = "R L R R L L  R L R R L L",
            timeSignatureString = "6/8",
            defaultBpm = 95,
            notes = listOf(
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.166f, stepIndex = 0, syllable = "1"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.166f, stepIndex = 1, syllable = "2"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.166f, stepIndex = 2, syllable = "3"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.166f, stepIndex = 3, syllable = "4"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.166f, stepIndex = 4, syllable = "5"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.166f, stepIndex = 5, syllable = "6"),
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.166f, stepIndex = 6, syllable = "1"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.166f, stepIndex = 7, syllable = "2"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.166f, stepIndex = 8, syllable = "3"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.166f, stepIndex = 9, syllable = "4"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.166f, stepIndex = 10, syllable = "5"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.166f, stepIndex = 11, syllable = "6")
            ),
            description = "A single paradiddle followed by an extra double stroke (R L R R L L). Because it ends on two lefts, the next group starts on the same hand!",
            technicalBreakdown = "Unlike the single paradiddle, the paradiddle-diddle stays on the right-hand lead. Practice leading with both hands equally.",
            musicalApplication = "One of the most popular rudiments on the modern drum kit. Play the accented R on the ride cymbal or floor tom and diddles on the snare for lightning-fast groove syncopation.",
            famousExample = "Steve Gadd's classic groove in Paul Simon's '50 Ways to Leave Your Lover'.",
            tempoTarget = RudimentTempoTarget(bronzeBpm = 70, silverBpm = 100, goldBpm = 135, platinumBpm = 165),
            relatedExerciseId = "ex_paradiddle_diddle"
        ),

        // ================= FLAM RUDIMENTS =================
        Rudiment(
            id = "flam",
            name = "The Flam",
            alternativeName = "Rudiment #20",
            category = RudimentCategory.FLAMS,
            difficulty = DifficultyLevel.BEGINNER,
            stickingSequence = "lR  /  rL",
            timeSignatureString = "2/4",
            defaultBpm = 70,
            notes = listOf(
                RudimentNote(StickingHand.LEFT, isAccented = false, isGraceNote = true, durationFraction = 0.05f, stepIndex = 0, syllable = "l"),
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.45f, stepIndex = 1, syllable = "1"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, isGraceNote = true, durationFraction = 0.05f, stepIndex = 2, syllable = "r"),
                RudimentNote(StickingHand.LEFT, isAccented = true, durationFraction = 0.45f, stepIndex = 3, syllable = "2")
            ),
            description = "A small grace note played immediately before an accented primary stroke, giving a thick, heavy, fattened sound.",
            technicalBreakdown = "Position the grace note stick low (1 inch above head) and the primary stroke high (8-10 inches). Both hands drop at the same time, so the lower stick lands a fraction of a millisecond earlier.",
            musicalApplication = "Accenting the snare on 2 and 4 in heavy rock, adding thickness to backbeats, and dramatic accents on tom hits.",
            famousExample = "Chad Smith (Red Hot Chili Peppers) and Dave Grohl (Nirvana) heavy rock backbeats.",
            tempoTarget = RudimentTempoTarget(bronzeBpm = 50, silverBpm = 75, goldBpm = 100, platinumBpm = 130)
        ),

        Rudiment(
            id = "flam_accent",
            name = "Flam Accent",
            alternativeName = "Rudiment #21",
            category = RudimentCategory.FLAMS,
            difficulty = DifficultyLevel.INTERMEDIATE,
            stickingSequence = "lR L R  rL R L",
            timeSignatureString = "6/8",
            defaultBpm = 80,
            notes = listOf(
                RudimentNote(StickingHand.LEFT, isAccented = false, isGraceNote = true, durationFraction = 0.05f, stepIndex = 0, syllable = "l"),
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.283f, stepIndex = 1, syllable = "1"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.333f, stepIndex = 2, syllable = "2"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.333f, stepIndex = 3, syllable = "3"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, isGraceNote = true, durationFraction = 0.05f, stepIndex = 4, syllable = "r"),
                RudimentNote(StickingHand.LEFT, isAccented = true, durationFraction = 0.283f, stepIndex = 5, syllable = "4"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.333f, stepIndex = 6, syllable = "5"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.333f, stepIndex = 7, syllable = "6")
            ),
            description = "A triplet alternation starting with a flam on the downbeat (lR L R rL R L).",
            technicalBreakdown = "Master the downstroke-tap-upstroke sequence. The third stroke must be an upstroke to prepare for the flam accent on the opposite hand.",
            musicalApplication = "Classic drum corps cadence rhythm, rolling triplet fills around the tom-toms, and heavy blues shuffle grooves.",
            famousExample = "Alex Van Halen and Jeff Porcaro shuffle fills.",
            tempoTarget = RudimentTempoTarget(bronzeBpm = 60, silverBpm = 85, goldBpm = 115, platinumBpm = 145)
        ),

        Rudiment(
            id = "flam_tap",
            name = "Flam Tap",
            alternativeName = "Rudiment #22",
            category = RudimentCategory.FLAMS,
            difficulty = DifficultyLevel.INTERMEDIATE,
            stickingSequence = "lR R  rL L",
            timeSignatureString = "2/4",
            defaultBpm = 75,
            notes = listOf(
                RudimentNote(StickingHand.LEFT, isAccented = false, isGraceNote = true, durationFraction = 0.05f, stepIndex = 0, syllable = "l"),
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.20f, stepIndex = 1, syllable = "1"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.25f, stepIndex = 2, syllable = "&"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, isGraceNote = true, durationFraction = 0.05f, stepIndex = 3, syllable = "r"),
                RudimentNote(StickingHand.LEFT, isAccented = true, durationFraction = 0.20f, stepIndex = 4, syllable = "2"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.25f, stepIndex = 5, syllable = "&")
            ),
            description = "A flam followed immediately by a tap on the same hand (lR R rL L).",
            technicalBreakdown = "The same hand plays the primary flam note and the tap. Stay relaxed to allow the double stroke on the primary hand to rebound smoothly.",
            musicalApplication = "Great for funk ghost note grooves on the snare drum and syncopated cowbell/agogo rhythms.",
            famousExample = "David Garibaldi (Tower of Power) intricate syncopated snare patterns.",
            tempoTarget = RudimentTempoTarget(bronzeBpm = 55, silverBpm = 80, goldBpm = 110, platinumBpm = 140)
        ),

        Rudiment(
            id = "flam_paradiddle",
            name = "Flam Paradiddle",
            alternativeName = "Flam-a-diddle (Rudiment #24)",
            category = RudimentCategory.FLAMS,
            difficulty = DifficultyLevel.ADVANCED,
            stickingSequence = "lR L R R  rL R L L",
            timeSignatureString = "4/4",
            defaultBpm = 75,
            notes = listOf(
                RudimentNote(StickingHand.LEFT, isAccented = false, isGraceNote = true, durationFraction = 0.05f, stepIndex = 0, syllable = "l"),
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.20f, stepIndex = 1, syllable = "1"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.25f, stepIndex = 2, syllable = "e"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.25f, stepIndex = 3, syllable = "&"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.25f, stepIndex = 4, syllable = "a"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, isGraceNote = true, durationFraction = 0.05f, stepIndex = 5, syllable = "r"),
                RudimentNote(StickingHand.LEFT, isAccented = true, durationFraction = 0.20f, stepIndex = 6, syllable = "2"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.25f, stepIndex = 7, syllable = "e"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.25f, stepIndex = 8, syllable = "&"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.25f, stepIndex = 9, syllable = "a")
            ),
            description = "Combines the power of a flam with the dexterity of a single paradiddle.",
            technicalBreakdown = "The grace note hand must quickly prepare right after playing the prior diddle. Maintain relaxed shoulders and let the wrist do the work.",
            musicalApplication = "Superb for creating explosive, punchy snare-and-tom fills in fusion and progressive rock.",
            famousExample = "Danny Carey (Tool) and Carter Beauford (Dave Matthews Band).",
            tempoTarget = RudimentTempoTarget(bronzeBpm = 50, silverBpm = 75, goldBpm = 105, platinumBpm = 135)
        ),

        Rudiment(
            id = "swiss_army_triplet",
            name = "Swiss Army Triplet",
            alternativeName = "Rudiment #27",
            category = RudimentCategory.FLAMS,
            difficulty = DifficultyLevel.ADVANCED,
            stickingSequence = "lR R L  lR R L",
            timeSignatureString = "6/8",
            defaultBpm = 85,
            notes = listOf(
                RudimentNote(StickingHand.LEFT, isAccented = false, isGraceNote = true, durationFraction = 0.05f, stepIndex = 0, syllable = "l"),
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.283f, stepIndex = 1, syllable = "1"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.333f, stepIndex = 2, syllable = "2"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.333f, stepIndex = 3, syllable = "3"),
                RudimentNote(StickingHand.LEFT, isAccented = false, isGraceNote = true, durationFraction = 0.05f, stepIndex = 4, syllable = "l"),
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.283f, stepIndex = 5, syllable = "4"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.333f, stepIndex = 6, syllable = "5"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.333f, stepIndex = 7, syllable = "6")
            ),
            description = "A flam followed by a right tap and a left tap (lR R L). Does not alternate hands, allowing effortless high-speed streaming.",
            technicalBreakdown = "Because the pattern repeats with the same hand lead, it can be played much faster than an alternating flam accent.",
            musicalApplication = "In modern drum soloing and gospel chops, split the hands across the hi-hat and snare for rolling, syncopated textures.",
            famousExample = "Larnell Lewis (Snarky Puppy) and Tony Royster Jr. high-speed solo patterns.",
            tempoTarget = RudimentTempoTarget(bronzeBpm = 65, silverBpm = 95, goldBpm = 125, platinumBpm = 155)
        ),

        // ================= DRAG RUDIMENTS =================
        Rudiment(
            id = "single_drag_tap",
            name = "Single Drag Tap",
            alternativeName = "Rudiment #31",
            category = RudimentCategory.DRAGS,
            difficulty = DifficultyLevel.INTERMEDIATE,
            stickingSequence = "llR L  rrL R",
            timeSignatureString = "2/4",
            defaultBpm = 75,
            notes = listOf(
                RudimentNote(StickingHand.LEFT, isAccented = false, isGraceNote = true, durationFraction = 0.04f, stepIndex = 0, syllable = "l"),
                RudimentNote(StickingHand.LEFT, isAccented = false, isGraceNote = true, durationFraction = 0.04f, stepIndex = 1, syllable = "l"),
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.42f, stepIndex = 2, syllable = "1"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.5f, stepIndex = 3, syllable = "&"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, isGraceNote = true, durationFraction = 0.04f, stepIndex = 4, syllable = "r"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, isGraceNote = true, durationFraction = 0.04f, stepIndex = 5, syllable = "r"),
                RudimentNote(StickingHand.LEFT, isAccented = true, durationFraction = 0.42f, stepIndex = 6, syllable = "2"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.5f, stepIndex = 7, syllable = "&")
            ),
            description = "Two soft closed bounce notes (the drag) resolving into an accented primary stroke, followed by an alternating tap.",
            technicalBreakdown = "Keep the drag notes very light and close to the drumhead. Do not force them into metronomic 32nd notes; let them buzz naturally into the primary stroke.",
            musicalApplication = "Essential for New Orleans second-line grooves, marching snare rolls, and classic jazz ride cymbal inflections.",
            famousExample = "Stanton Moore and Zigaboo Modeliste (The Meters) funk grooves.",
            tempoTarget = RudimentTempoTarget(bronzeBpm = 55, silverBpm = 80, goldBpm = 110, platinumBpm = 140)
        ),

        Rudiment(
            id = "lesson_25",
            name = "Lesson 25",
            alternativeName = "Rudiment #33",
            category = RudimentCategory.DRAGS,
            difficulty = DifficultyLevel.ADVANCED,
            stickingSequence = "llR L R  rrL R L",
            timeSignatureString = "2/4",
            defaultBpm = 75,
            notes = listOf(
                RudimentNote(StickingHand.LEFT, isAccented = false, isGraceNote = true, durationFraction = 0.04f, stepIndex = 0, syllable = "l"),
                RudimentNote(StickingHand.LEFT, isAccented = false, isGraceNote = true, durationFraction = 0.04f, stepIndex = 1, syllable = "l"),
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.21f, stepIndex = 2, syllable = "1"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.25f, stepIndex = 3, syllable = "&"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.5f, stepIndex = 4, syllable = "2"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, isGraceNote = true, durationFraction = 0.04f, stepIndex = 5, syllable = "r"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, isGraceNote = true, durationFraction = 0.04f, stepIndex = 6, syllable = "r"),
                RudimentNote(StickingHand.LEFT, isAccented = true, durationFraction = 0.21f, stepIndex = 7, syllable = "3"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.25f, stepIndex = 8, syllable = "&"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.5f, stepIndex = 9, syllable = "4")
            ),
            description = "A famous historical rudiment featuring a drag followed by two alternating single strokes.",
            technicalBreakdown = "Grip lightly with the thumb and index finger. Allow the drag to roll effortlessly before snapping the accent with the wrist.",
            musicalApplication = "Used heavily in traditional Scottish pipe band drumming, funk ghost-note variations, and snare drum solos.",
            famousExample = "Gene Krupa and Buddy Rich rudimental snare showcases.",
            tempoTarget = RudimentTempoTarget(bronzeBpm = 50, silverBpm = 75, goldBpm = 105, platinumBpm = 135)
        ),

        Rudiment(
            id = "dragadiddle",
            name = "Dragadiddle",
            alternativeName = "Drag Paradiddle #1 (Rudiment #35)",
            category = RudimentCategory.DRAGS,
            difficulty = DifficultyLevel.ADVANCED,
            stickingSequence = "llR L R R  rrL R L L",
            timeSignatureString = "4/4",
            defaultBpm = 70,
            notes = listOf(
                RudimentNote(StickingHand.LEFT, isAccented = false, isGraceNote = true, durationFraction = 0.04f, stepIndex = 0, syllable = "l"),
                RudimentNote(StickingHand.LEFT, isAccented = false, isGraceNote = true, durationFraction = 0.04f, stepIndex = 1, syllable = "l"),
                RudimentNote(StickingHand.RIGHT, isAccented = true, durationFraction = 0.21f, stepIndex = 2, syllable = "1"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.25f, stepIndex = 3, syllable = "e"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.25f, stepIndex = 4, syllable = "&"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.25f, stepIndex = 5, syllable = "a"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, isGraceNote = true, durationFraction = 0.04f, stepIndex = 6, syllable = "r"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, isGraceNote = true, durationFraction = 0.04f, stepIndex = 7, syllable = "r"),
                RudimentNote(StickingHand.LEFT, isAccented = true, durationFraction = 0.21f, stepIndex = 8, syllable = "2"),
                RudimentNote(StickingHand.RIGHT, isAccented = false, durationFraction = 0.25f, stepIndex = 9, syllable = "e"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.25f, stepIndex = 10, syllable = "&"),
                RudimentNote(StickingHand.LEFT, isAccented = false, durationFraction = 0.25f, stepIndex = 11, syllable = "a")
            ),
            description = "A paradiddle initiated by a double drag on the accented first note.",
            technicalBreakdown = "Execute the drag right as the previous diddle ends. Balance the soft grace notes with the snappy primary downstroke.",
            musicalApplication = "Superb for creating intricate hip-hop and neo-soul snare ghost note textures.",
            famousExample = "Chris Dave and Questlove (The Roots) subtle pocket phrasing.",
            tempoTarget = RudimentTempoTarget(bronzeBpm = 45, silverBpm = 70, goldBpm = 95, platinumBpm = 125)
        )
    )

    fun getById(id: String): Rudiment? = allRudiments.find { it.id == id }

    fun getByCategory(category: RudimentCategory): List<Rudiment> = allRudiments.filter { it.category == category }
}
