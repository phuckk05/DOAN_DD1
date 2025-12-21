package com.example.da.model

data class TestResult(
    val id: Int = 0,
    val testId: Int,
    val score: Int,
    val timeTakenSeconds: Int,
    val timestamp: Long
)

