package com.example.model

import com.example.audio.DrumInstrument
import com.example.audio.TimeSignature

enum class ExerciseCategory(val displayName: String, val iconName: String) {
    BASIC_RHYTHM("Basic Rhythms", "rhythm"),
    PARADIDDLE("Paradiddles & Rudiments", "rudiment"),
    DRUM_FILL("Common Drum Fills", "fill")
}

enum class DifficultyLevel(val displayName: String, val colorHex: Long) {
    BEGINNER("Beginner", 0xFF00E676),
    INTERMEDIATE("Intermediate", 0xFFFFB300),
    ADVANCED("Advanced", 0xFFFF5252)
}

enum class StickingHand(val label: String) {
    RIGHT("R"),
    LEFT("L"),
    KICK("K"),
    BOTH("RL")
}

data class ScheduledHit(
    val bar: Int,
    val step: Int, // 0 to stepsPerBar - 1
    val instrument: DrumInstrument,
    val sticking: StickingHand? = null,
    val isAccented: Boolean = false
)

data class Exercise(
    val id: String,
    val title: String,
    val category: ExerciseCategory,
    val difficulty: DifficultyLevel,
    val defaultBpm: Int,
    val timeSignature: TimeSignature = TimeSignature.TS_4_4,
    val stepsPerBar: Int = 16,
    val totalBars: Int = 2,
    val description: String,
    val stickingAdvice: String,
    val coachTip: String,
    val hits: List<ScheduledHit>
)

enum class TimingRating(val label: String, val points: Int) {
    PERFECT("PERFECT!", 100),
    GOOD("GOOD", 60),
    EARLY("EARLY", 30),
    LATE("LATE", 30),
    MISS("MISS", 0)
}

data class HitFeedback(
    val rating: TimingRating,
    val deltaMs: Long,
    val instrument: DrumInstrument,
    val timestamp: Long = System.currentTimeMillis()
)

data class ExerciseSessionStats(
    val exerciseId: String,
    val totalExpectedHits: Int = 0,
    val perfectHits: Int = 0,
    val goodHits: Int = 0,
    val earlyHits: Int = 0,
    val lateHits: Int = 0,
    val missedHits: Int = 0,
    val currentCombo: Int = 0,
    val maxCombo: Int = 0,
    val score: Int = 0
) {
    val totalAttempted: Int get() = perfectHits + goodHits + earlyHits + lateHits + missedHits
    val accuracyPercentage: Int
        get() {
            if (totalExpectedHits == 0) return 0
            val effectiveScore = (perfectHits * 1.0 + goodHits * 0.7 + earlyHits * 0.3 + lateHits * 0.3)
            return ((effectiveScore / totalExpectedHits.coerceAtLeast(1)) * 100).toInt().coerceIn(0, 100)
        }

    val starsEarned: Int
        get() = when {
            accuracyPercentage >= 90 -> 3
            accuracyPercentage >= 75 -> 2
            accuracyPercentage >= 50 -> 1
            else -> 0
        }
}
