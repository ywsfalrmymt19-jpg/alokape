package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.ExpenseEntry
import com.example.data.model.IncomeEntry
import com.example.ui.screens.DailyExpenseScreen
import com.example.ui.screens.DailyIncomeScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AccountingViewModel
import com.example.ui.viewmodel.CurrentScreen
import com.example.utils.DateUtils

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        // Enforce RTL layout for Arabic accounting app
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
          AccountingApp()
        }
      }
    }
  }
}

@Composable
fun AccountingApp(
    viewModel: AccountingViewModel = viewModel()
) {
  val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
  val allIncomes by viewModel.allIncomes.collectAsStateWithLifecycle()
  val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()

  // Seed sample initial records once if database is fresh
  LaunchedEffect(allIncomes, allExpenses) {
    if (allIncomes.isEmpty() && allExpenses.isEmpty()) {
      val today = DateUtils.getTodayIsoDate()
      viewModel.addIncome(8.0, "مقاولات ونقليات", 35000.0, today, "أعمال اليوم الأول")
      viewModel.addIncome(6.5, "أعمال حرة وتجارة", 22000.0, today, "دفعة نقدية")
      viewModel.addExpense("ديزل / وقود", 8000.0, today, "تعبئة خزان الشاحنة")
      viewModel.addExpense("طعام ومصروف", 2500.0, today, "وجبة غداء ومصاريف يومية")
    }
  }

  Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
    Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
      when (screen) {
        CurrentScreen.HOME -> HomeScreen(
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding)
        )
        CurrentScreen.DAILY_INCOME -> DailyIncomeScreen(
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding)
        )
        CurrentScreen.DAILY_EXPENSES -> DailyExpenseScreen(
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding)
        )
        CurrentScreen.REPORTS -> ReportsScreen(
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding)
        )
      }
    }
  }
}

