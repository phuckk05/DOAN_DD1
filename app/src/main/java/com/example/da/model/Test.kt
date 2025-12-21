package com.example.da.model

data class Test(
    val testId: Int = 0,
    val subjectId: Int,
    val name: String,
    val numQuestions: Int, // <-- Thuộc tính này sẽ được dùng
    val durationMinutes: Int,
    val allowMultipleAnswers: Boolean,
    val easyPercent: Int,
    val mediumPercent: Int,
    val hardPercent: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val subjectName: String? = null
)
