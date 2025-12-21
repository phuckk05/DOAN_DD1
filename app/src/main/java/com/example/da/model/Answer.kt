package com.example.da.model

data class Answer(
    val id: Int = 0,
    val questionId: Int,
    val answerText: String,
    val isCorrect: Boolean
)

