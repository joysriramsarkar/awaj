package com.awaj.assistant.tools

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.awaj.assistant.nlu.ToolResult
import java.net.URLEncoder

class WebSearchTool : Tool {
    override val name: String = "web_search"
    override val descriptionBangla: String = "গুগল ও ইন্টারনেটে তথ্য অনুসন্ধান করে"

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        val query = params["query"]?.toString()?.trim() ?: ""

        if (query.isBlank()) {
            return ToolResult.Failed("কী অনুসন্ধান করতে চান তা বলুন।")
        }

        return try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$encodedQuery")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ToolResult.Success("\"$query\" এর জন্য ওয়েব সার্চ খোলা হয়েছে।")
        } catch (e: Exception) {
            ToolResult.Failed("অনুসন্ধান করতে ব্যর্থ হয়েছে: ${e.localizedMessage}")
        }
    }
}
