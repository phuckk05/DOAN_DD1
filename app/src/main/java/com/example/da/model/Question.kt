package com.example.da.model
data class Question(
    val id: Int,
    val subject: String,
    val text: String,
    val difficulty: String
)

object QuestionData {
    val allQuestions = mutableListOf<Question>()
}