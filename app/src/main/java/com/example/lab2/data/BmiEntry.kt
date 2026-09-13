package com.example.lab2.data

data class BmiEntry(
    val id: Long = System.currentTimeMillis(),
    val timestamp: Long = System.currentTimeMillis(),
    val weight: Double,
    val height: Double,
    val bmi: Double,
    val gender: String,
    val category: String,
    val riskLevel: String
)
