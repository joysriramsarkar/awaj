package com.awaj.assistant.nlu

enum class RiskLevel {
    LOW,       // Safe actions (open app, torch, alarm, search, volume)
    MEDIUM,    // Calendar, read notification, route navigation
    HIGH,      // Call, SMS, WhatsApp message, delete
    BLOCKED    // Payment, bank, OTP, password, sensitive bypass
}

enum class ActionSource {
    VOICE, ROUTINE, AGENT, MANUAL
}

enum class AssistantMode {
    SAFE_MODE,           // Play Store Safe: Intent & API based tools only
    ACCESSIBILITY_MODE,  // Assisted screen reading & deterministic navigation
    LAB_MODE             // Autonomous GUI agent with VLM/LLM loop (Power users)
}

data class ActionRequest(
    val action: String,
    val params: Map<String, Any> = emptyMap(),
    val risk: RiskLevel = RiskLevel.LOW,
    val confirmationRequired: Boolean = false,
    val source: ActionSource = ActionSource.VOICE,
    val rawQuery: String = "",
    val summaryBangla: String = ""
)

sealed class ToolResult {
    data class Success(val messageBangla: String, val extraData: Map<String, Any> = emptyMap()) : ToolResult()
    data class Failed(val reasonBangla: String) : ToolResult()
    data class NeedsConfirmation(val summaryBangla: String, val pendingRequest: ActionRequest) : ToolResult()
    data class Blocked(val reasonBangla: String) : ToolResult()
    data class ClarificationNeeded(val questionBangla: String) : ToolResult()
}
