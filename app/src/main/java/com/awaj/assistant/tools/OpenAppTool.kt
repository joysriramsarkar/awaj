package com.awaj.assistant.tools

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.awaj.assistant.nlu.ToolResult
import com.awaj.assistant.safety.SensitiveAppBlocker

class OpenAppTool : Tool {
    override val name: String = "open_app"
    override val descriptionBangla: String = "ফোনে ইনস্টল থাকা যেকোনো অ্যাপ খোলে"

    private val packageMap = mapOf(
        "whatsapp" to "com.whatsapp",
        "youtube" to "com.google.android.youtube",
        "camera" to "com.google.android.GoogleCamera",
        "facebook" to "com.facebook.katana",
        "messenger" to "com.facebook.orca",
        "chrome" to "com.android.chrome",
        "maps" to "com.google.android.apps.maps",
        "gmail" to "com.google.android.gm",
        "playstore" to "com.android.vending",
        "settings" to "com.android.settings"
    )

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        val appQuery = (params["app_query"] ?: params["app"] ?: "").toString().trim()
        val rawName = (params["raw_name"] ?: appQuery).toString()

        if (appQuery.isBlank()) {
            return ToolResult.Failed("কোন অ্যাপটি খুলব তা বুঝতে পারিনি।")
        }

        // Check if sensitive
        if (SensitiveAppBlocker.isPackageBlocked(appQuery)) {
            return ToolResult.Blocked("নিরাপত্তার স্বার্থে আর্থিক বা পেমেন্ট অ্যাপ স্বয়ংক্রিয়ভাবে খোলার অনুমতি নেই।")
        }

        val targetPackage = packageMap[appQuery.lowercase()] ?: appQuery

        // Special handling for camera
        if (appQuery == "camera") {
            try {
                val intent = Intent("android.media.action.IMAGE_CAPTURE").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                return ToolResult.Success("ক্যামেরা খোলা হয়েছে।")
            } catch (e: Exception) {
                // fallback to package launch
            }
        }

        // 1. Try launching by explicit package name
        var launchIntent = context.packageManager.getLaunchIntentForPackage(targetPackage)

        // 2. If not found, search installed applications by label
        if (launchIntent == null) {
            val installedApps = context.packageManager.getInstalledApplications(0)
            for (app in installedApps) {
                val label = context.packageManager.getApplicationLabel(app).toString()
                if (label.contains(rawName, ignoreCase = true) || label.contains(appQuery, ignoreCase = true)) {
                    launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                    break
                }
            }
        }

        // 3. Fallback: search Play Store or browser if not installed
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
            return ToolResult.Success("$rawName অ্যাপ খোলা হয়েছে।")
        } else {
            return ToolResult.Failed("\"$rawName\" অ্যাপটি ফোনে পাওয়া যায়নি।")
        }
    }
}
