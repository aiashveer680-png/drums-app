package com.example.model

enum class BassDrumLetterCategory(val displayName: String, val subtitle: String) {
    SINGLE_NOTE("Single Notes", "Letters A - D: Single 16th note placements per beat"),
    TWO_NOTE("Two Notes", "Letters E - J: Two 16th notes per beat in various combinations"),
    THREE_NOTE("Three Notes", "Letters K - N: Three 16th notes with one rest per beat"),
    FULL_AND_REST("Full & Rest", "Letter O (continuous 16ths) and Letter P (rest)")
}

data class BassDrumLetter(
    val letter: String, // "A" .. "P"
    val pattern: String, // e.g. "1000", "0100"
    val category: BassDrumLetterCategory,
    val description: String,
    val countLabels: List<String> = listOf("1", "e", "&", "a"),
    val coachTip: String
) {
    fun hasKickOnSlot(slot: Int): Boolean {
        if (slot !in 0..3) return false
        return pattern.getOrNull(slot) == '1'
    }

    val activeCountDescription: String
        get() {
            val hits = mutableListOf<String>()
            for (i in 0 until 4) {
                if (pattern.getOrNull(i) == '1') hits.add(countLabels[i])
            }
            return if (hits.isEmpty()) "Rest (No kick - hands only)" else hits.joinToString(", ")
        }
}

data class BassDrumVoiceConfig(
    val playHiHat: Boolean = true,
    val playSnare: Boolean = true,
    val playKick: Boolean = true,
    val playClick: Boolean = false
)

data class BassDrumAutoAdvanceConfig(
    val enabled: Boolean = false,
    val barsPerLetter: Int = 4 // 1, 2, 4, 8
)
