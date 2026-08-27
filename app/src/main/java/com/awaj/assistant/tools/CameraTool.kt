package com.awaj.assistant.tools

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import com.awaj.assistant.nlu.ToolResult

class CameraTool : Tool {
    override val name: String = "open_camera_mode"
    override val descriptionBangla: String = "ছবি তোলা, সেলফি বা ভিডিও রেকর্ডিং মোড খোলে"

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        val mode = params["mode"]?.toString() ?: "photo"

        return try {
            val intent = when (mode) {
                "video" -> Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                "selfie" -> Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                    putExtra("android.intent.extras.CAMERA_FACING", 1)
                    putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
                    putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
                }
                else -> Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            }.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)

            val summary = when (mode) {
                "video" -> "ভিডিও রেকর্ডিং ক্যামেরা খোলা হয়েছে।"
                "selfie" -> "সেলফি ক্যামেরা খোলা হয়েছে।"
                else -> "ক্যামেরা খোলা হয়েছে।"
            }
            ToolResult.Success(summary)
        } catch (e: Exception) {
            ToolResult.Failed("ক্যামেরা খুলতে সমস্যা হয়েছে: ${e.localizedMessage}")
        }
    }
}
