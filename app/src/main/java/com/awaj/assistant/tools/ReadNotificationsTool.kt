package com.awaj.assistant.tools

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.awaj.assistant.nlu.ToolResult
import com.awaj.assistant.notification.AssistantNotificationListener
import com.awaj.assistant.safety.PermissionGate

class ReadNotificationsTool : Tool {
    override val name: String = "read_notifications"
    override val descriptionBangla: String = "সাম্প্রতিক নোটিফিকেশন পড়ে শোনানোর টুল (পৃথক পারমিশন ও ডিসক্লোজার আবশ্যক)"

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        // Enforce Notification Listener permission check
        if (!PermissionGate.isNotificationListenerGranted(context)) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                // fallback
            }
            return ToolResult.NeedsConfirmation(
                summaryBangla = "নোটিফিকেশন পড়তে সিস্টেমের Notification Access পারমিশন ও সম্মতি প্রয়োজন। সেটিংস ওপেন করা হয়েছে।",
                pendingRequest = com.awaj.assistant.nlu.ActionRequest(
                    action = name,
                    params = params,
                    risk = com.awaj.assistant.nlu.RiskLevel.MEDIUM,
                    confirmationRequired = true,
                    summaryBangla = "নোটিফিকেশন পড়ার সম্মতি"
                )
            )
        }

        val summary = AssistantNotificationListener.getLatestSummary()
        return ToolResult.Success(summary)
    }
}
