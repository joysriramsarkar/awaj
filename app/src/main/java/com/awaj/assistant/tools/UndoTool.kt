package com.awaj.assistant.tools

import android.content.Context
import com.awaj.assistant.nlu.ToolResult

object UndoRegistry {
    private var lastUndoableAction: (() -> ToolResult)? = null
    private var lastActionDescription: String = ""

    fun recordUndoableAction(description: String, undoBlock: () -> ToolResult) {
        lastActionDescription = description
        lastUndoableAction = undoBlock
    }

    fun executeUndo(): ToolResult {
        val undo = lastUndoableAction
        if (undo == null) {
            return ToolResult.Failed("বাতিল করার মতো কোনো সাম্প্রতিক কাজ পাওয়া যায়নি।")
        }
        val desc = lastActionDescription
        val result = undo.invoke()
        lastUndoableAction = null
        lastActionDescription = ""
        return if (result is ToolResult.Success) {
            ToolResult.Success("পূর্ববর্তী কাজ ($desc) বাতিল করা হয়েছে।")
        } else {
            result
        }
    }
}

class UndoTool : Tool {
    override val name: String = "undo_action"
    override val descriptionBangla: String = "সর্বশেষ সম্পন্ন কাজ বাতিল বা রিভার্ট করে"

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        return UndoRegistry.executeUndo()
    }
}
