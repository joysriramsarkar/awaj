package com.awaj.assistant.tools

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import com.awaj.assistant.accessibility.AssistAccessibilityService
import com.awaj.assistant.nlu.ToolResult
import kotlinx.coroutines.delay

class HotspotTool : Tool {
    override val name: String = "toggle_hotspot"
    override val descriptionBangla: String = "মোবাইল হটস্পট ও টেথারিং সরাসরি চালু, বন্ধ বা নিয়ন্ত্রণ করে"

    companion object {
        private var localReservation: WifiManager.LocalOnlyHotspotReservation? = null
    }

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        val state = params["state"]?.toString() ?: "open"
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        val accessibilityService = AssistAccessibilityService.getInstance()

        // 1. If Accessibility Service is enabled, automate the switch toggle directly
        if (accessibilityService != null) {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                component = ComponentName(
                    "com.android.settings",
                    "com.android.settings.TetherSettings"
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                val fallbackIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                context.startActivity(fallbackIntent)
            }

            // Wait briefly for UI render, toggle hotspot switch, and return
            delay(600)
            val toggled = accessibilityService.toggleSwitchByKeywords(
                listOf("হটস্পট", "মোবাইল হটস্পট", "Hotspot", "Portable hotspot", "Personal Hotspot", "Tethering")
            )

            if (toggled) {
                delay(300)
                accessibilityService.goBack()
                val summary = if (state == "on") "হটস্পট চালু করা হয়েছে।" else if (state == "off") "হটস্পট বন্ধ করা হয়েছে।" else "হটস্পট টগল করা হয়েছে।"
                return ToolResult.Success(summary)
            }
        }

        // 2. Direct Local Hotspot API (Android 8+)
        if (state == "on" && wifiManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                var started = false
                wifiManager.startLocalOnlyHotspot(object : WifiManager.LocalOnlyHotspotCallback() {
                    override fun onStarted(reservation: WifiManager.LocalOnlyHotspotReservation?) {
                        localReservation = reservation
                        started = true
                    }
                    override fun onFailed(reason: Int) {
                        super.onFailed(reason)
                    }
                }, Handler(Looper.getMainLooper()))

                delay(500)
                if (started || localReservation != null) {
                    return ToolResult.Success("হটস্পট সরাসরি চালু করা হয়েছে।")
                }
            } catch (e: Exception) {
                // Fallthrough to intent
            }
        } else if (state == "off" && localReservation != null) {
            try {
                localReservation?.close()
                localReservation = null
                return ToolResult.Success("হটস্পট বন্ধ করা হয়েছে।")
            } catch (e: Exception) {
                // Fallthrough
            }
        }

        // 3. Fallback: Open Tethering Settings
        return try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                component = ComponentName(
                    "com.android.settings",
                    "com.android.settings.TetherSettings"
                )
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                val fallbackIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
            }

            val actionSummary = when (state) {
                "on" -> "হটস্পট চালু করার জন্য সেটিংস খোলা হয়েছে। টগলটি অন করুন।"
                "off" -> "হটস্পট বন্ধ করার জন্য সেটিংস খোলা হয়েছে।"
                else -> "হটস্পট সেটিংস খোলা হয়েছে।"
            }
            ToolResult.Success(actionSummary)
        } catch (e: Exception) {
            ToolResult.Failed("হটস্পট সেটিংস খুলতে সমস্যা হয়েছে: ${e.localizedMessage}")
        }
    }
}
