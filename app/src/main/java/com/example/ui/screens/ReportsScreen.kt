package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ExpenseEntry
import com.example.data.model.IncomeEntry
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedContainer
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenContainer
import com.example.ui.viewmodel.AccountingViewModel
import com.example.ui.viewmodel.CurrentScreen
import com.example.ui.viewmodel.ReportPeriodType
import com.example.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: AccountingViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(ReportPeriodType.WEEKLY) }

    val allIncomes by viewModel.allIncomes.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()

    // Filter data based on selected tab
    val (periodLabel, filteredIncomes, filteredExpenses) = remember(selectedTab, allIncomes, allExpenses) {
        when (selectedTab) {
            ReportPeriodType.WEEKLY -> {
                val (start, end) = DateUtils.getWeekDateRange()
                Triple("آخر ٧ أيام ($start إلى $end)", allIncomes.filter { it.dateString in start..end }, allExpenses.filter { it.dateString in start..end })
            }
            ReportPeriodType.MONTHLY -> {
                val (start, end) = DateUtils.getMonthDateRange()
                Triple(DateUtils.getCurrentMonthDisplay(), allIncomes.filter { it.dateString in start..end }, allExpenses.filter { it.dateString in start..end })
            }
            ReportPeriodType.YEARLY -> {
                val currentYear = DateUtils.getCurrentYear()
                val (start, end) = DateUtils.getYearDateRange(currentYear)
                Triple("سنة $currentYear", allIncomes.filter { it.dateString in start..end }, allExpenses.filter { it.dateString in start..end })
            }
        }
    }

    val totalIncome = filteredIncomes.sumOf { it.amount }
    val totalHours = filteredIncomes.sumOf { it.hoursWorked }
    val totalExpense = filteredExpenses.sumOf { it.amount }
    val netBalance = totalIncome - totalExpense

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "التقارير المالية",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(CurrentScreen.HOME) },
                        modifier = Modifier.testTag("back_to_home_from_reports_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.exportPdfReport(context, selectedTab) },
                        modifier = Modifier.testTag("export_report_pdf_topbar_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "تصدير PDF",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EmeraldPrimary)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 3 Tabs matching PDF 1: اسبوعي - شهري - سنوي
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = EmeraldPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                        color = EmeraldPrimary
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == ReportPeriodType.WEEKLY,
                    onClick = { selectedTab = ReportPeriodType.WEEKLY },
                    text = { Text("اسبوعي", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
                    modifier = Modifier.testTag("tab_weekly")
                )
                Tab(
                    selected = selectedTab == ReportPeriodType.MONTHLY,
                    onClick = { selectedTab = ReportPeriodType.MONTHLY },
                    text = { Text("شهري", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
                    modifier = Modifier.testTag("tab_monthly")
                )
                Tab(
                    selected = selectedTab == ReportPeriodType.YEARLY,
                    onClick = { selectedTab = ReportPeriodType.YEARLY },
                    text = { Text("سنوي", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
                    modifier = Modifier.testTag("tab_yearly")
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Period Label Banner
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = periodLabel,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Button(
                                onClick = { viewModel.exportPdfReport(context, selectedTab) },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("print_pdf_button")
                            ) {
                                Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تصدير وطباعة PDF", fontSize = 11.5.sp)
                            }
                        }
                    }
                }

                // 4 Financial Metrics Grid (Income, Hours, Expenses, Net Profit/Loss)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Total Income
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = IncomeGreenContainer.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, tint = IncomeGreen, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "إجمالي الدخل", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = DateUtils.formatAmount(totalIncome),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = IncomeGreen
                                    )
                                }
                            }

                            // Total Hours
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "إجمالي الساعات", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "${DateUtils.formatAmount(totalHours)} س",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Total Expense
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = ExpenseRedContainer.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.TrendingDown, contentDescription = null, tint = ExpenseRed, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "إجمالي المصاريف", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = DateUtils.formatAmount(totalExpense),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = ExpenseRed
                                    )
                                }
                            }

                            // Net Balance (الراسب / الصافي)
                            Card(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (netBalance >= 0) EmeraldPrimary.copy(alpha = 0.12f) else ExpenseRed.copy(alpha = 0.12f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Assessment,
                                            contentDescription = null,
                                            tint = if (netBalance >= 0) EmeraldPrimary else ExpenseRed,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "الصافي (الراسب)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = DateUtils.formatAmount(netBalance),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (netBalance >= 0) EmeraldPrimary else ExpenseRed
                                    )
                                }
                            }
                        }
                    }
                }

                // Section 1: Incomes Breakdown
                item {
                    Text(
                        text = "تفاصيل الدخل وساعات العمل (${filteredIncomes.size} سجلات):",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (filteredIncomes.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Text(
                                text = "لا توجد قيود دخل في هذه الفترة المحددة",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            )
                        }
                    }
                } else {
                    items(filteredIncomes) { income ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = income.workType, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "${income.dateString} • ${income.hoursWorked} ساعة", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    text = DateUtils.formatAmount(income.amount),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IncomeGreen
                                )
                            }
                        }
                    }
                }

                // Section 2: Expenses Breakdown
                item {
                    Text(
                        text = "تفاصيل المصاريف والخرجيات (${filteredExpenses.size} سجلات):",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ExpenseRed,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (filteredExpenses.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Text(
                                text = "لا توجد قيود مصاريف في هذه الفترة المحددة",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            )
                        }
                    }
                } else {
                    items(filteredExpenses) { expense ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = expense.expenseType, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(text = expense.dateString, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    text = DateUtils.formatAmount(expense.amount),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ExpenseRed
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
