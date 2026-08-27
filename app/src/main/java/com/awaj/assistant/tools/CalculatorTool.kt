package com.awaj.assistant.tools

import android.content.Context
import com.awaj.assistant.nlu.IntentNormalizer
import com.awaj.assistant.nlu.ToolResult

class CalculatorTool : Tool {
    override val name: String = "calculate_math"
    override val descriptionBangla: String = "বাংলায় যোগ, বিয়োগ, গুণ, ভাগ ও গাণিতিক হিসাব সমাধান করে"

    override suspend fun execute(context: Context, params: Map<String, Any>): ToolResult {
        val num1 = (params["num1"] as? Number)?.toDouble() ?: 0.0
        val num2 = (params["num2"] as? Number)?.toDouble() ?: 0.0
        val op = params["operation"]?.toString() ?: "add"

        val result = when (op) {
            "add" -> num1 + num2
            "subtract" -> num1 - num2
            "multiply" -> num1 * num2
            "divide" -> if (num2 != 0.0) num1 / num2 else Double.NaN
            else -> num1 + num2
        }

        if (result.isNaN()) {
            return ToolResult.Failed("শূন্য দিয়ে ভাগ করা সম্ভব নয়।")
        }

        val formattedResult = if (result % 1.0 == 0.0) {
            result.toLong().toString()
        } else {
            String.format(java.util.Locale.US, "%.2f", result)
        }

        val banglaResult = IntentNormalizer.convertEnglishDigitsToBangla(formattedResult)
        return ToolResult.Success("গণনার ফলাফল হলো $banglaResult")
    }
}
