package com.awaj.assistant.tools

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.awaj.assistant.nlu.IntentNormalizer
import com.awaj.assistant.nlu.ToolResult

class DeviceInfoTool : Tool {
    override val name: String = "get_device_info"
    override val descriptionBangla: String = "ব্যাটারি চার্জ এবং ডিভাইসের বর্তমান অবস্থা জানায়"

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        return try {
            val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
                context.registerReceiver(null, filter)
            }

            val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val batteryPct = if (level >= 0 && scale > 0) (level * 100 / scale) else 0

            val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL

            val banglaPct = IntentNormalizer.convertEnglishDigitsToBangla(batteryPct.toString())
            val chargeStatus = if (isCharging) "চার্জ হচ্ছে" else "ব্যাটারিতে চলছে"

            val message = "ফোনে বর্তমানে $banglaPct% চার্জ আছে এবং ফোনটি $chargeStatus।"
            ToolResult.Success(message, mapOf("battery" to batteryPct, "charging" to isCharging))
        } catch (e: Exception) {
            ToolResult.Failed("ডিভাইসের তথ্য যাচাই করা সম্ভব হয়নি: ${e.localizedMessage}")
        }
    }
}
