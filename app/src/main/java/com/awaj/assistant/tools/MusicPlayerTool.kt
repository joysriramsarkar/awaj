package com.awaj.assistant.tools

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import com.awaj.assistant.nlu.ToolResult

class MusicPlayerTool : Tool {
    override val name: String = "play_music_query"
    override val descriptionBangla: String = "ইউটিউব, স্পটিফাই বা মিডিয়া প্লেয়ারে গান বা শিল্পী খোঁজে ও চালায়"

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        val query = params["query"]?.toString() ?: ""
        val platform = params["platform"]?.toString() ?: "default"

        return try {
            if (platform == "youtube" || query.contains("ইউটিউব")) {
                val cleanQuery = query.replace("ইউটিউবে", "").replace("গান", "").trim()
                val ytIntent = Intent(Intent.ACTION_SEARCH).apply {
                    setPackage("com.google.android.youtube")
                    putExtra("query", cleanQuery.ifBlank { "Bangla Songs" })
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                try {
                    context.startActivity(ytIntent)
                } catch (e: Exception) {
                    val webYt = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(cleanQuery)}")).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(webYt)
                }
                return ToolResult.Success("ইউটিউবে \"$cleanQuery\" গান চালানো হচ্ছে।")
            }

            if (platform == "spotify" || query.contains("স্পটিফাই")) {
                val cleanQuery = query.replace("স্পটিফাইতে", "").replace("গান", "").trim()
                val spotifyIntent = Intent(Intent.ACTION_MAIN).apply {
                    action = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH
                    setPackage("com.spotify.music")
                    putExtra(SearchManager.QUERY, cleanQuery)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                try {
                    context.startActivity(spotifyIntent)
                } catch (e: Exception) {
                    val webSpotify = Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/search/${Uri.encode(cleanQuery)}")).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(webSpotify)
                }
                return ToolResult.Success("স্পটিফাইতে \"$cleanQuery\" গান বাজানো হচ্ছে।")
            }

            // General media intent
            val mediaIntent = Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH).apply {
                putExtra(SearchManager.QUERY, query)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(mediaIntent)
                ToolResult.Success("গান বাজানো হচ্ছে: $query")
            } catch (e: Exception) {
                // Fallback to YouTube
                val ytIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(query)}")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(ytIntent)
                ToolResult.Success("\"$query\" গান চালানো হচ্ছে।")
            }
        } catch (e: Exception) {
            ToolResult.Failed("গান চালাতে সমস্যা হয়েছে: ${e.localizedMessage}")
        }
    }
}
