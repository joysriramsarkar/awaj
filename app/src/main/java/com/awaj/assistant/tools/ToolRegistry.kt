package com.awaj.assistant.tools

import android.content.Context
import com.awaj.assistant.nlu.ActionRequest
import com.awaj.assistant.nlu.RiskLevel
import com.awaj.assistant.nlu.ToolResult
import com.awaj.assistant.safety.PermissionGate
import com.awaj.assistant.safety.RiskClassifier
import com.awaj.assistant.safety.SensitiveAppBlocker

class ToolRegistry(
    customTools: List<Tool> = emptyList()
) {
    private val toolMap = mutableMapOf<String, Tool>()

    init {
        // Core System & Automation Tools
        register(OpenAppTool())
        register(CallTool())
        register(SmsTool())
        register(WhatsAppTool())
        register(AlarmTool())
        register(TimerTool())
        register(TorchTool())
        register(VolumeTool())
        register(BrightnessTool())
        register(CalendarTool())
        register(MediaControlTool())
        register(SettingsTool())
        register(WebSearchTool())
        register(WeatherTool())
        register(DeviceInfoTool())
        register(ReadNotificationsTool())
        register(StopTool())

        // Extended Smart Controls
        register(HotspotTool())
        register(ConnectivityTool())
        register(MusicPlayerTool())
        register(CalculatorTool())
        register(CameraTool())

        for (t in customTools) {
            register(t)
        }
    }

    fun register(tool: Tool) {
        toolMap[tool.name] = tool
    }

    fun getTool(name: String): Tool? = toolMap[name]

    suspend fun execute(context: Context, request: ActionRequest, bypassConfirmation: Boolean = false): ToolResult {
        // 1. Safety Gate Check for money transfer / credentials
        val risk = RiskClassifier.evaluateRisk(request)
        if (risk == RiskLevel.BLOCKED) {
            return ToolResult.Blocked("নিরাপত্তা নীতিমালার কারণে আর্থিক লেনদেন, পাসওয়ার্ড বা সংবেদনশীল কাজ স্বয়ংক্রিয়ভাবে করা সম্ভব নয়।")
        }

        // 2. Sensitive automated transfer check
        if (SensitiveAppBlocker.containsFinancialTransferIntent(request.rawQuery)) {
            return ToolResult.Blocked("আর্থিক লেনদেন বা ইউপিআই পিন সংক্রান্ত স্বয়ংক্রিয় কমান্ড বন্ধ রাখা হয়েছে।")
        }

        // 3. Confirmation requirement check
        if (!bypassConfirmation && RiskClassifier.isConfirmationRequired(request)) {
            val summary = request.summaryBangla.ifBlank {
                when (request.action) {
                    "make_call" -> "${request.params["contact"]}-কে কল করতে চান?"
                    "send_sms" -> "${request.params["contact"]}-কে SMS পাঠাতে চান?"
                    "send_whatsapp" -> "${request.params["contact"]}-কে হোয়াটসঅ্যাপ মেসেজ পাঠাতে চান?"
                    else -> "এই কাজটি করতে চান?"
                }
            }
            return ToolResult.NeedsConfirmation(summary, request)
        }

        // 4. Permission Gate Check
        val missingPermission = PermissionGate.getMissingPermissionForAction(context, request.action)
        if (missingPermission != null && request.action == "make_call" && !PermissionGate.hasCallPermission(context)) {
            // CallTool handles dialer fallback gracefully
        }

        // 5. Tool Lookup and Execution
        val tool = toolMap[request.action]
            ?: return ToolResult.Failed("এই কমান্ডটি পরিচালনা করার জন্য উপযুক্ত টুল পাওয়া যায়নি।")

        return try {
            tool.execute(context, request.params)
        } catch (e: Exception) {
            ToolResult.Failed("কমান্ড সম্পাদনে সমস্যা হয়েছে: ${e.localizedMessage}")
        }
    }
}
