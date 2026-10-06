package com.example.model

import com.example.audio.DrumInstrument

enum class RudimentCategory(val displayName: String, val description: String) {
    ROLLS("Roll Rudiments", "Single, double, and buzz rolls for smooth stick control"),
    DIDDLES("Diddle Rudiments", "Paradiddles and combinations balancing singles and doubles"),
    FLAMS("Flam Rudiments", "Primary and grace note articulations for power and texture"),
    DRAGS("Drag & Ruff Rudiments", "Double grace notes leading into accented primary strokes")
}

data class RudimentNote(
    val sticking: StickingHand, // RIGHT, LEFT
    val isAccented: Boolean = false,
    val isGraceNote: Boolean = false, // for Flams and Drags
    val durationFraction: Float = 0.25f, // e.g. 0.25 for 16th note, 0.333 for triplet, 0.5 for 8th
    val stepIndex: Int = 0,
    val syllable: String = "" // "1", "e", "&", "a", "tri", "let", etc.
)

data class RudimentTempoTarget(
    val bronzeBpm: Int = 60,
    val silverBpm: Int = 90,
    val goldBpm: Int = 120,
    val platinumBpm: Int = 150
)

data class Rudiment(
    val id: String,
    val name: String,
    val alternativeName: String = "",
    val category: RudimentCategory,
    val difficulty: DifficultyLevel,
    val stickingSequence: String, // e.g. "R L R R  L R L L"
    val timeSignatureString: String = "4/4",
    val defaultBpm: Int = 80,
    val notes: List<RudimentNote>,
    val description: String,
    val technicalBreakdown: String,
    val musicalApplication: String,
    val famousExample: String,
    val tempoTarget: RudimentTempoTarget = RudimentTempoTarget(),
    val relatedExerciseId: String? = null
)
