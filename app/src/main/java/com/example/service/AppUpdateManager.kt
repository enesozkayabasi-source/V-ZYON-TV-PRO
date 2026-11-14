package com.example.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val isUpdateAvailable: Boolean,
    val latestVersion: String,
    val currentVersion: String,
    val apkDownloadUrl: String,
    val changelog: String,
    val assetSize: Long
)

class AppUpdateManager(private val context: Context) {

    companion object {
        private const val GITHUB_API_LATEST_RELEASE =
            "https://api.github.com/repos/enesozkayabasi-source/V-ZYON-TV-PRO/releases/latest"
        private const val WEB_PORTAL_VERSION_JSON =
            "https://www.vizyontv4201.com.tr/version.json"
        private const val BACKUP_WEB_PORTAL_URL =
            "https://enesozkayabasi-source.github.io/V-ZYON-TV-PRO/"
        private const val DEFAULT_FALLBACK_APK_URL =
            "https://www.vizyontv4201.com.tr/releases/VIZYON.TV.v3.5.apk"
        private const val BACKUP_GITHUB_APK_URL =
            "https://github.com/enesozkayabasi-source/V-ZYON-TV-PRO/releases/download/v3.5/VIZYON.TV.v3.5.apk"
        private const val DEFAULT_V35_CHANGELOG =
            "VİZYON TV v3.5: Tüm TV kanalları ve TRT radyo/TV yayınları yenilendi, 40+ canlı radyo doğrulandı, akıllı canlı yayın çözümleyici güçlendirildi ve www.vizyontv4201.com.tr resmi web yayın portalı + QR indirme sistemi eklendi."
    }

    /**
     * Öncelikle www.vizyontv4201.com.tr (veya kayıtlı portal version.json) üzerinden,
     * erişilemezse yedek yayın sunucularından güncellemeleri kontrol eder.
     */
    suspend fun checkForUpdatesOnline(
        currentVersion: String,
        forceCheckUrl: String? = null,
        webPortalUrl: String = UserPreferencesManager.DEFAULT_WEB_PORTAL_URL
    ): UpdateInfo = withContext(Dispatchers.IO) {
        val primaryWebInfo = if (forceCheckUrl == null) fetchFromWebPortalManifest(currentVersion, webPortalUrl) else null
        val backupWebInfo = if (primaryWebInfo == null && forceCheckUrl == null && !webPortalUrl.contains("enesozkayabasi-source.github.io")) {
            fetchFromWebPortalManifest(currentVersion, BACKUP_WEB_PORTAL_URL)
        } else null
        val webInfo = primaryWebInfo ?: backupWebInfo
        val githubInfo = fetchFromGitHubRelease(currentVersion, forceCheckUrl)

        when {
            webInfo != null && githubInfo != null -> {
                if (isNewerVersion(githubInfo.latestVersion, webInfo.latestVersion)) githubInfo else webInfo
            }
            webInfo != null -> webInfo
            githubInfo != null -> githubInfo
            else -> UpdateInfo(
                isUpdateAvailable = isNewerVersion("v3.5", currentVersion),
                latestVersion = "v3.5",
                currentVersion = currentVersion,
                apkDownloadUrl = DEFAULT_FALLBACK_APK_URL,
                changelog = DEFAULT_V35_CHANGELOG,
                assetSize = 0L
            )
        }
    }

