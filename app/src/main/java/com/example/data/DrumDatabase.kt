package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ExerciseProgressEntity::class,
        LessonProgressEntity::class,
        PracticeSessionEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DrumDatabase : RoomDatabase() {
    abstract fun drumDao(): DrumDao

    companion object {
        @Volatile
        private var INSTANCE: DrumDatabase? = null

        fun getDatabase(context: Context): DrumDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DrumDatabase::class.java,
                    "beatora_drums_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
