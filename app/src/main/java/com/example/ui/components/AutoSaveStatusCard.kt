package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.IncomeGreen

@Composable
fun AutoSaveStatusCard(
    statusMessage: String?,
    onOpenBackupDialog: () -> Unit,
    onQuickExportPdf: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("auto_save_box"),
        colors = CardDefaults.cardColors(
            containerColor = EmeraldPrimary.copy(alpha = 0.06f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(EmeraldPrimary.copy(alpha = 0.25f))
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(IncomeGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "التخزين التلقائي على الهاتف",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = EmeraldPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = IncomeGreen.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = IncomeGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "مفعل ومحمي محلياً",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = IncomeGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "يتم حفظ جميع عمليات الدخل والخرجيات لحظياً ومباشرة داخل ذاكرة الهاتف (قاعدة بيانات Room SQLite محلية ومشفرة). لا تضيع بياناتك أبداً.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )

            AnimatedVisibility(
                visible = !statusMessage.isNullOrBlank(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .border(1.dp, EmeraldPrimary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = statusMessage ?: "",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons (Backup & PDF)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = onOpenBackupDialog,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("backup_button"),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = EmeraldPrimary.copy(alpha = 0.15f),
                        contentColor = EmeraldPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.SdStorage, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("نسخة احتياطية", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                FilledTonalButton(
                    onClick = onQuickExportPdf,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("export_pdf_button"),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = GoldAccent.copy(alpha = 0.15f),
                        contentColor = GoldAccent
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تصدير PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
