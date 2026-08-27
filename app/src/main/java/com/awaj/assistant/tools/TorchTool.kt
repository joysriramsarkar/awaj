package com.awaj.assistant.tools

import android.content.Context
import android.hardware.camera2.CameraManager
import com.awaj.assistant.nlu.ToolResult

class TorchTool : Tool {
    override val name: String = "toggle_torch"
    override val descriptionBangla: String = "ফোনের ফ্ল্যাশলাইট/টর্চ অন বা অফ করে"

    companion object {
        private var isTorchOn = false
    }

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        val targetState = params["state"]?.toString() ?: "toggle"
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager

        if (cameraManager == null) {
            return ToolResult.Failed("ক্যামেরা ম্যানেজার পাওয়া যায়নি।")
        }

        return try {
            val cameraId = cameraManager.cameraIdList.firstOrNull()
                ?: return ToolResult.Failed("ফোনে কোনো ফ্ল্যাশলাইট ক্যামেরা পাওয়া যায়নি।")

            val newState = when (targetState) {
                "on" -> true
                "off" -> false
                else -> !isTorchOn
            }

            cameraManager.setTorchMode(cameraId, newState)
            isTorchOn = newState

            // Record undo action
            UndoRegistry.recordUndoableAction(if (newState) "টর্চ বন্ধ করা" else "টর্চ জ্বালানো") {
                try {
                    cameraManager.setTorchMode(cameraId, !newState)
                    isTorchOn = !newState
                    ToolResult.Success(if (!newState) "টর্চ জ্বালানো হয়েছে।" else "টর্চ বন্ধ করা হয়েছে।")
                } catch (e: Exception) {
                    ToolResult.Failed("টর্চ রিভার্ট করা সম্ভব হয়নি।")
                }
            }

            if (newState) {
                ToolResult.Success("টর্চ জ্বালানো হয়েছে।")
            } else {
                ToolResult.Success("টর্চ বন্ধ করা হয়েছে।")
            }
        } catch (e: Exception) {
            ToolResult.Failed("টর্চ নিয়ন্ত্রণ করতে সমস্যা হয়েছে: ${e.localizedMessage}")
        }
    }
}
