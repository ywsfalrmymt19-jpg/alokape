package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent

@Composable
fun BackupRestoreDialog(
    onDismiss: () -> Unit,
    onExportBackup: () -> Unit,
    onRestoreBackup: (String) -> Unit
) {
    val context = LocalContext.current
    var showRestoreInput by remember { mutableStateOf(false) }
    var restoreJsonText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("إغلاق")
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Backup,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "النسخ الاحتياطي والاستعادة",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "بياناتك مخزنة بأمان في قاعدة بيانات هاتفك. يمكنك تصدير نسخة احتياطية (ملف JSON) أو استعادتها في أي وقت.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Export Button Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "تصدير نسخة احتياطية:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = EmeraldPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "إنشاء ومشاركة ملف نسخة احتياطية لجميع قيود الدخل والمصاريف",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                onExportBackup()
                                Toast.makeText(context, "جاري إعداد ملف النسخة الاحتياطية...", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("export_backup_button")
                        ) {
                            Icon(imageVector = Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("تصدير ومشاركة النسخة الآن")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Restore Button Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = GoldAccent.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "استعادة نسخة احتياطية:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = GoldAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "استيراد واسترجاع القيود من نص أو ملف النسخة الاحتياطية",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (!showRestoreInput) {
                            OutlinedButton(
                                onClick = { showRestoreInput = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("open_restore_input_button")
                            ) {
                                Icon(imageVector = Icons.Default.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("إدخال نص النسخة للاستعادة")
                            }
                        } else {
                            OutlinedTextField(
                                value = restoreJsonText,
                                onValueChange = { restoreJsonText = it },
                                label = { Text("الصق نص النسخة الاحتياطية (JSON)") },
                                placeholder = { Text("{\"appName\": \"Al-Okabi Accounting\", ...}") },
                                maxLines = 4,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedButton(onClick = { showRestoreInput = false }) {
                                    Text("إلغاء")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (restoreJsonText.isNotBlank()) {
                                            onRestoreBackup(restoreJsonText)
                                            showRestoreInput = false
                                        } else {
                                            Toast.makeText(context, "يرجى لصق نص النسخة الاحتياطية أولاً", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                                    modifier = Modifier.testTag("confirm_restore_button")
                                ) {
                                    Text("تنفيذ الاستعادة")
                                }
                            }
                        }
                    }
                }
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
