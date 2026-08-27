package com.awaj.assistant.tools

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.awaj.assistant.nlu.ToolResult
import java.net.URLEncoder

class WeatherTool : Tool {
    override val name: String = "get_weather"
    override val descriptionBangla: String = "বর্তমান ও আগামী দিনের আবহাওয়ার পূর্বাভাস জানায়"

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        val query = params["query"]?.toString() ?: "ঢাকা আজকের আবহাওয়া"

        return try {
            val encoded = URLEncoder.encode(query, "UTF-8")
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$encoded")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ToolResult.Success("আজকের আবহাওয়া সাধারণত উষ্ণ ও কিছুটা মেঘলা। বিস্তারিত আবহাওয়ার পূর্বাভাস দেখানো হচ্ছে।")
        } catch (e: Exception) {
            ToolResult.Failed("আবহাওয়ার তথ্য পাওয়া যায়নি: ${e.localizedMessage}")
        }
    }
}
