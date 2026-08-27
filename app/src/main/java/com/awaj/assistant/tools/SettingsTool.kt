package com.awaj.assistant.tools

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.awaj.assistant.nlu.ToolResult

class SettingsTool : Tool {
    override val name: String = "open_settings"
    override val descriptionBangla: String = "ওয়াইফাই, ব্লুটুথ ও সিস্টেম সেটিংস খোলে"

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        val setting = params["setting"]?.toString() ?: "general"

        val action = when (setting) {
            "wifi" -> Settings.ACTION_WIFI_SETTINGS
            "bluetooth" -> Settings.ACTION_BLUETOOTH_SETTINGS
            "display" -> Settings.ACTION_DISPLAY_SETTINGS
            "sound" -> Settings.ACTION_SOUND_SETTINGS
            "accessibility" -> Settings.ACTION_ACCESSIBILITY_SETTINGS
            "notification" -> Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
            else -> Settings.ACTION_SETTINGS
        }

        return try {
            val intent = Intent(action).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            val nameBangla = when (setting) {
                "wifi" -> "ওয়াইফাই"
                "bluetooth" -> "ব্লুটুথ"
                "display" -> "ডিসপ্লে"
                "sound" -> "সাউন্ড"
                "accessibility" -> "অ্যাক্সেসিবিলিটি"
                "notification" -> "নোটিফিকেশন"
                else -> "সিস্টেম"
            }
            ToolResult.Success("$nameBangla সেটিংস খোলা হয়েছে।")
        } catch (e: Exception) {
            ToolResult.Failed("সেটিংস খুলতে সমস্যা হয়েছে: ${e.localizedMessage}")
        }
    }
}
