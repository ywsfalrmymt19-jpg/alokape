package com.example.data.repository

import com.example.data.dao.ExpenseDao
import com.example.data.dao.IncomeDao
import com.example.data.model.ExpenseEntry
import com.example.data.model.IncomeEntry
import kotlinx.coroutines.flow.Flow

class AccountingRepository(
    private val incomeDao: IncomeDao,
    private val expenseDao: ExpenseDao
) {
    // Incomes
    val allIncomes: Flow<List<IncomeEntry>> = incomeDao.getAllIncomes()
    val allTimeTotalIncome: Flow<Double?> = incomeDao.getAllTimeTotalIncome()

    fun getIncomesForDate(dateStr: String): Flow<List<IncomeEntry>> =
        incomeDao.getIncomesForDate(dateStr)

    fun getIncomesBetweenDates(startDate: String, endDate: String): Flow<List<IncomeEntry>> =
        incomeDao.getIncomesBetweenDates(startDate, endDate)

    fun getTodayTotalIncome(dateStr: String): Flow<Double?> =
        incomeDao.getTodayTotalIncome(dateStr)

    fun getTodayTotalHours(dateStr: String): Flow<Double?> =
        incomeDao.getTodayTotalHours(dateStr)

    suspend fun insertIncome(income: IncomeEntry): Long =
        incomeDao.insertIncome(income)

    suspend fun updateIncome(income: IncomeEntry) =
        incomeDao.updateIncome(income)

    suspend fun deleteIncome(income: IncomeEntry) =
        incomeDao.deleteIncome(income)

    suspend fun deleteIncomeById(id: Long) =
        incomeDao.deleteIncomeById(id)

    // Expenses
    val allExpenses: Flow<List<ExpenseEntry>> = expenseDao.getAllExpenses()
    val allTimeTotalExpense: Flow<Double?> = expenseDao.getAllTimeTotalExpense()

    fun getExpensesForDate(dateStr: String): Flow<List<ExpenseEntry>> =
        expenseDao.getExpensesForDate(dateStr)

    fun getExpensesBetweenDates(startDate: String, endDate: String): Flow<List<ExpenseEntry>> =
        expenseDao.getExpensesBetweenDates(startDate, endDate)

    fun getTodayTotalExpense(dateStr: String): Flow<Double?> =
        expenseDao.getTodayTotalExpense(dateStr)

    suspend fun insertExpense(expense: ExpenseEntry): Long =
        expenseDao.insertExpense(expense)

    suspend fun updateExpense(expense: ExpenseEntry) =
        expenseDao.updateExpense(expense)

    suspend fun deleteExpense(expense: ExpenseEntry) =
        expenseDao.deleteExpense(expense)

    suspend fun deleteExpenseById(id: Long) =
        expenseDao.deleteExpenseById(id)

    // Backup & Restore
    suspend fun restoreAllData(incomes: List<IncomeEntry>, expenses: List<ExpenseEntry>) {
        incomeDao.clearAll()
        expenseDao.clearAll()
        incomeDao.insertAll(incomes)
        expenseDao.insertAll(expenses)
    }

    suspend fun insertSampleDataIfEmpty(sampleIncomes: List<IncomeEntry>, sampleExpenses: List<ExpenseEntry>) {
        incomeDao.insertAll(sampleIncomes)
        expenseDao.insertAll(sampleExpenses)
    }
}
