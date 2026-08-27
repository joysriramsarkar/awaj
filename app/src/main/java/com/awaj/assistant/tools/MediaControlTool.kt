package com.awaj.assistant.tools

import android.content.Context
import android.media.AudioManager
import android.view.KeyEvent
import com.awaj.assistant.nlu.ToolResult

class MediaControlTool : Tool {
    override val name: String = "media_control"
    override val descriptionBangla: String = "মিডিয়া প্লে, পজ এবং গান পরিবর্তন নিয়ন্ত্রণ করে"

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        val command = params["command"]?.toString() ?: "play"
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            ?: return ToolResult.Failed("অডিও ম্যানেজার পাওয়া যায়নি।")

        val keyCode = when (command) {
            "pause" -> KeyEvent.KEYCODE_MEDIA_PAUSE
            "play" -> KeyEvent.KEYCODE_MEDIA_PLAY
            "next" -> KeyEvent.KEYCODE_MEDIA_NEXT
            "previous" -> KeyEvent.KEYCODE_MEDIA_PREVIOUS
            else -> KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
        }

        return try {
            audioManager.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
            audioManager.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))

            val feedback = when (command) {
                "pause" -> "মিডিয়া বন্ধ (Pause) করা হয়েছে।"
                "next" -> "পরবর্তী গানে যাওয়া হয়েছে।"
                "previous" -> "পূর্ববর্তী গানে যাওয়া হয়েছে।"
                else -> "মিডিয়া চালু (Play) করা হয়েছে।"
            }
            ToolResult.Success(feedback)
        } catch (e: Exception) {
            ToolResult.Failed("মিডিয়া নিয়ন্ত্রণ করা সম্ভব হয়নি: ${e.localizedMessage}")
        }
    }
}
