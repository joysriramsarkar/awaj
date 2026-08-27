package com.awaj.assistant.routines

import android.content.Context
import com.awaj.assistant.nlu.ActionRequest
import com.awaj.assistant.nlu.RiskLevel
import com.awaj.assistant.nlu.ToolResult
import com.awaj.assistant.tools.ToolRegistry

data class Routine(
    val id: String,
    val nameBangla: String,
    val triggerPhraseBangla: String,
    val description: String,
    val actions: List<ActionRequest>
)

class RoutineManager(
    private val toolRegistry: ToolRegistry
) {
    private val defaultRoutines = listOf(
        Routine(
            id = "morning_routine",
            nameBangla = "সুপ্রভাত রুটিন",
            triggerPhraseBangla = "সুপ্রভাত",
            description = "আবহাওয়া এবং ব্যাটারি স্ট্যাটাস জানাবে",
            actions = listOf(
                ActionRequest("get_weather", mapOf("query" to "ঢাকা আবহাওয়া"), RiskLevel.LOW),
                ActionRequest("get_device_info", emptyMap(), RiskLevel.LOW),
                ActionRequest("set_volume", mapOf("direction" to "set", "level" to 60), RiskLevel.LOW)
            )
        ),
        Routine(
            id = "night_routine",
            nameBangla = "শুভ রাত্রি রুটিন",
            triggerPhraseBangla = "শুভ রাত্রি",
            description = "মিডিয়া পজ করবে, ব্রাইটনেস কমাবে এবং অ্যালার্ম সেট করবে",
            actions = listOf(
                ActionRequest("media_control", mapOf("command" to "pause"), RiskLevel.LOW),
                ActionRequest("set_brightness", mapOf("direction" to "set", "level" to 15), RiskLevel.LOW),
                ActionRequest("set_alarm", mapOf("hour" to 7, "minute" to 0, "is_tomorrow" to true), RiskLevel.LOW)
            )
        ),
        Routine(
            id = "focus_routine",
            nameBangla = "মনোযোগ মোড",
            triggerPhraseBangla = "পড়ার মোড",
            description = "ভলিউম মিউট করবে এবং গান বন্ধ করবে",
            actions = listOf(
                ActionRequest("media_control", mapOf("command" to "pause"), RiskLevel.LOW),
                ActionRequest("set_volume", mapOf("direction" to "mute"), RiskLevel.LOW)
            )
        )
    )

    fun getAvailableRoutines(): List<Routine> = defaultRoutines

    suspend fun runRoutine(context: Context, routineId: String): ToolResult {
        val routine = defaultRoutines.firstOrNull { it.id == routineId }
            ?: return ToolResult.Failed("রুটিনটি পাওয়া যায়নি।")

        val results = mutableListOf<String>()
        for (action in routine.actions) {
            val result = toolRegistry.execute(context, action, bypassConfirmation = true)
            if (result is ToolResult.Success) {
                results.add(result.messageBangla)
            }
        }

        return ToolResult.Success(
            messageBangla = "${routine.nameBangla} সম্পন্ন হয়েছে। " + results.take(2).joinToString(" ")
        )
    }
}
