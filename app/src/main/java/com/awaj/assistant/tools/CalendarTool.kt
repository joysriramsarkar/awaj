package com.awaj.assistant.tools

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import com.awaj.assistant.nlu.ToolResult
import java.util.Calendar

class CalendarTool : Tool {
    override val name: String = "add_calendar_event"
    override val descriptionBangla: String = "ক্যালেন্ডারে নতুন ইভেন্ট বা রিমাইন্ডার যোগ করে"

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        val title = params["title"]?.toString() ?: "Awaj মিটিং ও ইভেন্ট"

        val startTime = Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, 1)
            set(Calendar.MINUTE, 0)
        }
        val endTime = Calendar.getInstance().apply {
            add(Calendar.HOUR_OF_DAY, 2)
            set(Calendar.MINUTE, 0)
        }

        return try {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, title)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime.timeInMillis)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.timeInMillis)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ToolResult.Success("ক্যালেন্ডারে \"$title\" ইভেন্ট তৈরির স্ক্রিন খোলা হয়েছে।")
        } catch (e: Exception) {
            ToolResult.Failed("ক্যালেন্ডার ইভেন্ট যোগ করতে সমস্যা হয়েছে: ${e.localizedMessage}")
        }
    }
}
