package com.awaj.assistant.voice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.awaj.assistant.AwajApplication
import com.awaj.assistant.MainActivity
import com.awaj.assistant.R

class VoiceService : Service() {

    private var wakeWordDetector: WakeWordDetector? = null
    private var wakeLock: PowerManager.WakeLock? = null

    companion object {
        const val CHANNEL_ID = "awaj_voice_channel"
        const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
            val intent = Intent(context, VoiceService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, VoiceService::class.java))
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())

        // Acquire partial wakelock to maintain audio processing while screen is off
        val powerManager = getSystemService(Context.POWER_SERVICE) as? PowerManager
        wakeLock = powerManager?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Awaj:VoiceWakeLock")
        wakeLock?.acquire(24 * 60 * 60 * 1000L) // 24 hours max with auto-release

        val voiceProfileManager = VoiceProfileManager(applicationContext)

        // Start Wake-word background listener with Voice Profile Biometrics
        wakeWordDetector = WakeWordDetector(this, voiceProfileManager) { wakeWord ->
            // Wake up screen and launch MainActivity
            val launchIntent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                putExtra("action_trigger_mic", true)
                putExtra("wake_word_triggered", wakeWord)
            }
            startActivity(launchIntent)
        }
        wakeWordDetector?.startContinuousListening()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        wakeWordDetector?.stopContinuousListening()
        wakeWordDetector = null

        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
        wakeLock = null
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("আওয়াজ ভয়েস সিকিউরিটি সক্রিয়")
            .setContentText("লক স্ক্রিনেও ‘হেই আওয়াজ’ শুনলে শুধুমাত্র আপনার কণ্ঠে জাগ্রত হবে")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Awaj Voice Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "ভয়েস সিকিউরিটি ও ওয়েক-ওয়ার্ড ব্যাকগ্রাউন্ড সার্ভিস নোটিফিকেশন"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }
}
