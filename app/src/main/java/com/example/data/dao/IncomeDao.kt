package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.IncomeEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {
    @Query("SELECT * FROM incomes ORDER BY timestamp DESC")
    fun getAllIncomes(): Flow<List<IncomeEntry>>

    @Query("SELECT * FROM incomes WHERE dateString = :dateStr ORDER BY timestamp DESC")
    fun getIncomesForDate(dateStr: String): Flow<List<IncomeEntry>>

    @Query("SELECT * FROM incomes WHERE dateString >= :startDate AND dateString <= :endDate ORDER BY dateString ASC, timestamp ASC")
    fun getIncomesBetweenDates(startDate: String, endDate: String): Flow<List<IncomeEntry>>

    @Query("SELECT SUM(amount) FROM incomes WHERE dateString = :dateStr")
    fun getTodayTotalIncome(dateStr: String): Flow<Double?>

    @Query("SELECT SUM(hoursWorked) FROM incomes WHERE dateString = :dateStr")
    fun getTodayTotalHours(dateStr: String): Flow<Double?>

    @Query("SELECT SUM(amount) FROM incomes")
    fun getAllTimeTotalIncome(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: IncomeEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(incomes: List<IncomeEntry>)

    @Update
    suspend fun updateIncome(income: IncomeEntry)

    @Delete
    suspend fun deleteIncome(income: IncomeEntry)

    @Query("DELETE FROM incomes WHERE id = :id")
    suspend fun deleteIncomeById(id: Long)

    @Query("DELETE FROM incomes")
    suspend fun clearAll()
}
