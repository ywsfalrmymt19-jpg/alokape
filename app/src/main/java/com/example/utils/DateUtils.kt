package com.example.utils

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val arabicDisplayFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("ar"))
    private val shortDisplayFormat = SimpleDateFormat("d MMM yyyy", Locale("ar"))
    private val monthFormat = SimpleDateFormat("MMMM yyyy", Locale("ar"))
    private val monthIsoFormat = SimpleDateFormat("yyyy-MM", Locale.US)
    private val yearIsoFormat = SimpleDateFormat("yyyy", Locale.US)

    private val numberFormatter = DecimalFormat("#,##0.##", DecimalFormatSymbols(Locale.US))

    fun getTodayIsoDate(): String {
        return isoFormat.format(Date())
    }

    fun formatIsoToDisplay(isoDate: String): String {
        return try {
            val date = isoFormat.parse(isoDate) ?: return isoDate
            val todayIso = getTodayIsoDate()
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayIso = isoFormat.format(cal.time)

            when (isoDate) {
                todayIso -> "اليوم (${shortDisplayFormat.format(date)})"
                yesterdayIso -> "أمس (${shortDisplayFormat.format(date)})"
                else -> arabicDisplayFormat.format(date)
            }
        } catch (e: Exception) {
            isoDate
        }
    }

    fun formatShortDate(isoDate: String): String {
        return try {
            val date = isoFormat.parse(isoDate) ?: return isoDate
            shortDisplayFormat.format(date)
        } catch (e: Exception) {
            isoDate
        }
    }

    fun formatAmount(amount: Double): String {
        return numberFormatter.format(amount)
    }

    fun getWeekDateRange(): Pair<String, String> {
        val cal = Calendar.getInstance()
        val endIso = isoFormat.format(cal.time)
        cal.add(Calendar.DAY_OF_YEAR, -6)
        val startIso = isoFormat.format(cal.time)
        return Pair(startIso, endIso)
    }

    fun getMonthDateRange(cal: Calendar = Calendar.getInstance()): Pair<String, String> {
        val clone = cal.clone() as Calendar
        clone.set(Calendar.DAY_OF_MONTH, 1)
        val startIso = isoFormat.format(clone.time)
        clone.set(Calendar.DAY_OF_MONTH, clone.getActualMaximum(Calendar.DAY_OF_MONTH))
        val endIso = isoFormat.format(clone.time)
        return Pair(startIso, endIso)
    }

    fun getYearDateRange(year: Int = Calendar.getInstance().get(Calendar.YEAR)): Pair<String, String> {
        val startIso = "$year-01-01"
        val endIso = "$year-12-31"
        return Pair(startIso, endIso)
    }

    fun getCurrentMonthDisplay(cal: Calendar = Calendar.getInstance()): String {
        return monthFormat.format(cal.time)
    }

    fun getCurrentYear(cal: Calendar = Calendar.getInstance()): Int {
        return cal.get(Calendar.YEAR)
    }
}