    private fun fetchFromGitHubRelease(currentVersion: String, forceCheckUrl: String?): UpdateInfo? {
        return try {
            val url = URL(forceCheckUrl ?: GITHUB_API_LATEST_RELEASE)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 4000
                readTimeout = 4000
                requestMethod = "GET"
                setRequestProperty("User-Agent", "Mozilla/5.0 (Android; Mobile; VIZYON-TV-PRO)")
                setRequestProperty("Accept", "application/vnd.github.v3+json")
            }

            if (conn.responseCode in 200..299) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(responseText)
                val tagName = json.optString("tag_name", "v3.5")
                val body = json.optString("body", DEFAULT_V35_CHANGELOG)

                var apkUrl = DEFAULT_FALLBACK_APK_URL
                var apkSize = 0L

                val assets = json.optJSONArray("assets")
                if (assets != null) {
                    for (i in 0 until assets.length()) {
                        val asset = assets.getJSONObject(i)
                        val name = asset.optString("name", "")
                        if (name.endsWith(".apk", ignoreCase = true)) {
                            apkUrl = asset.optString("browser_download_url", DEFAULT_FALLBACK_APK_URL)
                            apkSize = asset.optLong("size", 0L)
                            break
                        }
                    }
                }

                UpdateInfo(
                    isUpdateAvailable = isNewerVersion(tagName, currentVersion),
                    latestVersion = tagName,
                    currentVersion = currentVersion,
                    apkDownloadUrl = apkUrl,
                    changelog = body,
                    assetSize = apkSize
                )
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun fetchFromWebPortalManifest(currentVersion: String, webPortalUrl: String): UpdateInfo? {
        return try {
            val cleanBase = webPortalUrl.trim().let {
                when {
                    it.endsWith("version.json", ignoreCase = true) -> it
                    it.endsWith(".html", ignoreCase = true) -> it.substringBeforeLast("/") + "/version.json"
                    else -> it.trimEnd('/') + "/version.json"
                }
            }
            val manifestUrl = URL(cleanBase)
            val conn = (manifestUrl.openConnection() as HttpURLConnection).apply {
                connectTimeout = 4000
                readTimeout = 4000
                requestMethod = "GET"
                setRequestProperty("User-Agent", "Mozilla/5.0 (Android; Mobile; VIZYON-TV-PRO)")
                setRequestProperty("Accept", "application/json")
            }

            if (conn.responseCode in 200..299) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(responseText)
                val version = json.optString("version", "v3.5")
                val rawApkUrl = json.optString("apkUrl", DEFAULT_FALLBACK_APK_URL)
                val resolvedApkUrl = if (rawApkUrl.startsWith("http://") || rawApkUrl.startsWith("https://")) {
                    rawApkUrl
                } else {
                    URL(manifestUrl, rawApkUrl).toString()
                }
                val changelog = json.optString("changelog", DEFAULT_V35_CHANGELOG)
                val size = json.optLong("apkSizeBytes", 0L)
                UpdateInfo(
                    isUpdateAvailable = isNewerVersion(version, currentVersion),
                    latestVersion = version,
                    currentVersion = currentVersion,
                    apkDownloadUrl = resolvedApkUrl,
                    changelog = changelog,
                    assetSize = size
                )
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * APK dosyasını uygulama içinden doğrudan indirir ve ilerleme durumunu bildirir
     */
    suspend fun downloadApk(
        downloadUrl: String,
        onProgress: (bytesDownloaded: Long, totalBytes: Long, progressFraction: Float) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        val candidates = if (downloadUrl.contains("vizyontv4201.com.tr")) {
            listOf(downloadUrl, BACKUP_GITHUB_APK_URL)
        } else {
            listOf(downloadUrl)
        }

        var lastError: Exception? = null
        for (candidateUrl in candidates) {
            try {
                val updateDir = File(context.cacheDir, "updates")
                if (!updateDir.exists()) {
                    updateDir.mkdirs()
                }
                val destinationFile = File(updateDir, "vizyontv_update.apk")
                if (destinationFile.exists()) {
                    destinationFile.delete()
                }

                var currentUrl = candidateUrl
                var connection: HttpURLConnection
                var redirects = 0
                while (true) {
                    val urlObj = URL(currentUrl)
                    connection = (urlObj.openConnection() as HttpURLConnection).apply {
                        connectTimeout = 8000
                        readTimeout = 15000
                        instanceFollowRedirects = false
                        setRequestProperty("User-Agent", "Mozilla/5.0 (Android; Mobile; VIZYON-TV-PRO)")
                    }
                    val code = connection.responseCode
                    if (code in 300..399 && redirects < 5) {
                        val location = connection.getHeaderField("Location")
                        if (!location.isNullOrBlank()) {
                            currentUrl = location
                            redirects++
                            continue
                        }
                    }
                    break
                }

                if (connection.responseCode !in 200..299) {
                    throw IllegalStateException("Sunucu yanıtı: HTTP ${connection.responseCode}")
                }

                val totalBytes = connection.contentLength.toLong()
                var bytesDownloaded = 0L

                connection.inputStream.use { input ->
                    FileOutputStream(destinationFile).use { output ->
                        val buffer = ByteArray(32 * 1024)
                        var bytesRead: Int
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            bytesDownloaded += bytesRead

                            val fraction = if (totalBytes > 0) {
                                (bytesDownloaded.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
                            } else {
                                0.5f
                            }
                            onProgress(bytesDownloaded, totalBytes, fraction)
                        }
                        output.flush()
                    }
                }

                onProgress(bytesDownloaded, totalBytes, 1.0f)
                return@withContext Result.success(destinationFile)
            } catch (e: Exception) {
                lastError = e
            }
        }
        Result.failure(lastError ?: IllegalStateException("APK indirilemedi."))
    }

    /**
     * İndirilen APK'yı Android Paket Yükleyicisi ile doğrudan başlatır (Tarayıcı açmadan)
     */
    fun installApk(apkFile: File): Result<Unit> {
        return try {
            if (!apkFile.exists()) {
                return Result.failure(IllegalStateException("İndirilen APK dosyası bulunamadı."))
            }

            // Android 8.0+ Bilinmeyen kaynak izin kontrolü
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    val permissionIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = Uri.parse("package:${context.packageName}")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(permissionIntent)
                }
            }

            val contentUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(contentUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(installIntent)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun isNewerVersion(remoteTag: String, localTag: String): Boolean {
        val cleanRemote = remoteTag.removePrefix("v").trim()
        val cleanLocal = localTag.removePrefix("v").trim()
        if (cleanRemote == cleanLocal) return false

        val remoteParts = cleanRemote.split(".").mapNotNull { it.toIntOrNull() }
        val localParts = cleanLocal.split(".").mapNotNull { it.toIntOrNull() }

        for (i in 0 until maxOf(remoteParts.size, localParts.size)) {
            val r = remoteParts.getOrElse(i) { 0 }
            val l = localParts.getOrElse(i) { 0 }
            if (r > l) return true
            if (r < l) return false
        }
        return false
    }
}
