package com.awaj.assistant.appfunctions

import android.content.Context
import com.awaj.assistant.nlu.ActionRequest
import com.awaj.assistant.nlu.ActionSource
import com.awaj.assistant.nlu.RiskLevel
import com.awaj.assistant.nlu.ToolResult
import com.awaj.assistant.tools.ToolRegistry

/**
 * Android AppFunctions (MCP-Style Tool Exposure Architecture)
 * Introduced in Android I/O 2026 for exposing safe, policy-compliant app tools directly
 * to system AI agents like Google Gemini without Play Store policy risks.
 */
data class AppFunctionDefinition(
    val functionName: String,
    val description: String,
    val parameters: Map<String, String>,
    val isReadOnly: Boolean = false
)

object AwajAppFunctions {

    /**
     * List of tools safely exposed to Android AppFunctions / MCP Ecosystem.
     */
    val exposedFunctions: List<AppFunctionDefinition> = listOf(
        AppFunctionDefinition(
            functionName = "awaj_open_app",
            description = "Launches an installed Android app by Bengali name or package alias.",
            parameters = mapOf("app_query" to "String: Bengali or English name of the target app")
        ),
        AppFunctionDefinition(
            functionName = "awaj_toggle_torch",
            description = "Controls the phone flashlight/torch state.",
            parameters = mapOf("state" to "String: 'on', 'off', or 'toggle'")
        ),
        AppFunctionDefinition(
            functionName = "awaj_set_alarm",
            description = "Sets a clock alarm at specified hour and minute.",
            parameters = mapOf(
                "hour" to "Int: 0 to 23",
                "minute" to "Int: 0 to 59",
                "is_tomorrow" to "Boolean"
            )
        ),
        AppFunctionDefinition(
            functionName = "awaj_set_timer",
            description = "Starts a countdown timer in seconds.",
            parameters = mapOf("seconds" to "Int: duration in seconds")
        ),
        AppFunctionDefinition(
            functionName = "awaj_set_volume",
            description = "Controls media volume level or mute.",
            parameters = mapOf("direction" to "String: 'up', 'down', 'mute', 'set'", "level" to "Int: 0 to 100")
        ),
        AppFunctionDefinition(
            functionName = "awaj_get_device_info",
            description = "Returns current battery percentage and device status.",
            parameters = emptyMap(),
            isReadOnly = true
        )
    )

    /**
     * Executes an AppFunction invoked by system Gemini Agent.
     */
    suspend fun executeAppFunction(
        context: Context,
        toolRegistry: ToolRegistry,
        functionName: String,
        params: Map<String, Any>
    ): ToolResult {
        val actionName = when (functionName) {
            "awaj_open_app" -> "open_app"
            "awaj_toggle_torch" -> "toggle_torch"
            "awaj_set_alarm" -> "set_alarm"
            "awaj_set_timer" -> "set_timer"
            "awaj_set_volume" -> "set_volume"
            "awaj_get_device_info" -> "get_device_info"
            else -> functionName.removePrefix("awaj_")
        }

        val request = ActionRequest(
            action = actionName,
            params = params,
            risk = RiskLevel.LOW,
            confirmationRequired = false,
            source = ActionSource.AGENT,
            rawQuery = "AppFunctions invocation: $functionName",
            summaryBangla = "AppFunctions সিস্টেম এজেন্টের মাধ্যমে $actionName সম্পাদন করা হচ্ছে"
        )

        return toolRegistry.execute(context, request, bypassConfirmation = true)
    }
}
