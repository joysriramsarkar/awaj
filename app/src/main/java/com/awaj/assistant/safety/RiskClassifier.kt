package com.awaj.assistant.safety

import com.awaj.assistant.nlu.ActionRequest
import com.awaj.assistant.nlu.RiskLevel
import java.util.Locale

object RiskClassifier {

    private val blockedKeywords = listOf(
        "টাকা পাঠাও", "টাকা পাঠাতে", "সেন্ড মানি", "ক্যাশ আউট", "পাসওয়ার্ড", "password",
        "pin", "পিন নম্বর", "পিন কোড", "otp", "ওটিপি", "cvv", "ব্যাংক ট্রান্সফার",
        "delete account", "ফ্যাক্টরি রিসেট", "format phone", "সব ডিলিট",
        "টাকা ট্রান্সফার", "পেমেন্ট করো", "পেমেন্ট কর", "ইউপিআই পিন", "upi pin"
    )

    private val highRiskActions = setOf(
        "make_call",
        "send_sms",
        "send_whatsapp",
        "delete_file",
        "share_location"
    )

    private val mediumRiskActions = setOf(
        "add_calendar_event",
        "read_notifications",
        "gui_action"
    )

    fun evaluateRisk(request: ActionRequest): RiskLevel {
        val query = request.rawQuery.lowercase(Locale.getDefault())

        // 1. Check if financial transfer or credential input intent
        if (SensitiveAppBlocker.containsFinancialTransferIntent(query)) {
            return RiskLevel.BLOCKED
        }

        // 2. Check forbidden destructive system operations
        for (kw in blockedKeywords) {
            if (query.contains(kw)) {
                return RiskLevel.BLOCKED
            }
        }

        if (request.risk == RiskLevel.BLOCKED) {
            return RiskLevel.BLOCKED
        }

        if (highRiskActions.contains(request.action)) {
            return RiskLevel.HIGH
        }

        if (mediumRiskActions.contains(request.action)) {
            return RiskLevel.MEDIUM
        }

        return RiskLevel.LOW
    }

    fun isConfirmationRequired(request: ActionRequest): Boolean {
        val risk = evaluateRisk(request)
        return risk == RiskLevel.HIGH || (risk == RiskLevel.MEDIUM && request.action == "add_calendar_event")
    }
}
