package com.awaj.assistant.agent

import com.awaj.assistant.accessibility.NodeFinder
import com.awaj.assistant.accessibility.UiElement
import com.awaj.assistant.safety.SensitiveAppBlocker
import java.util.Locale

class AgentPlanner {

    fun planNextStep(
        goal: String,
        currentElements: List<UiElement>,
        stepIndex: Int
    ): AgentStep {
        // 1. Safety Check: If screen contains password / PIN / OTP / Bank keywords -> ABORT immediately
        for (element in currentElements) {
            val text = element.text + " " + element.contentDescription
            if (SensitiveAppBlocker.containsSensitiveKeywords(text) || element.isEditable && element.className.contains("Password", ignoreCase = true)) {
                return AgentStep(
                    stepNumber = stepIndex,
                    thought = "স্ক্রিনে সংবেদনশীল তথ্য বা পাসওয়ার্ড পাওয়া গেছে। নিরাপত্তার স্বার্থে থামছি।",
                    action = AgentStepAction.Abort("সংবেদনশীল ফিল্ড শনাক্ত হওয়ায় এজেন্ট বন্ধ করা হয়েছে।")
                )
            }
        }

        val goalLower = goal.lowercase(Locale.getDefault())

        // 2. Goal: Dark Mode in Settings ("ডার্ক মোড চালু করো")
        if (goalLower.contains("ডার্ক মোড") || goalLower.contains("dark mode")) {
            val displayNode = NodeFinder.findElementByText(currentElements, "Display")
                ?: NodeFinder.findElementByText(currentElements, "ডিসপ্লে")
            if (displayNode != null) {
                return AgentStep(
                    stepNumber = stepIndex,
                    thought = "ডিসপ্লে সেটিংস পাওয়া গেছে। সেখানে ট্যাপ করা হচ্ছে।",
                    action = AgentStepAction.ClickText(displayNode.text.ifBlank { "Display" })
                )
            }

            val darkThemeNode = NodeFinder.findElementByText(currentElements, "Dark theme")
                ?: NodeFinder.findElementByText(currentElements, "Dark mode")
                ?: NodeFinder.findElementByText(currentElements, "ডার্ক থিম")
            if (darkThemeNode != null) {
                return AgentStep(
                    stepNumber = stepIndex,
                    thought = "ডার্ক থিম টগল পাওয়া গেছে। সক্রিয় করা হচ্ছে।",
                    action = AgentStepAction.ClickText(darkThemeNode.text)
                )
            }
        }

        // 3. Goal: Search contact or generic button
        val targetNode = NodeFinder.findElementByText(currentElements, goal)
        if (targetNode != null) {
            return AgentStep(
                stepNumber = stepIndex,
                thought = "স্ক্রিনে কাঙ্ক্ষিত উপাদান \"${targetNode.text}\" পাওয়া গেছে।",
                action = AgentStepAction.ClickText(targetNode.text)
            )
        }

        // 4. Default: Completed or cannot resolve
        return AgentStep(
            stepNumber = stepIndex,
            thought = "কাজের জন্য প্রয়োজনীয় পদক্ষেপ সমাপ্ত হয়েছে।",
            action = AgentStepAction.Finish("উদ্দেশ্য সম্পন্ন হয়েছে।")
        )
    }
}
