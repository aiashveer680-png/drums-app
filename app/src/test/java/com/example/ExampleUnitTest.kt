package com.example

import com.example.audio.CountdownTimerConfig
import com.example.audio.PresetRhythmCatalog
import com.example.audio.TimeSignature
import com.example.model.ExerciseSessionStats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testCountdownTimerConfig() {
        val timer = CountdownTimerConfig(
            enabled = true,
            durationSeconds = 300,
            remainingSeconds = 150,
            isCompleted = false
        )
        assertEquals("02:30", timer.formattedRemaining)
        assertEquals("05:00", timer.formattedTotal)
        assertEquals(0.5f, timer.progress, 0.01f)
    }

    @Test
    fun testTimeSignatures() {
        val ts44 = TimeSignature.TS_4_4
        assertEquals(4, ts44.numerator)
        assertEquals(4, ts44.denominator)
        assertEquals("4/4", ts44.displayName)

        val ts54 = TimeSignature.TS_5_4
        assertEquals(5, ts54.numerator)
        assertEquals(4, ts54.denominator)
        assertEquals("5/4", ts54.displayName)

        val ts68 = TimeSignature.TS_6_8
        assertEquals(6, ts68.numerator)
        assertEquals(8, ts68.denominator)
        assertEquals("6/8", ts68.displayName)

        val ts128 = TimeSignature.TS_12_8
        assertEquals(12, ts128.numerator)
        assertEquals(8, ts128.denominator)
        assertEquals("12/8", ts128.displayName)

        assertTrue(TimeSignature.standardList.contains(ts54))
        assertTrue(TimeSignature.standardList.contains(ts128))
    }

    @Test
    fun testPresetRhythmCatalog() {
        val rhythms = PresetRhythmCatalog.allRhythms
        assertTrue("Catalog should contain preset grooves", rhythms.isNotEmpty())
        val rockGroove = rhythms.firstOrNull { it.id == "rock_standard" }
        assertNotNull(rockGroove)
        assertTrue(rockGroove!!.hits.isNotEmpty())
    }

    @Test
    fun testExerciseAccuracyCalculation() {
        val perfectSession = ExerciseSessionStats(
            exerciseId = "test_ex",
            totalExpectedHits = 20,
            perfectHits = 19,
            goodHits = 1,
            earlyHits = 0,
            lateHits = 0,
            missedHits = 0,
            score = 2500
        )
        assertTrue(perfectSession.accuracyPercentage >= 95)
        assertEquals(3, perfectSession.starsEarned)
    }
}
