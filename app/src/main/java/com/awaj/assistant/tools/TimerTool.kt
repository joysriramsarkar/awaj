package com.awaj.assistant.tools

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import com.awaj.assistant.nlu.IntentNormalizer
import com.awaj.assistant.nlu.ToolResult

class TimerTool : Tool {
    override val name: String = "set_timer"
    override val descriptionBangla: String = "কাউন্টডাউন টাইমার সেট করে"

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        val seconds = (params["seconds"] as? Number)?.toInt() ?: 300
        val label = params["label"]?.toString() ?: "Awaj Timer"

        return try {
            val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
                putExtra(AlarmClock.EXTRA_LENGTH, seconds)
                putExtra(AlarmClock.EXTRA_MESSAGE, label)
                putExtra(AlarmClock.EXTRA_SKIP_UI, true)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)

            val minutes = seconds / 60
            val banglaMin = IntentNormalizer.convertEnglishDigitsToBangla(minutes.toString())
            val banglaSec = IntentNormalizer.convertEnglishDigitsToBangla((seconds % 60).toString())

            val durationText = if (minutes > 0 && seconds % 60 > 0) {
                "$banglaMin মিনিট $banglaSec সেকেন্ডের"
            } else if (minutes > 0) {
                "$banglaMin মিনিটের"
            } else {
                "$banglaSec সেকেন্ডের"
            }

            ToolResult.Success("$durationText টাইমার চালু করা হয়েছে।")
        } catch (e: Exception) {
            ToolResult.Failed("টাইমার সেট করতে সমস্যা হয়েছে: ${e.localizedMessage}")
        }
    }
}
