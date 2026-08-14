package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val expenseType: String, // نوع الخرج
    val amount: Double,      // مبلغ الخرج
    val dateString: String,  // YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)
