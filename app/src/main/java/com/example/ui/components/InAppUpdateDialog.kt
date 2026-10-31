package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan

enum class UpdateDialogState {
    PROMPT,
    DOWNLOADING,
    READY_TO_INSTALL,
    ERROR
}

@Composable
fun InAppUpdateDialog(
    state: UpdateDialogState,
    currentVersion: String,
    targetVersion: String,
    changelog: String,
    progress: Float,
    downloadedBytes: Long,
    totalBytes: Long,
    errorMessage: String? = null,
    onStartDownload: () -> Unit,
    onRunInBackground: () -> Unit = {},
    onInstall: () -> Unit,
    onOpenBrowser: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = {
            if (state == UpdateDialogState.DOWNLOADING) {
                onRunInBackground()
            } else {
                onDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = state != UpdateDialogState.DOWNLOADING
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("in_app_update_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = NeonAmber.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SystemUpdate,
                                    contentDescription = null,
                                    tint = NeonAmber,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "VİZYON TV Güncellemesi",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (state == UpdateDialogState.DOWNLOADING) "Arka Planda İndirme Destekli" else "Uygulama İçi Güncelleme Sistemi",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            if (state == UpdateDialogState.DOWNLOADING) {
                                onRunInBackground()
                            } else {
                                onDismiss()
                            }
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = if (state == UpdateDialogState.DOWNLOADING) "Arka Plana Al" else "Kapat",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(14.dp))

                // Content by State
                when (state) {
                    UpdateDialogState.PROMPT -> {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = NeonCyan.copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Mevcut Sürüm: $currentVersion",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Hedef: $targetVersion",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan
                                    )
                                }
                                if (changelog.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = changelog,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Yeni sürümü tarayıcıya yönlendirilmeden, doğrudan uygulama içerisinden indirip kurabilirsiniz.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(18.dp))

                        // Primary Action: Start In-App Download
                        Button(
                            onClick = onStartDownload,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("btn_start_inapp_download")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Uygulama İçinden İndir & Kur",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Secondary Action: Open in Browser
                        Button(
                            onClick = onOpenBrowser,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInBrowser,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Tarayıcıda / GitHub'da Aç",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }

                    UpdateDialogState.DOWNLOADING -> {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Güncelleme Paketi İndiriliyor...",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = NeonAmber,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val percentage = (progress * 100).toInt()
                                Text(
                                    text = "$percentage%",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = NeonAmber
                                )

                                val downloadedMb = "%.1f".format(downloadedBytes / (1024f * 1024f))
                                val totalMb = if (totalBytes > 0) {
                                    "%.1f MB".format(totalBytes / (1024f * 1024f))
                                } else {
                                    "Bilinmiyor"
                                }
                                Text(
                                    text = "$downloadedMb MB / $totalMb",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "İndirme tamamlandığında Android yükleyici otomatik olarak açılacaktır. İsterseniz indirmeyi arka planda devam ettirebilirsiniz.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action: Arka Planda Devam Et
                            Button(
                                onClick = onRunInBackground,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("btn_run_download_in_background")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDownload,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Arka Planda Devam Et",
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    UpdateDialogState.READY_TO_INSTALL -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "İndirme Başarıyla Tamamlandı!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Güncellemeyi tamamlamak için aşağıdaki butona basın.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = {
                                    onInstall()
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("btn_install_downloaded_apk")
                            ) {
                                Text(
                                    text = "Paketi Yükle ve Başlat",
                                    color = Color.Black,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = onDismiss,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(40.dp)
                            ) {
                                Text(
                                    text = "Daha Sonra / Kapat",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    UpdateDialogState.ERROR -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "İndirme Sırasında Sorun Oluştu",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.error
                            )
                            if (!errorMessage.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = errorMessage,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = onStartDownload,
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Tekrar Dene", color = Color.Black, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = onOpenBrowser,
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Tarayıcıda İndir", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
