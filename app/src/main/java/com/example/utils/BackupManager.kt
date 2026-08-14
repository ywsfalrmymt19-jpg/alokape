package com.example.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.model.ExpenseEntry
import com.example.data.model.IncomeEntry
import com.example.data.repository.AccountingRepository
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BackupManager {

    fun exportToJson(incomes: List<IncomeEntry>, expenses: List<ExpenseEntry>): String {
        val root = JSONObject()
        root.put("appName", "Al-Okabi Accounting App")
        root.put("version", "1.0")
        root.put("exportDate", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date()))
        root.put("user", "يوسف")
        root.put("developer", "د/ مالك الهيمة - 771134103")

        val incomesArray = JSONArray()
        for (inc in incomes) {
            val obj = JSONObject().apply {
                put("id", inc.id)
                put("hoursWorked", inc.hoursWorked)
                put("workType", inc.workType)
                put("amount", inc.amount)
                put("dateString", inc.dateString)
                put("timestamp", inc.timestamp)
                put("notes", inc.notes)
            }
            incomesArray.put(obj)
        }
        root.put("incomes", incomesArray)

        val expensesArray = JSONArray()
        for (exp in expenses) {
            val obj = JSONObject().apply {
                put("id", exp.id)
                put("expenseType", exp.expenseType)
                put("amount", exp.amount)
                put("dateString", exp.dateString)
                put("timestamp", exp.timestamp)
                put("notes", exp.notes)
            }
            expensesArray.put(obj)
        }
        root.put("expenses", expensesArray)

        return root.toString(2)
    }

    fun parseJsonBackup(jsonString: String): Pair<List<IncomeEntry>, List<ExpenseEntry>>? {
        return try {
            val root = JSONObject(jsonString)
            val incomesList = mutableListOf<IncomeEntry>()
            val expensesList = mutableListOf<ExpenseEntry>()

            val incomesArray = root.optJSONArray("incomes") ?: JSONArray()
            for (i in 0 until incomesArray.length()) {
                val obj = incomesArray.getJSONObject(i)
                incomesList.add(
                    IncomeEntry(
                        id = 0, // Auto-generate on import
                        hoursWorked = obj.optDouble("hoursWorked", 0.0),
                        workType = obj.optString("workType", "عام"),
                        amount = obj.optDouble("amount", 0.0),
                        dateString = obj.optString("dateString", DateUtils.getTodayIsoDate()),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        notes = obj.optString("notes", "")
                    )
                )
            }

            val expensesArray = root.optJSONArray("expenses") ?: JSONArray()
            for (i in 0 until expensesArray.length()) {
                val obj = expensesArray.getJSONObject(i)
                expensesList.add(
                    ExpenseEntry(
                        id = 0,
                        expenseType = obj.optString("expenseType", "مصروف"),
                        amount = obj.optDouble("amount", 0.0),
                        dateString = obj.optString("dateString", DateUtils.getTodayIsoDate()),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        notes = obj.optString("notes", "")
                    )
                )
            }

            Pair(incomesList, expensesList)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveBackupFileAndShare(context: Context, jsonString: String): File? {
        return try {
            val dir = File(context.cacheDir, "backups")
            if (!dir.exists()) dir.mkdirs()
            val fileName = "al_okabi_backup_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.json"
            val file = File(dir, fileName)
            val fos = FileOutputStream(file)
            fos.write(jsonString.toByteArray(Charsets.UTF_8))
            fos.flush()
            fos.close()

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "نسخة احتياطية - تطبيق العكابي المحاسبي")
                putExtra(Intent.EXTRA_TEXT, "ملف النسخة الاحتياطية لتطبيق العكابي المحاسبي.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "حفظ أو إرسال النسخة الاحتياطية")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
