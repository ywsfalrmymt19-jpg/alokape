package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incomes")
data class IncomeEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hoursWorked: Double, // إجمالي عدد الساعات
    val workType: String,    // نوع العمل
    val amount: Double,      // إجمالي المبلغ
    val dateString: String,  // YYYY-MM-DD
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)
