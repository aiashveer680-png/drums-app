package com.example.data

import com.example.audio.DrumInstrument
import com.example.audio.TimeSignature
import com.example.model.DifficultyLevel
import com.example.model.Exercise
import com.example.model.ExerciseCategory
import com.example.model.ExerciseSessionStats
import com.example.model.Lesson
import com.example.model.LessonSlide
import com.example.model.ScheduledHit
import com.example.model.StickingHand
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DrumRepository(private val drumDao: DrumDao) {

    val allExerciseProgress: Flow<List<ExerciseProgressEntity>> = drumDao.getAllExerciseProgress()
    val allLessonProgress: Flow<List<LessonProgressEntity>> = drumDao.getAllLessonProgress()
    val recentSessions: Flow<List<PracticeSessionEntity>> = drumDao.getRecentPracticeSessions()
    val userProfile: Flow<UserProfileEntity?> = drumDao.getUserProfile()

    // Default static curriculum catalog
    val exercises: List<Exercise> = listOf(
        // ================= BASIC RHYTHMS =================
        Exercise(
            id = "rhythm_moneymaker",
            title = "The Moneymaker (4/4 Rock)",
            category = ExerciseCategory.BASIC_RHYTHM,
            difficulty = DifficultyLevel.BEGINNER,
            defaultBpm = 80,
            timeSignature = TimeSignature.TS_4_4,
            stepsPerBar = 16,
            totalBars = 2,
            description = "The most recorded drum beat in music history. Solid bass drum on beats 1 & 3, cracking snare backbeat on 2 & 4, and steady 8th notes on the hi-hat.",
            stickingAdvice = "Right hand maintains continuous 8th notes on closed hi-hat. Left hand drops on beats 2 & 4. Right foot hits on beats 1 & 3.",
            coachTip = "Lock into the pocket! Keep the hi-hat relaxed and uniform. Don't rush into beat 2.",
            hits = buildTwoBarRockGroove()
        ),
        Exercise(
            id = "rhythm_four_on_floor",
            title = "Four on the Floor (Dance Pulse)",
            category = ExerciseCategory.BASIC_RHYTHM,
            difficulty = DifficultyLevel.BEGINNER,
            defaultBpm = 110,
            timeSignature = TimeSignature.TS_4_4,
            stepsPerBar = 16,
            totalBars = 2,
            description = "Driving dance, disco, and pop groove. Bass drum hits on every quarter note (1, 2, 3, 4) with snappy snare on 2 & 4 and upbeat open hi-hat sizzles.",
            stickingAdvice = "Right foot pulses steady quarters. Accentuate the upbeat 'and' on the hi-hat.",
            coachTip = "Consistent bass drum velocity is key. Let the snare snap provide the contrast!",
            hits = buildFourOnFloorGroove()
        ),
        Exercise(
            id = "rhythm_syncopated_kick",
            title = "Syncopated Funk Kick",
            category = ExerciseCategory.BASIC_RHYTHM,
            difficulty = DifficultyLevel.INTERMEDIATE,
            defaultBpm = 90,
            timeSignature = TimeSignature.TS_4_4,
            stepsPerBar = 16,
            totalBars = 2,
            description = "Modern pop and funk rhythm featuring displaced bass drum hits on the 'and' of beats 2 and 3.",
            stickingAdvice = "Right hand steady on hi-hat. Foot coordinates with the offbeat space between hi-hat clicks.",
            coachTip = "Count aloud: '1 and 2 AND 3 AND 4'. Independence between foot and hands develops here.",
            hits = buildSyncopatedGroove()
        ),

        // ================= PARADIDDLES & RUDIMENTS =================
        Exercise(
            id = "rudiment_single_paradiddle",
            title = "Single Paradiddle (R L R R - L R L L)",
            category = ExerciseCategory.PARADIDDLE,
            difficulty = DifficultyLevel.BEGINNER,
            defaultBpm = 95,
            timeSignature = TimeSignature.TS_4_4,
            stepsPerBar = 16,
            totalBars = 2,
            description = "The cornerstone of drum rudiments! Combines single strokes with a double stroke to switch lead hands smoothly without missing a beat.",
            stickingAdvice = "R - L - R - R | L - R - L - L. Accent the first note of each four, keep inner notes down at tap height.",
            coachTip = "Musora Coach Rule: Use rebound on the double (R R / L L) rather than two separate muscular hits.",
            hits = buildSingleParadiddleHits()
        ),
        Exercise(
            id = "rudiment_double_stroke_roll",
            title = "Double Stroke Roll (R R L L)",
            category = ExerciseCategory.PARADIDDLE,
            difficulty = DifficultyLevel.INTERMEDIATE,
            defaultBpm = 100,
            timeSignature = TimeSignature.TS_4_4,
            stepsPerBar = 16,
            totalBars = 2,
            description = "Two strokes per hand in succession. Essential for smooth, rapid drum rolls, orchestral chops, and dynamic fills.",
            stickingAdvice = "R - R - L - L - R - R - L - L. Let the second stroke bounce naturally with fulcrum pinch.",
            coachTip = "Ensure stroke #2 sounds equally as loud and clear as stroke #1. Avoid buzzing.",
            hits = buildDoubleStrokeRollHits()
        ),
        Exercise(
            id = "rudiment_double_paradiddle",
            title = "Double Paradiddle (6/8 Sticking)",
            category = ExerciseCategory.PARADIDDLE,
            difficulty = DifficultyLevel.ADVANCED,
            defaultBpm = 110,
            timeSignature = TimeSignature.TS_6_8,
            stepsPerBar = 12,
            totalBars = 2,
            description = "Six-note rudiment (R L R L R R - L R L R L L). Beautiful for 6/8 ballads, blues shuffles, and Afro-Cuban grooves.",
            stickingAdvice = "Four singles followed by a double: R - L - R - L - R - R | L - R - L - R - L - L.",
            coachTip = "Feel the lilt in two groups of three. Accent beat 1 and beat 4 of 6/8.",
            hits = buildDoubleParadiddleHits()
        ),

        // ================= COMMON DRUM FILLS =================
        Exercise(
            id = "fill_around_the_kit",
            title = "Around the Kit (8th Note Fill)",
            category = ExerciseCategory.DRUM_FILL,
            difficulty = DifficultyLevel.BEGINNER,
            defaultBpm = 85,
            timeSignature = TimeSignature.TS_4_4,
            stepsPerBar = 16,
            totalBars = 2,
            description = "Bar 1 lays down the solid groove; Bar 2 explodes across the drums moving from Snare to High Tom to Floor Tom, landing on Crash + Kick on beat 1.",
            stickingAdvice = "Bar 2: 2 hits on Snare (R L), 2 hits on High Tom (R L), 4 hits on Floor Tom (R L R L), Crash & Kick on the downbeat.",
            coachTip = "Keep your tempo steady through the transition! Most drummers rush fills—stay locked.",
            hits = buildAroundTheKitFill()
        ),
        Exercise(
            id = "fill_classic_16th_rock",
            title = "Classic 16th Rock Fill",
            category = ExerciseCategory.DRUM_FILL,
            difficulty = DifficultyLevel.INTERMEDIATE,
            defaultBpm = 90,
            timeSignature = TimeSignature.TS_4_4,
            stepsPerBar = 16,
            totalBars = 2,
            description = "The signature fill heard in classic rock and pop anthems. Bar 2 transitions to rapid-fire 16th notes streaming across all drums.",
            stickingAdvice = "Alternate hands: R L R L on Snare, R L on High Tom, R L R L on Floor Tom, resolve on Crash + Kick.",
            coachTip = "Lead with the right hand, stay relaxed, and visualize the landing spot on the Crash.",
            hits = buildClassic16thRockFill()
        ),
        Exercise(
            id = "fill_linear_six_stroke",
            title = "Linear Six-Stroke Chops Fill",
            category = ExerciseCategory.DRUM_FILL,
            difficulty = DifficultyLevel.ADVANCED,
            defaultBpm = 95,
            timeSignature = TimeSignature.TS_4_4,
            stepsPerBar = 16,
            totalBars = 2,
            description = "Modern gospel and fusion fill: no two limbs hit at the exact same time, creating an explosive flowing waterfall of sound.",
            stickingAdvice = "R L K K R L pattern distributed between High Tom, Snare, and Bass Drum.",
            coachTip = "Linear fills require flawless limb independence. Start at 60 BPM until the kick-hand transition is seamless.",
            hits = buildLinearFill()
        )
    )

    // Lessons catalog
    val lessons: List<Lesson> = listOf(
        Lesson(
            id = "lesson_method_1",
            levelNumber = 1,
            levelName = "The Musora Method: Level 1",
            title = "Drumming Foundations & The Fulcrum Grip",
            subtitle = "Master stick grip, posture, and your first drum hits",
            instructor = "Coach Dave Atkinson",
            durationMin = 10,
            overview = "Welcome to your drumming journey! This introductory lesson builds the anatomical foundation of drumming: matched grip, the natural stick rebound, and clean kick pedal action.",
            keyPoints = listOf(
                "Find the natural fulcrum between your thumb and index knuckle (approx 1/3 from the stick butt).",
                "Keep your wrists relaxed; let the drumstick bounce freely off the drum head.",
                "Sit with your hips slightly higher than your knees for maximum kick pedal power.",
                "Practice stroke types: Full stroke, Down stroke, Tap, and Up stroke."
            ),
            slides = listOf(
                LessonSlide(
                    stepTitle = "1. The Matched Grip",
                    description = "Grip the stick at the balance point. Hold lightly like a bird—firm enough not to drop, gentle enough not to crush.",
                    diagramOrNotationHint = "Fulcrum: Thumb & Index finger pivot. Remaining fingers gently wrap.",
                    coachNotes = "Tension is the #1 enemy of speed. If your forearm tenses up, shake it out!"
                ),
                LessonSlide(
                    stepTitle = "2. Rebound vs Muscling",
                    description = "Throw the stick downward and allow the drum head trampoline effect to bring the tip back up to the ready position.",
                    diagramOrNotationHint = "Motion: Wrist drop -> Rebound spring back to 90 degrees.",
                    coachNotes = "Work with physics, not against it. The stick wants to bounce!"
                ),
                LessonSlide(
                    stepTitle = "3. Playing Your First Rock Groove",
                    description = "Combine Kick on beat 1, Snare on beat 2, Kick on 3, Snare on 4, with steady 8th-note Hi-Hats.",
                    diagramOrNotationHint = "Counts: 1 & 2 & 3 & 4 & | K+H, H, S+H, H, K+H, H, S+H, H",
                    coachNotes = "Count out loud while playing. Counting synchronizes your brain and limbs."
                )
            ),
            targetExerciseId = "rhythm_moneymaker"
        ),
        Lesson(
            id = "lesson_method_2",
            levelNumber = 2,
            levelName = "The Musora Method: Level 2",
            title = "Building Groove Consistency & Dynamics",
            subtitle = "Locking in with the Metronome and Four-on-the-Floor",
            instructor = "Todd Sucherman",
            durationMin = 12,
            overview = "Explore how to lock into the pocket with the metronome. You'll master dance rhythms like Four-on-the-Floor and develop control over volume dynamics.",
            keyPoints = listOf(
                "Play behind, ahead, or right in the center of the metronome click.",
                "Control Hi-Hat foot pressure to switch between crisp closed chicks and sizzling open slosh.",
                "Use the rimshot technique for powerful rock snare backbeats.",
                "Maintain a consistent pulse without speeding up during exciting sections."
            ),
            slides = listOf(
                LessonSlide(
                    stepTitle = "1. Disappearing Click Concept",
                    description = "When you hit the drum exactly on the metronome click, the click seems to disappear! That is the center of the pocket.",
                    diagramOrNotationHint = "Metronome at 100 BPM: Aim for zero latency.",
                    coachNotes = "If you can still hear the click clearly right beside your drum hit, you are slightly early or late."
                ),
                LessonSlide(
                    stepTitle = "2. Four-on-the-Floor Mastery",
                    description = "The foundation of dance, disco, and stadium rock. Keep your right foot pumping like a heartbeat.",
                    diagramOrNotationHint = "Kick: 1, 2, 3, 4 | Snare: 2, 4 | Hi-Hat: Upbeat '&'",
                    coachNotes = "Don't let your kick drum waver. It must be as steady as an electric sequencer."
                )
            ),
            targetExerciseId = "rhythm_four_on_floor"
        ),
        Lesson(
            id = "lesson_method_3",
            levelNumber = 3,
            levelName = "The Musora Method: Level 3",
            title = "Paradiddles & Stick Control Secrets",
            subtitle = "Unlocking hand speed, accents, and fluid rudiments",
            instructor = "Hannah Welton",
            durationMin = 15,
            overview = "The Single Paradiddle is the Swiss Army knife of drumming. By changing where accents fall, a single rudiment transforms into grooves, fills, and jazz ostinatos.",
            keyPoints = listOf(
                "Sticking pattern: Right Left Right Right - Left Right Left Left.",
                "Differentiate between Accented notes (high stick height) and Taps (low stick height ~2 inches).",
                "Utilize the Moeller whipping stroke for effortless power and speed.",
                "Apply the paradiddle across different drums and cymbals on your kit."
            ),
            slides = listOf(
                LessonSlide(
                    stepTitle = "1. Breaking Down the Paradiddle",
                    description = "A paradiddle has two singles (Para) and one double (Diddle). That creates R-L-R-R and L-R-L-L.",
                    diagramOrNotationHint = "> R l r r > L r l l (Capital letters are loud accents).",
                    coachNotes = "Keep the doubles whisper quiet compared to the accented first stroke."
                ),
                LessonSlide(
                    stepTitle = "2. Orchestrating on the Kit",
                    description = "Move your right hand to the Ride cymbal or Floor Tom while your left stays on the Snare.",
                    diagramOrNotationHint = "Ride (R) + Snare (L): Instantly sounds like advanced funk!",
                    coachNotes = "Once your hands know the pattern, your kit opens up with endless possibilities."
                )
            ),
            targetExerciseId = "rudiment_single_paradiddle"
        ),
        Lesson(
            id = "lesson_method_4",
            levelNumber = 4,
            levelName = "The Musora Method: Level 4",
            title = "Drum Fills That Serve the Music",
            subtitle = "Smooth transitions from groove to fill and back",
            instructor = "Coach Dave Atkinson",
            durationMin = 14,
            overview = "Learn how to enter and exit drum fills without speeding up or slowing down. We break down the classic Around-the-Kit fill and 16th-note orchestrations.",
            keyPoints = listOf(
                "Never compromise the tempo of the song just to play a flashy fill.",
                "Lead with your dominant hand and let momentum carry your arms around the toms.",
                "Always strike the Crash cymbal together with the Bass Drum on beat 1 to declare the groove's return.",
                "Breathe! Don't hold your breath while executing complex fill patterns."
            ),
            slides = listOf(
                LessonSlide(
                    stepTitle = "1. The 3+1 Measure Rule",
                    description = "Play 3 bars of steady groove, followed by 1 bar of fill. This trains song-structure awareness.",
                    diagramOrNotationHint = "Bar 1-3: Pocket Groove | Bar 4: Fill -> Crash on Bar 5 beat 1.",
                    coachNotes = "Musicians love drummers who make the transition seamless and in-time."
                ),
                LessonSlide(
                    stepTitle = "2. Around the Kit Mechanics",
                    description = "Travel clockwise from Snare to High Tom to Floor Tom, keeping stick angles flat to avoid rim dents.",
                    diagramOrNotationHint = "Snare (1 &) -> High Tom (2 &) -> Floor Tom (3 & 4 &) -> Crash + Kick (1)",
                    coachNotes = "Aim directly for the center sweet spot of each tom head."
                )
            ),
            targetExerciseId = "fill_around_the_kit"
        )
    )

    suspend fun recordExerciseSession(
        stats: ExerciseSessionStats,
        bpm: Int,
        durationSeconds: Int
    ) {
        val existing = drumDao.getExerciseProgress(stats.exerciseId)
        val newScore = stats.score.coerceAtLeast(existing?.highScore ?: 0)
        val newStars = stats.starsEarned.coerceAtLeast(existing?.starsEarned ?: 0)
        val newAcc = stats.accuracyPercentage.coerceAtLeast(existing?.bestAccuracyPct ?: 0)
        val newBpm = bpm.coerceAtLeast(existing?.highestBpm ?: 0)
        val completions = (existing?.completionCount ?: 0) + 1

        val updatedProgress = ExerciseProgressEntity(
            exerciseId = stats.exerciseId,
            highScore = newScore,
            starsEarned = newStars,
            bestAccuracyPct = newAcc,
            highestBpm = newBpm,
            completionCount = completions,
            lastPracticedTimestamp = System.currentTimeMillis()
        )
        drumDao.saveExerciseProgress(updatedProgress)

        // Find exercise title
        val exercise = exercises.firstOrNull { it.id == stats.exerciseId }
        val title = exercise?.title ?: "Drum Exercise"
        val category = exercise?.category?.displayName ?: "Rhythm"

        // Insert practice session log
        drumDao.insertPracticeSession(
            PracticeSessionEntity(
                title = title,
                category = category,
                durationSeconds = durationSeconds,
                bpm = bpm,
                accuracyPct = stats.accuracyPercentage
            )
        )

        // Award XP & update Streak
        val earnedXp = (stats.score / 5) + (stats.starsEarned * 50) + (durationSeconds / 2)
        updateUserXpAndStreak(earnedXp)
    }

    suspend fun recordRhythmPracticeSession(
        rhythmName: String,
        bpm: Int,
        durationSeconds: Int
    ) {
        drumDao.insertPracticeSession(
            PracticeSessionEntity(
                title = rhythmName,
                category = "Rhythm Practice",
                durationSeconds = durationSeconds,
                bpm = bpm,
                accuracyPct = 100
            )
        )
        val earnedXp = (durationSeconds / 2).coerceAtLeast(25)
        updateUserXpAndStreak(earnedXp)
    }

    suspend fun markLessonCompleted(lessonId: String) {
        drumDao.saveLessonProgress(
            LessonProgressEntity(
                lessonId = lessonId,
                isCompleted = true,
                completedAt = System.currentTimeMillis()
            )
        )
        updateUserXpAndStreak(150)
    }

    private suspend fun updateUserXpAndStreak(addedXp: Int) {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        // For default or fetched user
        val profile = UserProfileEntity(
            id = 1,
            totalXp = 250 + addedXp,
            currentStreakDays = 3,
            longestStreakDays = 7,
            lastActiveDate = todayStr
        )
        drumDao.saveUserProfile(profile)
    }

    companion object {
        private fun buildTwoBarRockGroove(): List<ScheduledHit> {
            val hits = mutableListOf<ScheduledHit>()
            for (bar in 0..1) {
                // Hi-Hat on all 8th notes (steps 0, 2, 4, 6, 8, 10, 12, 14)
                for (step in 0 until 16 step 2) {
                    hits.add(ScheduledHit(bar, step, DrumInstrument.HIHAT_CLOSED, StickingHand.RIGHT))
                }
                // Kick on 1 and 3 (step 0 and step 8)
                hits.add(ScheduledHit(bar, 0, DrumInstrument.KICK, StickingHand.KICK, isAccented = true))
                hits.add(ScheduledHit(bar, 8, DrumInstrument.KICK, StickingHand.KICK))
                // Snare on 2 and 4 (step 4 and step 12)
                hits.add(ScheduledHit(bar, 4, DrumInstrument.SNARE, StickingHand.LEFT, isAccented = true))
                hits.add(ScheduledHit(bar, 12, DrumInstrument.SNARE, StickingHand.LEFT, isAccented = true))
            }
            return hits
        }

        private fun buildFourOnFloorGroove(): List<ScheduledHit> {
            val hits = mutableListOf<ScheduledHit>()
            for (bar in 0..1) {
                // Kick on every quarter note (steps 0, 4, 8, 12)
                for (step in 0 until 16 step 4) {
                    hits.add(ScheduledHit(bar, step, DrumInstrument.KICK, StickingHand.KICK, isAccented = true))
                }
                // Snare on 2 and 4 (steps 4 and 12)
                hits.add(ScheduledHit(bar, 4, DrumInstrument.SNARE, StickingHand.LEFT, isAccented = true))
                hits.add(ScheduledHit(bar, 12, DrumInstrument.SNARE, StickingHand.LEFT, isAccented = true))
                // Closed hi-hat on downbeats, Open hi-hat on upbeats (steps 2, 6, 10, 14)
                for (step in 0 until 16 step 4) {
                    hits.add(ScheduledHit(bar, step, DrumInstrument.HIHAT_CLOSED, StickingHand.RIGHT))
                }
                for (step in 2 until 16 step 4) {
                    hits.add(ScheduledHit(bar, step, DrumInstrument.HIHAT_OPEN, StickingHand.RIGHT, isAccented = true))
                }
            }
            return hits
        }

        private fun buildSyncopatedGroove(): List<ScheduledHit> {
            val hits = mutableListOf<ScheduledHit>()
            for (bar in 0..1) {
                for (step in 0 until 16 step 2) {
                    hits.add(ScheduledHit(bar, step, DrumInstrument.HIHAT_CLOSED, StickingHand.RIGHT))
                }
                // Snare on 2 and 4
                hits.add(ScheduledHit(bar, 4, DrumInstrument.SNARE, StickingHand.LEFT, isAccented = true))
                hits.add(ScheduledHit(bar, 12, DrumInstrument.SNARE, StickingHand.LEFT, isAccented = true))
                // Syncopated kick: Beat 1 (step 0), 2-and (step 6), 3-and (step 10)
                hits.add(ScheduledHit(bar, 0, DrumInstrument.KICK, StickingHand.KICK, isAccented = true))
                hits.add(ScheduledHit(bar, 6, DrumInstrument.KICK, StickingHand.KICK))
                hits.add(ScheduledHit(bar, 10, DrumInstrument.KICK, StickingHand.KICK))
            }
            return hits
        }

        private fun buildSingleParadiddleHits(): List<ScheduledHit> {
            val hits = mutableListOf<ScheduledHit>()
            // Pattern in 16th notes:
            // R L R R  L R L L  R L R R  L R L L
            val stickings = listOf(
                StickingHand.RIGHT, StickingHand.LEFT, StickingHand.RIGHT, StickingHand.RIGHT,
                StickingHand.LEFT, StickingHand.RIGHT, StickingHand.LEFT, StickingHand.LEFT,
                StickingHand.RIGHT, StickingHand.LEFT, StickingHand.RIGHT, StickingHand.RIGHT,
                StickingHand.LEFT, StickingHand.RIGHT, StickingHand.LEFT, StickingHand.LEFT
            )
            for (bar in 0..1) {
                for (step in 0 until 16) {
                    val isAcc = (step == 0 || step == 4 || step == 8 || step == 12)
                    hits.add(ScheduledHit(bar, step, DrumInstrument.SNARE, stickings[step], isAccented = isAcc))
                }
            }
            return hits
        }

        private fun buildDoubleStrokeRollHits(): List<ScheduledHit> {
            val hits = mutableListOf<ScheduledHit>()
            // R R L L R R L L
            val stickings = listOf(
                StickingHand.RIGHT, StickingHand.RIGHT, StickingHand.LEFT, StickingHand.LEFT,
                StickingHand.RIGHT, StickingHand.RIGHT, StickingHand.LEFT, StickingHand.LEFT,
                StickingHand.RIGHT, StickingHand.RIGHT, StickingHand.LEFT, StickingHand.LEFT,
                StickingHand.RIGHT, StickingHand.RIGHT, StickingHand.LEFT, StickingHand.LEFT
            )
            for (bar in 0..1) {
                for (step in 0 until 16) {
                    hits.add(ScheduledHit(bar, step, DrumInstrument.SNARE, stickings[step], isAccented = (step % 4 == 0)))
                }
            }
            return hits
        }

        private fun buildDoubleParadiddleHits(): List<ScheduledHit> {
            val hits = mutableListOf<ScheduledHit>()
            // In 6/8 (12 steps): R L R L R R | L R L R L L
            val stickings = listOf(
                StickingHand.RIGHT, StickingHand.LEFT, StickingHand.RIGHT, StickingHand.LEFT, StickingHand.RIGHT, StickingHand.RIGHT,
                StickingHand.LEFT, StickingHand.RIGHT, StickingHand.LEFT, StickingHand.RIGHT, StickingHand.LEFT, StickingHand.LEFT
            )
            for (bar in 0..1) {
                for (step in 0 until 12) {
                    val isAcc = (step == 0 || step == 6)
                    hits.add(ScheduledHit(bar, step, DrumInstrument.SNARE, stickings[step], isAccented = isAcc))
                }
            }
            return hits
        }

        private fun buildAroundTheKitFill(): List<ScheduledHit> {
            val hits = mutableListOf<ScheduledHit>()
            // Bar 0: Groove
            for (step in 0 until 16 step 2) {
                hits.add(ScheduledHit(0, step, DrumInstrument.HIHAT_CLOSED, StickingHand.RIGHT))
            }
            hits.add(ScheduledHit(0, 0, DrumInstrument.KICK, StickingHand.KICK, isAccented = true))
            hits.add(ScheduledHit(0, 4, DrumInstrument.SNARE, StickingHand.LEFT, isAccented = true))
            hits.add(ScheduledHit(0, 8, DrumInstrument.KICK, StickingHand.KICK))
            hits.add(ScheduledHit(0, 12, DrumInstrument.SNARE, StickingHand.LEFT, isAccented = true))

            // Bar 1: Fill around kit (8th notes: 0, 2 on Snare, 4, 6 on High Tom, 8, 10, 12, 14 on Floor Tom)
            hits.add(ScheduledHit(1, 0, DrumInstrument.SNARE, StickingHand.RIGHT, isAccented = true))
            hits.add(ScheduledHit(1, 2, DrumInstrument.SNARE, StickingHand.LEFT))
            hits.add(ScheduledHit(1, 4, DrumInstrument.TOM_HIGH, StickingHand.RIGHT, isAccented = true))
            hits.add(ScheduledHit(1, 6, DrumInstrument.TOM_HIGH, StickingHand.LEFT))
            hits.add(ScheduledHit(1, 8, DrumInstrument.TOM_FLOOR, StickingHand.RIGHT, isAccented = true))
            hits.add(ScheduledHit(1, 10, DrumInstrument.TOM_FLOOR, StickingHand.LEFT))
            hits.add(ScheduledHit(1, 12, DrumInstrument.TOM_FLOOR, StickingHand.RIGHT))
            hits.add(ScheduledHit(1, 14, DrumInstrument.TOM_FLOOR, StickingHand.LEFT))
            return hits
        }

        private fun buildClassic16thRockFill(): List<ScheduledHit> {
            val hits = mutableListOf<ScheduledHit>()
            // Bar 0: Groove
            for (step in 0 until 16 step 2) {
                hits.add(ScheduledHit(0, step, DrumInstrument.HIHAT_CLOSED, StickingHand.RIGHT))
            }
            hits.add(ScheduledHit(0, 0, DrumInstrument.KICK, StickingHand.KICK, isAccented = true))
            hits.add(ScheduledHit(0, 4, DrumInstrument.SNARE, StickingHand.LEFT, isAccented = true))
            hits.add(ScheduledHit(0, 8, DrumInstrument.KICK, StickingHand.KICK))
            hits.add(ScheduledHit(0, 12, DrumInstrument.SNARE, StickingHand.LEFT, isAccented = true))

            // Bar 1: 16th notes: 4 on Snare, 4 on High Tom, 6 on Floor Tom, Crash + Kick landing
            for (s in 0..3) hits.add(ScheduledHit(1, s, DrumInstrument.SNARE, if (s % 2 == 0) StickingHand.RIGHT else StickingHand.LEFT, isAccented = (s == 0)))
            for (s in 4..7) hits.add(ScheduledHit(1, s, DrumInstrument.TOM_HIGH, if (s % 2 == 0) StickingHand.RIGHT else StickingHand.LEFT, isAccented = (s == 4)))
            for (s in 8..15) hits.add(ScheduledHit(1, s, DrumInstrument.TOM_FLOOR, if (s % 2 == 0) StickingHand.RIGHT else StickingHand.LEFT, isAccented = (s == 8)))
            return hits
        }

        private fun buildLinearFill(): List<ScheduledHit> {
            val hits = mutableListOf<ScheduledHit>()
            // Bar 0: Groove
            for (step in 0 until 16 step 2) {
                hits.add(ScheduledHit(0, step, DrumInstrument.HIHAT_CLOSED, StickingHand.RIGHT))
            }
            hits.add(ScheduledHit(0, 0, DrumInstrument.KICK, StickingHand.KICK, isAccented = true))
            hits.add(ScheduledHit(0, 4, DrumInstrument.SNARE, StickingHand.LEFT, isAccented = true))
            hits.add(ScheduledHit(0, 8, DrumInstrument.KICK, StickingHand.KICK))
            hits.add(ScheduledHit(0, 12, DrumInstrument.SNARE, StickingHand.LEFT, isAccented = true))

            // Bar 1: Linear chops (R L K K R L)
            hits.add(ScheduledHit(1, 0, DrumInstrument.TOM_HIGH, StickingHand.RIGHT, isAccented = true))
            hits.add(ScheduledHit(1, 2, DrumInstrument.SNARE, StickingHand.LEFT))
            hits.add(ScheduledHit(1, 4, DrumInstrument.KICK, StickingHand.KICK))
            hits.add(ScheduledHit(1, 6, DrumInstrument.KICK, StickingHand.KICK))
            hits.add(ScheduledHit(1, 8, DrumInstrument.TOM_FLOOR, StickingHand.RIGHT, isAccented = true))
            hits.add(ScheduledHit(1, 10, DrumInstrument.SNARE, StickingHand.LEFT))
            hits.add(ScheduledHit(1, 12, DrumInstrument.CRASH, StickingHand.RIGHT, isAccented = true))
            hits.add(ScheduledHit(1, 12, DrumInstrument.KICK, StickingHand.KICK, isAccented = true))
            return hits
        }
    }
}
