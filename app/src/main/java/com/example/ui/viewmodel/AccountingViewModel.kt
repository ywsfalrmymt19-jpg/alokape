package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AccountingDatabase
import com.example.data.model.ExpenseEntry
import com.example.data.model.IncomeEntry
import com.example.data.repository.AccountingRepository
import com.example.utils.BackupManager
import com.example.utils.DateUtils
import com.example.utils.PdfReportGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ReportPeriodType {
    WEEKLY, MONTHLY, YEARLY
}

enum class CurrentScreen {
    HOME, DAILY_INCOME, DAILY_EXPENSES, REPORTS
}

class AccountingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AccountingRepository

    init {
        val db = AccountingDatabase.getDatabase(application)
        repository = AccountingRepository(db.incomeDao(), db.expenseDao())
    }

    // Screen Navigation
    private val _currentScreen = MutableStateFlow(CurrentScreen.HOME)
    val currentScreen: StateFlow<CurrentScreen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: CurrentScreen) {
        _currentScreen.value = screen
    }

    // Selected Date for Income/Expense screens
    private val _selectedDate = MutableStateFlow(DateUtils.getTodayIsoDate())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    fun setSelectedDate(dateIso: String) {
        _selectedDate.value = dateIso
    }

    // Auto-save notification feedback
    private val _autoSaveStatus = MutableStateFlow<String?>("جاهز - متصل بذاكرة الهاتف")
    val autoSaveStatus: StateFlow<String?> = _autoSaveStatus.asStateFlow()

    // All records
    val allIncomes: StateFlow<List<IncomeEntry>> = repository.allIncomes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExpenses: StateFlow<List<ExpenseEntry>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected date records
    val incomesForSelectedDate: StateFlow<List<IncomeEntry>> = _selectedDate
        .flatMapLatest { date -> repository.getIncomesForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expensesForSelectedDate: StateFlow<List<ExpenseEntry>> = _selectedDate
        .flatMapLatest { date -> repository.getExpensesForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Today totals
    val todayIso = DateUtils.getTodayIsoDate()
    val todayTotalIncome: StateFlow<Double> = repository.getTodayTotalIncome(todayIso)
        .combine(MutableStateFlow(0.0)) { total, _ -> total ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayTotalHours: StateFlow<Double> = repository.getTodayTotalHours(todayIso)
        .combine(MutableStateFlow(0.0)) { total, _ -> total ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todayTotalExpense: StateFlow<Double> = repository.getTodayTotalExpense(todayIso)
        .combine(MutableStateFlow(0.0)) { total, _ -> total ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // All time totals
    val allTimeIncome: StateFlow<Double> = repository.allTimeTotalIncome
        .combine(MutableStateFlow(0.0)) { total, _ -> total ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val allTimeExpense: StateFlow<Double> = repository.allTimeTotalExpense
        .combine(MutableStateFlow(0.0)) { total, _ -> total ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Income Operations
    fun addIncome(hours: Double, workType: String, amount: Double, dateStr: String, notes: String = "") {
        viewModelScope.launch {
            val entry = IncomeEntry(
                hoursWorked = hours,
                workType = workType.trim(),
                amount = amount,
                dateString = dateStr,
                notes = notes.trim()
            )
            repository.insertIncome(entry)
            _autoSaveStatus.value = "تم الحفظ تلقائياً في ذاكرة الهاتف ✓ (${DateUtils.formatAmount(amount)} ريال/عملة)"
        }
    }

    fun updateIncome(income: IncomeEntry) {
        viewModelScope.launch {
            repository.updateIncome(income)
            _autoSaveStatus.value = "تم تحديث السجل تلقائياً في الهاتف ✓"
        }
    }

    fun deleteIncome(income: IncomeEntry) {
        viewModelScope.launch {
            repository.deleteIncome(income)
            _autoSaveStatus.value = "تم حذف السجل وتحديث قاعدة البيانات ✓"
        }
    }

    // Expense Operations
    fun addExpense(expenseType: String, amount: Double, dateStr: String, notes: String = "") {
        viewModelScope.launch {
            val entry = ExpenseEntry(
                expenseType = expenseType.trim(),
                amount = amount,
                dateString = dateStr,
                notes = notes.trim()
            )
            repository.insertExpense(entry)
            _autoSaveStatus.value = "تم حفظ المصروف تلقائياً في ذاكرة الهاتف ✓ (${DateUtils.formatAmount(amount)})"
        }
    }

    fun updateExpense(expense: ExpenseEntry) {
        viewModelScope.launch {
            repository.updateExpense(expense)
            _autoSaveStatus.value = "تم تحديث المصروف في قاعدة بيانات الهاتف ✓"
        }
    }

    fun deleteExpense(expense: ExpenseEntry) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            _autoSaveStatus.value = "تم حذف المصروف من ذاكرة الهاتف ✓"
        }
    }

    // PDF Report Export
    fun exportPdfReport(context: Context, periodType: ReportPeriodType) {
        viewModelScope.launch {
            val (title, periodName, filteredIncomes, filteredExpenses) = when (periodType) {
                ReportPeriodType.WEEKLY -> {
                    val (start, end) = DateUtils.getWeekDateRange()
                    val incs = allIncomes.value.filter { it.dateString in start..end }
                    val exps = allExpenses.value.filter { it.dateString in start..end }
                    Quadruple("التقرير الأسبوعي", "آخر ٧ أيام ($start إلى $end)", incs, exps)
                }
                ReportPeriodType.MONTHLY -> {
                    val (start, end) = DateUtils.getMonthDateRange()
                    val incs = allIncomes.value.filter { it.dateString in start..end }
                    val exps = allExpenses.value.filter { it.dateString in start..end }
                    Quadruple("التقرير الشهري", DateUtils.getCurrentMonthDisplay(), incs, exps)
                }
                ReportPeriodType.YEARLY -> {
                    val currentYear = DateUtils.getCurrentYear()
                    val (start, end) = DateUtils.getYearDateRange(currentYear)
                    val incs = allIncomes.value.filter { it.dateString in start..end }
                    val exps = allExpenses.value.filter { it.dateString in start..end }
                    Quadruple("التقرير السنوي", "سنة $currentYear", incs, exps)
                }
            }

            PdfReportGenerator.generateAndSharePdf(
                context = context,
                reportTitle = title,
                periodName = periodName,
                incomes = filteredIncomes,
                expenses = filteredExpenses
            )
        }
    }

    // JSON Backup Export & Restore
    fun exportBackup(context: Context) {
        val json = BackupManager.exportToJson(allIncomes.value, allExpenses.value)
        BackupManager.saveBackupFileAndShare(context, json)
    }

    fun restoreBackup(jsonString: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val parsed = BackupManager.parseJsonBackup(jsonString)
            if (parsed != null) {
                repository.restoreAllData(parsed.first, parsed.second)
                _autoSaveStatus.value = "تمت استعادة البيانات بنجاح (${parsed.first.size} دخل، ${parsed.second.size} مصاريف)"
                onComplete(true, "تمت استعادة ${parsed.first.size} قيد دخل و ${parsed.second.size} قيد خرجيات بنجاح!")
            } else {
                onComplete(false, "صيغة ملف النسخة الاحتياطية غير صالحة!")
            }
        }
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
