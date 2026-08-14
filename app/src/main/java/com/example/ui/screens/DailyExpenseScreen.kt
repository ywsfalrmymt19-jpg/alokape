package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ExpenseEntry
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedContainer
import com.example.ui.viewmodel.AccountingViewModel
import com.example.ui.viewmodel.CurrentScreen
import com.example.utils.DateUtils
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DailyExpenseScreen(
    viewModel: AccountingViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val expenses by viewModel.expensesForSelectedDate.collectAsStateWithLifecycle()

    val dayTotalExpense = expenses.sumOf { it.amount }

    var showAddSheet by remember { mutableStateOf(false) }
    var entryToEdit by remember { mutableStateOf<ExpenseEntry?>(null) }
    var entryToDelete by remember { mutableStateOf<ExpenseEntry?>(null) }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formatted = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            viewModel.setSelectedDate(formatted)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "الخرجيات اليومي",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(CurrentScreen.HOME) },
                        modifier = Modifier.testTag("back_to_home_from_expense_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ExpenseRed)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    entryToEdit = null
                    showAddSheet = true
                },
                containerColor = ExpenseRed,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_expense_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة قيد خرجيات")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Date Selector Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { datePickerDialog.show() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "اختيار التاريخ",
                            tint = ExpenseRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = DateUtils.formatIsoToDisplay(selectedDate),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (selectedDate != DateUtils.getTodayIsoDate()) {
                        OutlinedButton(
                            onClick = { viewModel.setSelectedDate(DateUtils.getTodayIsoDate()) },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("العودة لليوم", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Summary Card for Selected Date
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ExpenseRedContainer.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "إجمالي مصاريف هذا اليوم", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = DateUtils.formatAmount(dayTotalExpense),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = ExpenseRed
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "عدد القيود", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "${expenses.size} مصاريف",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Expense Entries List
            if (expenses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "لا توجد مصاريف أو خرجيات مسجلة لهذا التاريخ",
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "اضغط على زر (+) لإضافة قيد جديد",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(expenses, key = { it.id }) { expense ->
                        ExpenseItemCard(
                            expense = expense,
                            onEdit = {
                                entryToEdit = expense
                                showAddSheet = true
                            },
                            onDelete = { entryToDelete = expense }
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    entryToDelete?.let { expense ->
        AlertDialog(
            onDismissRequest = { entryToDelete = null },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteExpense(expense)
                        entryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { entryToDelete = null }) {
                    Text("إلغاء")
                }
            },
            title = { Text("تأكيد حذف قيد المصروف") },
            text = { Text("هل أنت متأكد من حذف مصروف (${expense.expenseType} - ${DateUtils.formatAmount(expense.amount)}) من قاعدة بيانات الهاتف؟") }
        )
    }

    // Add / Edit Expense Bottom Sheet
    if (showAddSheet) {
        ExpenseFormBottomSheet(
            selectedDate = selectedDate,
            initialExpense = entryToEdit,
            onDismiss = { showAddSheet = false },
            onSave = { expenseType, amount, notes ->
                if (entryToEdit != null) {
                    viewModel.updateExpense(
                        entryToEdit!!.copy(
                            expenseType = expenseType,
                            amount = amount,
                            notes = notes
                        )
                    )
                } else {
                    viewModel.addExpense(expenseType, amount, selectedDate, notes)
                }
                showAddSheet = false
            }
        )
    }
}

@Composable
fun ExpenseItemCard(
    expense: ExpenseEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("expense_item_${expense.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ExpenseRed.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = ExpenseRed,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = expense.expenseType,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (expense.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = expense.notes,
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = DateUtils.formatAmount(expense.amount),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ExpenseRed
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "تعديل", tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(18.dp))
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExpenseFormBottomSheet(
    selectedDate: String,
    initialExpense: ExpenseEntry?,
    onDismiss: () -> Unit,
    onSave: (expenseType: String, amount: Double, notes: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var expenseTypeText by remember { mutableStateOf(initialExpense?.expenseType ?: "ديزل / وقود") }
    var amountText by remember { mutableStateOf(initialExpense?.amount?.toString() ?: "") }
    var notesText by remember { mutableStateOf(initialExpense?.notes ?: "") }

    val quickExpenseTypes = listOf(
        "ديزل / وقود",
        "صيانة وقطع",
        "طعام ومصروف",
        "أجور عمال",
        "إيجار",
        "فواتير وكهرباء",
        "مشتريات عامة",
        "أخرى"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = if (initialExpense == null) "إضافة قيد خرجيات يومي" else "تعديل قيد الخرجيات",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ExpenseRed
            )
            Text(
                text = "التاريخ: ${DateUtils.formatIsoToDisplay(selectedDate)}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Category Chips
            Text(text = "نوع الخرج / البيان:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                quickExpenseTypes.forEach { type ->
                    FilterChip(
                        selected = expenseTypeText == type,
                        onClick = { expenseTypeText = type },
                        label = { Text(type, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ExpenseRed.copy(alpha = 0.2f),
                            selectedLabelColor = ExpenseRed
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Field: Expense Type Custom
            OutlinedTextField(
                value = expenseTypeText,
                onValueChange = { expenseTypeText = it },
                label = { Text("نوع الخرج / البيان") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("expense_type_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Field: Expense Amount
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("مبلغ الخرج") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("expense_amount_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Field: Notes
            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                label = { Text("ملاحظات إضافية (اختياري)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (amount > 0 && expenseTypeText.isNotBlank()) {
                        onSave(expenseTypeText, amount, notesText)
                    }
                },
                enabled = (amountText.toDoubleOrNull() ?: 0.0) > 0 && expenseTypeText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_expense_button")
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (initialExpense == null) "حفظ المصروف مباشرة في الهاتف" else "تحديث المصروف",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
