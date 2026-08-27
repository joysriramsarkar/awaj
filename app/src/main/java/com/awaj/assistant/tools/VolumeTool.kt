package com.awaj.assistant.tools

import android.content.Context
import android.media.AudioManager
import com.awaj.assistant.nlu.IntentNormalizer
import com.awaj.assistant.nlu.ToolResult

class VolumeTool : Tool {
    override val name: String = "set_volume"
    override val descriptionBangla: String = "মিডিয়া বা রিং ভলিউম বৃদ্ধি, হ্রাস বা সমন্বয় করে"

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        val direction = params["direction"]?.toString() ?: "up"
        val level = (params["level"] as? Number)?.toInt() ?: -1

        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            ?: return ToolResult.Failed("অডিও ম্যানেজার পাওয়া যায়নি।")

        val streamType = AudioManager.STREAM_MUSIC
        val maxVolume = audioManager.getStreamMaxVolume(streamType)
        val prevVolume = audioManager.getStreamVolume(streamType)

        return try {
            val result = when (direction) {
                "mute" -> {
                    audioManager.setStreamVolume(streamType, 0, AudioManager.FLAG_SHOW_UI)
                    ToolResult.Success("ভলিউম নিঃশব্দ (Mute) করা হয়েছে।")
                }
                "up" -> {
                    audioManager.adjustStreamVolume(streamType, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
                    val current = audioManager.getStreamVolume(streamType)
                    val pct = (current * 100) / maxVolume
                    val banglaPct = IntentNormalizer.convertEnglishDigitsToBangla(pct.toString())
                    ToolResult.Success("ভলিউম বাড়িয়ে $banglaPct% করা হয়েছে।")
                }
                "down" -> {
                    audioManager.adjustStreamVolume(streamType, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
                    val current = audioManager.getStreamVolume(streamType)
                    val pct = (current * 100) / maxVolume
                    val banglaPct = IntentNormalizer.convertEnglishDigitsToBangla(pct.toString())
                    ToolResult.Success("ভলিউম কমিয়ে $banglaPct% করা হয়েছে।")
                }
                "set" -> {
                    if (level in 0..100) {
                        val targetVolume = (level * maxVolume) / 100
                        audioManager.setStreamVolume(streamType, targetVolume, AudioManager.FLAG_SHOW_UI)
                        val banglaLevel = IntentNormalizer.convertEnglishDigitsToBangla(level.toString())
                        ToolResult.Success("ভলিউম $banglaLevel% এ নির্ধারণ করা হয়েছে।")
                    } else {
                        ToolResult.Failed("সঠিক ভলিউম মাত্রা উল্লেখ করুন (০ থেকে ১০০ এর মধ্যে)।")
                    }
                }
                else -> ToolResult.Success("ভলিউম সমন্বয় করা হয়েছে।")
            }

            if (result is ToolResult.Success) {
                UndoRegistry.recordUndoableAction("ভলিউম পরিবর্তন") {
                    try {
                        audioManager.setStreamVolume(streamType, prevVolume, AudioManager.FLAG_SHOW_UI)
                        val prevPct = (prevVolume * 100) / maxVolume
                        val banglaPrev = IntentNormalizer.convertEnglishDigitsToBangla(prevPrev(prevPct).toString())
                        ToolResult.Success("ভলিউম পূর্বাবস্থায় ($banglaPrev%) ফিরিয়ে নেওয়া হয়েছে।")
                    } catch (e: Exception) {
                        ToolResult.Failed("ভলিউম রিভার্ট করা সম্ভব হয়নি।")
                    }
                }
            }

            result
        } catch (e: Exception) {
            ToolResult.Failed("ভলিউম পরিবর্তন করতে সমস্যা হয়েছে: ${e.localizedMessage}")
        }
    }

    private fun prevPrev(pct: Int): Int = pct.coerceIn(0, 100)
}
