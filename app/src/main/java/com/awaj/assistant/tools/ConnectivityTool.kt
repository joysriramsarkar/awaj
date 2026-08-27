package com.awaj.assistant.tools

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.awaj.assistant.nlu.ToolResult

class ConnectivityTool : Tool {
    override val name: String = "control_connectivity"
    override val descriptionBangla: String = "ওয়াইফাই, ব্লুটুথ ও নেটওয়ার্ক সংযোগ নিয়ন্ত্রণ করে"

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        val target = params["target"]?.toString() ?: "wifi"
        val state = params["state"]?.toString() ?: "toggle"

        return try {
            val intent = when (target) {
                "wifi" -> Intent(Settings.ACTION_WIFI_SETTINGS)
                "bluetooth" -> Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                "airplane" -> Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
                else -> Intent(Settings.ACTION_WIRELESS_SETTINGS)
            }.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)

            val banglaTarget = when (target) {
                "wifi" -> "ওয়াইফাই"
                "bluetooth" -> "ব্লুটুথ"
                "airplane" -> "ফ্লাইট মোড"
                else -> "কানেক্টিভিটি"
            }

            ToolResult.Success("$banglaTarget সেটিংস খোলা হয়েছে।")
        } catch (e: Exception) {
            ToolResult.Failed("কানেক্টিভিটি সেটিংস খুলতে ব্যর্থ: ${e.localizedMessage}")
        }
    }
}
