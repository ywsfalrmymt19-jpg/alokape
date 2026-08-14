package com.example.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.ExpenseEntry
import com.example.data.model.IncomeEntry
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportGenerator {

    fun generateAndSharePdf(
        context: Context,
        reportTitle: String,
        periodName: String,
        incomes: List<IncomeEntry>,
        expenses: List<ExpenseEntry>
    ): File? {
        try {
            val totalIncome = incomes.sumOf { it.amount }
            val totalHours = incomes.sumOf { it.hoursWorked }
            val totalExpenses = expenses.sumOf { it.amount }
            val netBalance = totalIncome - totalExpenses

            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 standard size
            val page = pdfDocument.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            // Paints
            val textPaint = Paint().apply {
                isAntiAlias = true
                textSize = 12f
                color = Color.DKGRAY
            }

            val titlePaint = Paint().apply {
                isAntiAlias = true
                textSize = 20f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                color = Color.rgb(15, 81, 50) // Emerald Primary
                textAlign = Paint.Align.CENTER
            }

            val subTitlePaint = Paint().apply {
                isAntiAlias = true
                textSize = 12f
                color = Color.rgb(100, 116, 139)
                textAlign = Paint.Align.CENTER
            }

            val headerBgPaint = Paint().apply {
                color = Color.rgb(209, 231, 221) // Emerald Container
                style = Paint.Style.FILL
            }

            val summaryCardPaint = Paint().apply {
                color = Color.rgb(241, 245, 249)
                style = Paint.Style.FILL
            }

            val tableHeaderPaint = Paint().apply {
                color = Color.rgb(15, 81, 50)
                style = Paint.Style.FILL
            }

            val tableHeaderTextPaint = Paint().apply {
                isAntiAlias = true
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                color = Color.WHITE
            }

            val linePaint = Paint().apply {
                color = Color.rgb(226, 232, 240)
                strokeWidth = 1f
            }

            val width = 595f
            var currentY = 40f

            // Top Header
            canvas.drawText("تطبيق العكابي المحاسبي", width / 2f, currentY, titlePaint)
            currentY += 20f

            canvas.drawText("التقرير المالي - $reportTitle ($periodName)", width / 2f, currentY, subTitlePaint)
            currentY += 16f

            val devInfo = "صاحب الحساب: يوسف | برمجة د/ مالك الهيمة - هاتف: 771134103"
            canvas.drawText(devInfo, width / 2f, currentY, subTitlePaint)
            currentY += 25f

            // Summary Card (4 metrics)
            val cardRect = RectF(30f, currentY, width - 30f, currentY + 70f)
            canvas.drawRoundRect(cardRect, 10f, 10f, summaryCardPaint)

            val metricTitlePaint = Paint().apply {
                isAntiAlias = true
                textSize = 10f
                color = Color.rgb(100, 116, 139)
                textAlign = Paint.Align.CENTER
            }

            val metricValPaint = Paint().apply {
                isAntiAlias = true
                textSize = 13f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }

            val colWidth = (width - 60f) / 4f

            // 1. Total Income
            metricValPaint.color = Color.rgb(21, 128, 61)
            canvas.drawText("إجمالي الدخل", 30f + colWidth * 0.5f, currentY + 25f, metricTitlePaint)
            canvas.drawText(DateUtils.formatAmount(totalIncome), 30f + colWidth * 0.5f, currentY + 50f, metricValPaint)

            // 2. Total Hours
            metricValPaint.color = Color.rgb(29, 78, 216)
            canvas.drawText("إجمالي الساعات", 30f + colWidth * 1.5f, currentY + 25f, metricTitlePaint)
            canvas.drawText("${DateUtils.formatAmount(totalHours)} س", 30f + colWidth * 1.5f, currentY + 50f, metricValPaint)

            // 3. Total Expenses
            metricValPaint.color = Color.rgb(185, 28, 28)
            canvas.drawText("إجمالي المصاريف", 30f + colWidth * 2.5f, currentY + 25f, metricTitlePaint)
            canvas.drawText(DateUtils.formatAmount(totalExpenses), 30f + colWidth * 2.5f, currentY + 50f, metricValPaint)

            // 4. Net Balance
            metricValPaint.color = if (netBalance >= 0) Color.rgb(21, 128, 61) else Color.rgb(185, 28, 28)
            canvas.drawText("الصافي (الراسب)", 30f + colWidth * 3.5f, currentY + 25f, metricTitlePaint)
            canvas.drawText(DateUtils.formatAmount(netBalance), 30f + colWidth * 3.5f, currentY + 50f, metricValPaint)

            currentY += 90f

            // Section 1: Income Entries
            val sectionPaint = Paint().apply {
                isAntiAlias = true
                textSize = 14f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                color = Color.rgb(15, 81, 50)
            }

            canvas.drawText("١. كشف الدخل والعمل:", 35f, currentY, sectionPaint)
            currentY += 15f

            // Income Table Header
            canvas.drawRect(30f, currentY, width - 30f, currentY + 24f, tableHeaderPaint)
            canvas.drawText("التاريخ", 40f, currentY + 16f, tableHeaderTextPaint)
            canvas.drawText("نوع العمل", 160f, currentY + 16f, tableHeaderTextPaint)
            canvas.drawText("الساعات", 340f, currentY + 16f, tableHeaderTextPaint)
            canvas.drawText("المبلغ", 460f, currentY + 16f, tableHeaderTextPaint)
            currentY += 24f

            val rowPaint = Paint().apply {
                isAntiAlias = true
                textSize = 10.5f
                color = Color.rgb(30, 41, 59)
            }

            if (incomes.isEmpty()) {
                canvas.drawText("لا توجد قيود دخل في هذه الفترة", 40f, currentY + 18f, textPaint)
                currentY += 24f
            } else {
                incomes.take(15).forEach { inc ->
                    canvas.drawLine(30f, currentY, width - 30f, currentY, linePaint)
                    canvas.drawText(inc.dateString, 40f, currentY + 16f, rowPaint)
                    canvas.drawText(inc.workType, 160f, currentY + 16f, rowPaint)
                    canvas.drawText("${inc.hoursWorked} ساعة", 340f, currentY + 16f, rowPaint)
                    canvas.drawText(DateUtils.formatAmount(inc.amount), 460f, currentY + 16f, rowPaint)
                    currentY += 22f
                }
            }

            currentY += 15f

            // Section 2: Expense Entries
            val expSectionPaint = Paint().apply {
                isAntiAlias = true
                textSize = 14f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                color = Color.rgb(185, 28, 28)
            }
            canvas.drawText("٢. كشف المصاريف والخرجيات:", 35f, currentY, expSectionPaint)
            currentY += 15f

            val expHeaderPaint = Paint().apply {
                color = Color.rgb(185, 28, 28)
                style = Paint.Style.FILL
            }
            canvas.drawRect(30f, currentY, width - 30f, currentY + 24f, expHeaderPaint)
            canvas.drawText("التاريخ", 40f, currentY + 16f, tableHeaderTextPaint)
            canvas.drawText("نوع الخرج / البيان", 180f, currentY + 16f, tableHeaderTextPaint)
            canvas.drawText("المبلغ", 460f, currentY + 16f, tableHeaderTextPaint)
            currentY += 24f

            if (expenses.isEmpty()) {
                canvas.drawText("لا توجد قيود خرجيات في هذه الفترة", 40f, currentY + 18f, textPaint)
                currentY += 24f
            } else {
                expenses.take(15).forEach { exp ->
                    canvas.drawLine(30f, currentY, width - 30f, currentY, linePaint)
                    canvas.drawText(exp.dateString, 40f, currentY + 16f, rowPaint)
                    canvas.drawText(exp.expenseType, 180f, currentY + 16f, rowPaint)
                    canvas.drawText(DateUtils.formatAmount(exp.amount), 460f, currentY + 16f, rowPaint)
                    currentY += 22f
                }
            }

            // Footer
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())
            val footerPaint = Paint().apply {
                isAntiAlias = true
                textSize = 9f
                color = Color.GRAY
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("تم استخراج هذا التقرير تلقائياً بواسطة تطبيق العكابي المحاسبي في $timestamp", width / 2f, 820f, footerPaint)

            pdfDocument.finishPage(page)

            // Save PDF to cache dir
            val dir = File(context.cacheDir, "reports")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, "AlOkabi_Report_${System.currentTimeMillis()}.pdf")
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDocument.close()

            // Open Share Intent
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "تقرير مالي - تطبيق العكابي المحاسبي")
                putExtra(Intent.EXTRA_TEXT, "مرفق التقرير المالي الصادر من تطبيق العكابي المحاسبي - يوسف.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "مشاركة أو طباعة تقرير PDF")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)

            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
