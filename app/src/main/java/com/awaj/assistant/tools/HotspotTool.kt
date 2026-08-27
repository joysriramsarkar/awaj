package com.awaj.assistant.tools

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.awaj.assistant.nlu.ToolResult

class HotspotTool : Tool {
    override val name: String = "toggle_hotspot"
    override val descriptionBangla: String = "মোবাইল হটস্পট ও টেথারিং সেটিংস চালু বা নিয়ন্ত্রণ করে"

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        val state = params["state"]?.toString() ?: "open"

        return try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                component = ComponentName(
                    "com.android.settings",
                    "com.android.settings.TetherSettings"
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                // Fallback to wireless settings
                val fallbackIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
            }

            val actionSummary = when (state) {
                "on" -> "হটস্পট চালু করার জন্য টেথারিং সেটিংস খোলা হয়েছে।"
                "off" -> "হটস্পট বন্ধ করার জন্য টেথারিং সেটিংস খোলা হয়েছে।"
                else -> "হটস্পট সেটিংস খোলা হয়েছে।"
            }
            ToolResult.Success(actionSummary)
        } catch (e: Exception) {
            ToolResult.Failed("হটস্পট খুলতে সমস্যা হয়েছে: ${e.localizedMessage}")
        }
    }
}
