package com.awaj.assistant.tools

import android.content.Context
import com.awaj.assistant.nlu.LlmClient
import com.awaj.assistant.nlu.ToolResult
import java.util.Locale

class AiChatTool(
    private val llmClient: LlmClient
) : Tool {
    override val name: String = "ai_chat"
    override val descriptionBangla: String = "এআই বুদ্ধিমত্তার সাহায্যে যেকোনো সাধারণ জ্ঞান, বিজ্ঞান, সাহিত্য ও প্রশ্নের উত্তর দেয়"

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        // 1. Direct answer passed by RuleParser
        val directAnswer = params["answer"]?.toString()
        if (!directAnswer.isNullOrBlank()) {
            return ToolResult.Success(directAnswer)
        }

        val question = params["question"]?.toString() ?: params["query"]?.toString() ?: ""
        if (question.isBlank()) {
            return ToolResult.Success("আমি শুনছি, যেকোনো প্রশ্ন বা আদেশ করতে পারেন।")
        }

        // 2. Try Gemini AI Client if API Key is available
        if (llmClient.hasApiKey()) {
            try {
                val aiAnswer = llmClient.askAiQuestion(question)
                if (!aiAnswer.isNullOrBlank()) {
                    return ToolResult.Success(aiAnswer)
                }
            } catch (e: Exception) {
                // Fallthrough to on-device knowledge engine
            }
        }

        // 3. Fast On-Device Bengali Knowledge Engine (100% Offline)
        val qLower = question.lowercase(Locale.getDefault())
        val offlineAnswer = when {
            qLower.contains("বাংলাদেশের রাজধানী") || qLower.contains("ঢাকা") -> "বাংলাদেশের রাজধানী ঢাকা।"
            qLower.contains("ভারতের রাজধানী") || qLower.contains("দিল্লি") -> "ভারতের রাজধানী নয়াদিল্লি।"
            qLower.contains("পশ্চিমবঙ্গের রাজধানী") || qLower.contains("কলকাতা") -> "পশ্চিমবঙ্গের রাজধানী কলকাতা।"
            qLower.contains("জাতীয় কবি") || qLower.contains("জাতীয় কবি") -> "বাংলাদেশের জাতীয় কবি কাজী নজরুল ইসলাম।"
            qLower.contains("জাতীয় ফল") || qLower.contains("জাতীয় ফল") -> "বাংলাদেশের জাতীয় ফল কাঁঠাল।"
            qLower.contains("জাতীয় ফুল") || qLower.contains("জাতীয় ফুল") -> "বাংলাদেশের জাতীয় ফুল শাপলা।"
            qLower.contains("জাতীয় পশু") || qLower.contains("জাতীয় পশু") -> "রয়েল বেঙ্গল টাইগার।"
            qLower.contains("স্বাধীনতা দিবস") -> "বাংলাদেশের স্বাধীনতা দিবস ২৬শে মার্চ এবং ভারতের ১৫ই আগস্ট।"
            qLower.contains("বিজয় দিবস") || qLower.contains("বিজয় দিবস") -> "বাংলাদেশের বিজয় দিবস ১৬ই ডিসেম্বর।"
            qLower.contains("আন্তর্জাতিক মাতৃভাষা দিবস") || qLower.contains("একুশে ফেব্রুয়ারি") -> "আন্তর্জাতিক মাতৃভাষা দিবস ২১শে ফেব্রুয়ারি।"
            qLower.contains("সূর্য কোন দিকে") -> "সূর্য পূর্ব দিকে ওঠে এবং পশ্চিম দিকে অস্ত যায়।"
            qLower.contains("পৃথিবী কি গোল") || qLower.contains("পৃথিবীর আকার") -> "পৃথিবী সম্পূর্ণ গোল নয়, এটি অভিগত গোলক বা উপবৃত্তাকার।"
            qLower.contains("চাঁদে প্রথম") -> "১৯৬৯ সালে নীল আর্মস্ট্রং প্রথম চাঁদে পা রাখেন।"
            else -> null
        }

        if (offlineAnswer != null) {
            return ToolResult.Success(offlineAnswer)
        }

        return ToolResult.Success(
            "\"$question\" প্রশ্নের বিস্তারিত ব্যাখ্যার জন্য সেটিংসে Gemini API Key যুক্ত করুন অথবা বলুন 'গুগলে সার্চ করো'।"
        )
    }
}
