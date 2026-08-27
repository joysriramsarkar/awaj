package com.awaj.assistant.notification

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class NotificationItem(
    val packageName: String,
    val title: String,
    val text: String,
    val postTime: Long
)

class AssistantNotificationListener : NotificationListenerService() {

    companion object {
        private val _latestNotifications = MutableStateFlow<List<NotificationItem>>(emptyList())
        val latestNotifications: StateFlow<List<NotificationItem>> = _latestNotifications.asStateFlow()

        fun getLatestSummary(): String {
            val list = _latestNotifications.value
            if (list.isEmpty()) {
                return "বর্তমানে কোনো নতুন নোটিফিকেশন নেই।"
            }
            val recent = list.take(3)
            val sb = StringBuilder("সর্বশেষ নোটিফিকেশন:\n")
            for ((idx, item) in recent.withIndex()) {
                sb.append("${idx + 1}. ${item.title}: ${item.text}\n")
            }
            return sb.toString().trim()
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        val extras = sbn.notification.extras ?: return
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""

        if (title.isNotBlank() || text.isNotBlank()) {
            val item = NotificationItem(
                packageName = sbn.packageName,
                title = title,
                text = text,
                postTime = sbn.postTime
            )
            val updated = listOf(item) + _latestNotifications.value.take(19)
            _latestNotifications.value = updated
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        if (sbn == null) return
        _latestNotifications.value = _latestNotifications.value.filter { it.packageName != sbn.packageName }
    }
}
