package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DrumDao {
    // Exercises
    @Query("SELECT * FROM exercise_progress")
    fun getAllExerciseProgress(): Flow<List<ExerciseProgressEntity>>

    @Query("SELECT * FROM exercise_progress WHERE exerciseId = :exerciseId")
    suspend fun getExerciseProgress(exerciseId: String): ExerciseProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveExerciseProgress(progress: ExerciseProgressEntity)

    // Lessons
    @Query("SELECT * FROM lesson_progress")
    fun getAllLessonProgress(): Flow<List<LessonProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLessonProgress(progress: LessonProgressEntity)

    // Practice Sessions
    @Query("SELECT * FROM practice_sessions ORDER BY timestamp DESC LIMIT 50")
    fun getRecentPracticeSessions(): Flow<List<PracticeSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPracticeSession(session: PracticeSessionEntity)

    // Profile & Streak
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)
}
