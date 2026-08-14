package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.AutoSaveStatusCard
import com.example.ui.components.BackupRestoreDialog
import com.example.ui.components.DeveloperProfileDialog
import com.example.ui.components.UserProfileDialog
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryDark
import com.example.ui.theme.EmeraldPrimaryLight
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedContainer
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenContainer
import com.example.ui.viewmodel.AccountingViewModel
import com.example.ui.viewmodel.CurrentScreen
import com.example.ui.viewmodel.ReportPeriodType
import com.example.utils.DateUtils

@Composable
fun HomeScreen(
    viewModel: AccountingViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val todayIncome by viewModel.todayTotalIncome.collectAsStateWithLifecycle()
    val todayHours by viewModel.todayTotalHours.collectAsStateWithLifecycle()
    val todayExpense by viewModel.todayTotalExpense.collectAsStateWithLifecycle()
    val allTimeIncome by viewModel.allTimeIncome.collectAsStateWithLifecycle()
    val allTimeExpense by viewModel.allTimeExpense.collectAsStateWithLifecycle()
    val autoSaveStatus by viewModel.autoSaveStatus.collectAsStateWithLifecycle()

    val todayNet = todayIncome - todayExpense
    val allTimeNet = allTimeIncome - allTimeExpense

    var showDeveloperDialog by remember { mutableStateOf(false) }
    var showUserDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }

    if (showDeveloperDialog) {
        DeveloperProfileDialog(onDismiss = { showDeveloperDialog = false })
    }

    if (showUserDialog) {
        UserProfileDialog(onDismiss = { showUserDialog = false })
    }

    if (showBackupDialog) {
        BackupRestoreDialog(
            onDismiss = { showBackupDialog = false },
            onExportBackup = { viewModel.exportBackup(context) },
            onRestoreBackup = { json ->
                viewModel.restoreBackup(json) { success, msg ->
                    android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldPrimary)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(EmeraldPrimaryDark, EmeraldPrimary, EmeraldPrimaryLight)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "تطبيق العكابي المحاسبي",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "إدارة الدخل والخرجيات والتقارير المالية الذكية",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF34D399))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = DateUtils.formatIsoToDisplay(DateUtils.getTodayIsoDate()),
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        // Logo icon
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(2.dp, GoldAccent, RoundedCornerShape(16.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_app_logo),
                                contentDescription = "شعار التطبيق",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize()
                            )
                        }
                    }
                }
            }
        }

        // Top Row: 3 Cards matching the hand-drawn sketch
        // 1. برمجة الدكتور مالك الهيمة | 2. صورة باسم يوسف | 3. التقارير
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: برمجة الدكتور مالك الهيمة
                ElevatedCard(
                    onClick = { showDeveloperDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(125.dp)
                        .testTag("developer_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeveloperMode,
                                contentDescription = "المطور",
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "برمجة الدكتور",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "مالك الهيمة",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Card 2: صورة باسم يوسف
                ElevatedCard(
                    onClick = { showUserDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(125.dp)
                        .testTag("user_profile_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .border(2.dp, GoldAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.youssef_photo),
                                contentDescription = "صورة يوسف",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "صورة باسم",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "يوسف",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Card 3: التقارير
                ElevatedCard(
                    onClick = { viewModel.navigateTo(CurrentScreen.REPORTS) },
                    modifier = Modifier
                        .weight(1f)
                        .height(125.dp)
                        .testTag("reports_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(GoldAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = "التقارير",
                                tint = GoldAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "كشف وحسابات",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "التقارير",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Live Financial Summary Strip
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Today Net Balance
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "صافي اليوم (الراسب)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = DateUtils.formatAmount(todayNet),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (todayNet >= 0) IncomeGreen else ExpenseRed
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    // All time net balance
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "إجمالي الصافي العام", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = DateUtils.formatAmount(allTimeNet),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (allTimeNet >= 0) EmeraldPrimary else ExpenseRed
                        )
                    }
                }
            }
        }

        // Bottom Row: 2 Big Cards matching the hand-drawn sketch
        // 1. الدخل اليومي | 2. الخرجيات اليومي
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Big Card 1: الدخل اليومي (Daily Income)
                ElevatedCard(
                    onClick = { viewModel.navigateTo(CurrentScreen.DAILY_INCOME) },
                    modifier = Modifier
                        .weight(1f)
                        .height(180.dp)
                        .testTag("daily_income_big_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = IncomeGreenContainer.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(IncomeGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "الدخل اليومي",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = IncomeGreen,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = DateUtils.formatAmount(todayIncome),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "إجمالي الساعات: ${DateUtils.formatAmount(todayHours)} س",
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = IncomeGreen,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "تسجيل وعرض الدخل ⬅",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }

                // Big Card 2: الخرجيات اليومي (Daily Expenses)
                ElevatedCard(
                    onClick = { viewModel.navigateTo(CurrentScreen.DAILY_EXPENSES) },
                    modifier = Modifier
                        .weight(1f)
                        .height(180.dp)
                        .testTag("daily_expenses_big_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = ExpenseRedContainer.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ExpenseRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingDown,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "الخرجيات اليومي",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = ExpenseRed,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = DateUtils.formatAmount(todayExpense),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "المصاريف والنفقات",
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = ExpenseRed,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "تسجيل وعرض الخرج ⬅",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Auto-Save Status Box (Kotak penyimpanan otomatis ke telepon)
        item {
            AutoSaveStatusCard(
                statusMessage = autoSaveStatus,
                onOpenBackupDialog = { showBackupDialog = true },
                onQuickExportPdf = { viewModel.exportPdfReport(context, ReportPeriodType.WEEKLY) }
            )
        }

        // Footer matching the hand-written note:
        // "تصميم وبرمجة د/ مالك الهيمة هاتف 771134103"
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:771134103")).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    }
                    .testTag("footer_developer_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "اتصال",
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تصميم وبرمجة د/ مالك الهيمة - هاتف: 771134103",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
