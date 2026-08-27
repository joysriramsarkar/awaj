package com.awaj.assistant.tools

import android.content.Context
import com.awaj.assistant.nlu.LlmClient
import com.awaj.assistant.nlu.ToolResult

class AiChatTool(
    private val llmClient: LlmClient
) : Tool {
    override val name: String = "ai_chat"
    override val descriptionBangla: String = "এআই বুদ্ধিমত্তার সাহায্যে যেকোনো সাধারণ জ্ঞান, বিজ্ঞান, সাহিত্য ও প্রশ্নের উত্তর দেয়"

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        val directAnswer = params["answer"]?.toString()
        if (!directAnswer.isNullOrBlank()) {
            return ToolResult.Success(directAnswer)
        }

        val question = params["query"]?.toString() ?: ""
        if (question.isNotBlank()) {
            val aiAnswer = llmClient.askAiQuestion(question)
            if (!aiAnswer.isNullOrBlank()) {
                return ToolResult.Success(aiAnswer)
            }
        }

        return ToolResult.Success("এআই দিয়ে যেকোনো প্রশ্নের উত্তর পেতে সেটিংস থেকে আপনার Gemini API Key যুক্ত করুন।")
    }
}
