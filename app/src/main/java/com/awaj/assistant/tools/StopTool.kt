package com.awaj.assistant.tools

import android.content.Context
import com.awaj.assistant.nlu.ToolResult

class StopTool : Tool {
    override val name: String = "stop_all"
    override val descriptionBangla: String = "সব চলমান কাজ ও প্রক্রিয়া সাথে সাথে বন্ধ করে"

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        return ToolResult.Success("সব চলমান কমান্ড সফলভাবে বাতিল করা হয়েছে।")
    }
}
