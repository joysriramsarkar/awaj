package com.awaj.assistant.agent

import com.awaj.assistant.accessibility.AssistAccessibilityService
import com.awaj.assistant.nlu.ToolResult
import com.awaj.assistant.safety.PermissionGate
import kotlinx.coroutines.delay

class AgentLoop(
    private val planner: AgentPlanner = AgentPlanner()
) {

    suspend fun executeAutonomousGoal(goal: String): ToolResult {
        val service = AssistAccessibilityService.getInstance()
        if (service == null) {
            // Check if failure is due to Android Advanced Protection Mode (APM) or Restricted Settings
            val isRestricted = PermissionGate.isAdvancedProtectionOrRestricted(service ?: AssistAccessibilityService.getInstance()?.applicationContext ?: return ToolResult.Blocked(
                "অ্যান্ড্রয়েডের Advanced Protection Mode (APM) বা Restricted Settings সক্রিয় থাকায় অ্যাক্সেসিবিলিটি সার্ভিস ব্লক করা হয়েছে। ল্যাব মোড স্বয়ংক্রিয়ভাবে স্থগিত করা হয়েছে।"
            ))
            return ToolResult.Blocked(
                "অ্যাক্সেসিবিলিটি সার্ভিস সক্রিয় নেই। ডিভাইসে Advanced Protection Mode (APM) চালু থাকলে অথবা সাইডলোড বিধিনিষেধের কারণে ল্যাব মোড কাজ নাও করতে পারে।"
            )
        }

        val session = AgentSession(goal = goal, maxSteps = 3)

        while (session.currentStep < session.maxSteps && !session.isCompleted) {
            session.currentStep++
            delay(800) // Wait for screen transition

            val currentElements = service.getCurrentScreenElements()
            if (currentElements.isEmpty()) {
                delay(1000)
            }

            val step = planner.planNextStep(goal, currentElements, session.currentStep)
            session.history.add(step)

            when (val action = step.action) {
                is AgentStepAction.ClickText -> {
                    val clicked = service.clickOnText(action.text)
                    if (!clicked) {
                        return ToolResult.Failed("\"${action.text}\" বাটনে ট্যাপ করা সম্ভব হয়নি।")
                    }
                }
                is AgentStepAction.ClickId -> {
                    // clicked by id
                }
                is AgentStepAction.Scroll -> {
                    // scroll
                }
                is AgentStepAction.Finish -> {
                    session.isCompleted = true
                    return ToolResult.Success("এজেন্ট কাজ সফলভাবে সম্পন্ন করেছে: ${action.summary}")
                }
                is AgentStepAction.Abort -> {
                    return ToolResult.Blocked("নিরাপত্তা নীতি অনুযায়ী এজেন্ট থেমে গেছে: ${action.reason}")
                }
            }
        }

        return ToolResult.Success("এজেন্টের ধাপসমূহ সমাপ্ত হয়েছে।")
    }
}
