package com.example.model

data class LessonSlide(
    val stepTitle: String,
    val description: String,
    val diagramOrNotationHint: String,
    val coachNotes: String
)

data class Lesson(
    val id: String,
    val levelNumber: Int,
    val levelName: String,
    val title: String,
    val subtitle: String,
    val instructor: String,
    val durationMin: Int,
    val overview: String,
    val keyPoints: List<String>,
    val slides: List<LessonSlide>,
    val targetExerciseId: String? = null
)
