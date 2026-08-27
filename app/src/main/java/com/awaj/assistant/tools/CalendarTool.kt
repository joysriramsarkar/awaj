package com.awaj.assistant.tools

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import com.awaj.assistant.nlu.IntentNormalizer
import com.awaj.assistant.nlu.ToolResult
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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

class DateTimeTool : Tool {
    override val name: String = "get_datetime_info"
    override val descriptionBangla: String = "আজকের দিন, তারিখ, বার ও বর্তমান সময় সরাসরি বাংলায় জানায়"

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        val type = params["type"]?.toString() ?: "all"
        val cal = Calendar.getInstance()

        val dayOfWeek = when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SATURDAY -> "শনিবার"
            Calendar.SUNDAY -> "রবিবার"
            Calendar.MONDAY -> "সোমবার"
            Calendar.TUESDAY -> "মঙ্গলবার"
            Calendar.WEDNESDAY -> "বুধবার"
            Calendar.THURSDAY -> "বৃহস্পতিবার"
            Calendar.FRIDAY -> "শুক্রবার"
            else -> "আজকের দিন"
        }

        val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
        val monthName = when (cal.get(Calendar.MONTH)) {
            Calendar.JANUARY -> "জানুয়ারি"
            Calendar.FEBRUARY -> "ফেব্রুয়ারি"
            Calendar.MARCH -> "মার্চ"
            Calendar.APRIL -> "এপ্রিল"
            Calendar.MAY -> "মে"
            Calendar.JUNE -> "জুন"
            Calendar.JULY -> "জুলাই"
            Calendar.AUGUST -> "আগস্ট"
            Calendar.SEPTEMBER -> "সেপ্টেম্বর"
            Calendar.OCTOBER -> "অক্টোবর"
            Calendar.NOVEMBER -> "নভেম্বর"
            Calendar.DECEMBER -> "ডিসেম্বর"
            else -> ""
        }
        val year = cal.get(Calendar.YEAR)

        val hour24 = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val period = when {
            hour24 in 4..5 -> "ভোর"
            hour24 in 6..11 -> "সকাল"
            hour24 in 12..14 -> "দুপুর"
            hour24 in 15..17 -> "বিকেল"
            hour24 in 18..19 -> "সন্ধ্যা"
            else -> "রাত"
        }
        val hour12 = cal.get(Calendar.HOUR).let { if (it == 0) 12 else it }

        val dayBangla = IntentNormalizer.convertEnglishDigitsToBangla(dayOfMonth.toString())
        val yearBangla = IntentNormalizer.convertEnglishDigitsToBangla(year.toString())
        val hourBangla = IntentNormalizer.convertEnglishDigitsToBangla(hour12.toString())
        val minuteBangla = IntentNormalizer.convertEnglishDigitsToBangla(minute.toString())

        val answer = when (type) {
            "day" -> "আজ $dayOfWeek।"
            "date" -> "আজকের তারিখ $dayBangla $monthName $yearBangla।"
            "time" -> if (minute == 0) "এখন $period $hourBangla টা বাজে।" else "এখন $period $hourBangla টা $minuteBangla মিনিট।"
            else -> "আজ $dayOfWeek, $dayBangla $monthName $yearBangla। এখন $period $hourBangla টা $minuteBangla মিনিট।"
        }

        return ToolResult.Success(answer)
    }
}
