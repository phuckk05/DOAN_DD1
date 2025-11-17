package com.example.da.model

data class Question(
    val id: Int,
    val subjectId: Int,
    val text: String,
    val difficulty: String,
    val isMultipleChoice: Boolean = false
)
