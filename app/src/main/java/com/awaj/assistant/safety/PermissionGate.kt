package com.awaj.assistant.safety

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.awaj.assistant.accessibility.AssistAccessibilityService
import com.awaj.assistant.notification.AssistantNotificationListener

object PermissionGate {

    fun hasRecordAudioPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasCallPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasSmsPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasContactsPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Checks if NotificationListenerService has been granted permission in system settings.
     * Uses NotificationManagerCompat with fallback to Settings.Secure.
     */
    fun isNotificationListenerGranted(context: Context): Boolean {
        // 1. Primary AndroidX API check
        try {
            val enabledPackages = NotificationManagerCompat.getEnabledListenerPackages(context)
            if (enabledPackages.contains(context.packageName)) {
                return true
            }
        } catch (e: Exception) {
            // fallback
        }

        // 2. Settings.Secure check for OEMs
        val flat = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        )
        if (!TextUtils.isEmpty(flat)) {
            val names = flat.split(":").toTypedArray()
            val expectedComponent = ComponentName(context, AssistantNotificationListener::class.java).flattenToString()
            for (name in names) {
                if (name.contains(expectedComponent) || name.contains(context.packageName)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Checks if Accessibility Service is enabled in system settings.
     */
    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        if (AssistAccessibilityService.getInstance() != null) return true

        // Fallback check in Settings.Secure
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: ""
        val expectedComponent = ComponentName(context, AssistAccessibilityService::class.java).flattenToString()
        return enabledServices.contains(expectedComponent) || enabledServices.contains(context.packageName)
    }

    /**
     * Checks if Android's Advanced Protection Mode (APM) or Restricted Settings is likely blocking accessibility/sideload permissions.
     */
    fun isAdvancedProtectionOrRestricted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            !isAccessibilityServiceEnabled(context)
        } else {
            false
        }
    }

    fun getMissingPermissionForAction(context: Context, action: String): String? {
        return when (action) {
            "make_call" -> if (!hasCallPermission(context)) Manifest.permission.CALL_PHONE else null
            "send_sms" -> if (!hasSmsPermission(context)) Manifest.permission.SEND_SMS else null
            "read_notifications" -> if (!isNotificationListenerGranted(context)) "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS" else null
            "gui_action" -> if (!isAccessibilityServiceEnabled(context)) "android.settings.ACCESSIBILITY_SETTINGS" else null
            else -> null
        }
    }
}
