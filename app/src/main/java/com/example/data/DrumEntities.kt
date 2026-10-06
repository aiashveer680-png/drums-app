package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_progress")
data class ExerciseProgressEntity(
    @PrimaryKey val exerciseId: String,
    val highScore: Int = 0,
    val starsEarned: Int = 0,
    val bestAccuracyPct: Int = 0,
    val highestBpm: Int = 0,
    val completionCount: Int = 0,
    val lastPracticedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "lesson_progress")
data class LessonProgressEntity(
    @PrimaryKey val lessonId: String,
    val isCompleted: Boolean = false,
    val completedAt: Long = 0L
)

@Entity(tableName = "practice_sessions")
data class PracticeSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val timestamp: Long = System.currentTimeMillis(),
    val title: String,
    val category: String,
    val durationSeconds: Int,
    val bpm: Int,
    val accuracyPct: Int
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val totalXp: Int = 0,
    val currentStreakDays: Int = 1,
    val longestStreakDays: Int = 1,
    val lastActiveDate: String = ""
)
