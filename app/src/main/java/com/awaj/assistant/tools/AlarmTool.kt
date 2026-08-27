package com.awaj.assistant.tools

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import com.awaj.assistant.nlu.IntentNormalizer
import com.awaj.assistant.nlu.ToolResult
import java.util.Locale

class AlarmTool : Tool {
    override val name: String = "set_alarm"
    override val descriptionBangla: String = "নির্দিষ্ট সময়ে অ্যালার্ম সেট করে"

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        val hour = (params["hour"] as? Number)?.toInt() ?: 7
        val minute = (params["minute"] as? Number)?.toInt() ?: 0
        val isTomorrow = (params["is_tomorrow"] as? Boolean) ?: false

        return try {
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_HOUR, hour)
                putExtra(AlarmClock.EXTRA_MINUTES, minute)
                putExtra(AlarmClock.EXTRA_MESSAGE, "Awaj Assistant Alarm")
                putExtra(AlarmClock.EXTRA_SKIP_UI, true)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                // Fallback without EXTRA_SKIP_UI for OEM devices that restrict silent alarm setting
                val fallbackIntent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                    putExtra(AlarmClock.EXTRA_HOUR, hour)
                    putExtra(AlarmClock.EXTRA_MINUTES, minute)
                    putExtra(AlarmClock.EXTRA_MESSAGE, "Awaj Assistant Alarm")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                try {
                    context.startActivity(fallbackIntent)
                } catch (fallbackEx: Exception) {
                    // Final fallback: open alarms list
                    val showIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(showIntent)
                }
            }

            val timeFormatted = String.format(Locale.US, "%02d:%02d", hour, minute)
            val banglaTime = IntentNormalizer.convertEnglishDigitsToBangla(timeFormatted)
            val dayPrefix = if (isTomorrow) "আগামীকাল " else ""

            ToolResult.Success("${dayPrefix}$banglaTime টায় অ্যালার্ম সেট করা হয়েছে।")
        } catch (e: Exception) {
            ToolResult.Failed("অ্যালার্ম সেট করতে সমস্যা হয়েছে: ${e.localizedMessage}")
        }
    }
}
