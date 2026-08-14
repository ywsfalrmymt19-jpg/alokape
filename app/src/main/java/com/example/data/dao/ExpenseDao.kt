package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ExpenseEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY timestamp DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntry>>

    @Query("SELECT * FROM expenses WHERE dateString = :dateStr ORDER BY timestamp DESC")
    fun getExpensesForDate(dateStr: String): Flow<List<ExpenseEntry>>

    @Query("SELECT * FROM expenses WHERE dateString >= :startDate AND dateString <= :endDate ORDER BY dateString ASC, timestamp ASC")
    fun getExpensesBetweenDates(startDate: String, endDate: String): Flow<List<ExpenseEntry>>

    @Query("SELECT SUM(amount) FROM expenses WHERE dateString = :dateStr")
    fun getTodayTotalExpense(dateStr: String): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expenses")
    fun getAllTimeTotalExpense(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(expenses: List<ExpenseEntry>)

    @Update
    suspend fun updateExpense(expense: ExpenseEntry)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntry)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: Long)

    @Query("DELETE FROM expenses")
    suspend fun clearAll()
}
