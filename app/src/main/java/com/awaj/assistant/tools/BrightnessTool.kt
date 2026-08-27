package com.awaj.assistant.tools

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.awaj.assistant.nlu.IntentNormalizer
import com.awaj.assistant.nlu.ToolResult

class BrightnessTool : Tool {
    override val name: String = "set_brightness"
    override val descriptionBangla: String = "স্ক্রিন ব্রাইটনেস বাড়ায় বা কমায়"

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        val direction = params["direction"]?.toString() ?: "down"
        val level = (params["level"] as? Number)?.toInt() ?: -1

        return try {
            if (Settings.System.canWrite(context)) {
                val currentBrightness = Settings.System.getInt(
                    context.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS,
                    128
                )

                val newBrightness = when (direction) {
                    "up" -> (currentBrightness + 50).coerceAtMost(255)
                    "down" -> (currentBrightness - 50).coerceAtLeast(10)
                    "set" -> if (level in 0..100) ((level * 255) / 100).coerceIn(10, 255) else currentBrightness
                    else -> currentBrightness
                }

                Settings.System.putInt(
                    context.contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS,
                    newBrightness
                )

                val pct = (newBrightness * 100) / 255
                val banglaPct = IntentNormalizer.convertEnglishDigitsToBangla(pct.toString())
                ToolResult.Success("ব্রাইটনেস $banglaPct% নির্ধারণ করা হয়েছে।")
            } else {
                // If WRITE_SETTINGS is not granted, open display settings
                val intent = Intent(Settings.ACTION_DISPLAY_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                ToolResult.Success("ব্রাইটনেস পরিবর্তনের জন্য ডিসপ্লে সেটিংস খোলা হয়েছে।")
            }
        } catch (e: Exception) {
            ToolResult.Failed("ব্রাইটনেস পরিবর্তন করতে সমস্যা হয়েছে: ${e.localizedMessage}")
        }
    }
}
