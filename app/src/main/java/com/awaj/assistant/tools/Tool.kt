package com.awaj.assistant.tools

import android.content.Context
import com.awaj.assistant.nlu.ToolResult

interface Tool {
    val name: String
    val descriptionBangla: String
    suspend fun execute(context: Context, params: Map<String, Any>): ToolResult
}
