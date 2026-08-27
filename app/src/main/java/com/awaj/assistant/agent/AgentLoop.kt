package com.awaj.assistant.agent

import com.awaj.assistant.accessibility.AssistAccessibilityService
import com.awaj.assistant.nlu.ToolResult
import com.awaj.assistant.safety.PermissionGate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AgentLoop(
    private val planner: AgentPlanner = AgentPlanner()
) {

    private val _liveReasoning = MutableStateFlow("")
    val liveReasoning: StateFlow<String> = _liveReasoning.asStateFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()

    fun emergencyStop() {
        _isRunning.value = false
        _liveReasoning.value = "জরুরি ভিত্তিতে এজেন্ট থামানো হয়েছে।"
    }

    suspend fun executeAutonomousGoal(goal: String): ToolResult {
        val service = AssistAccessibilityService.getInstance()
        if (service == null) {
            val ctx = AssistAccessibilityService.getInstance()?.applicationContext
            val isRestricted = if (ctx != null) PermissionGate.isAdvancedProtectionOrRestricted(ctx) else false
            return ToolResult.Blocked(
                if (isRestricted) {
                    "অ্যান্ড্রয়েডের Advanced Protection Mode (APM) বা Restricted Settings সক্রিয় থাকায় অ্যাক্সেসিবিলিটি সার্ভিস ব্লক করা হয়েছে। ল্যাব মোড স্বয়ংক্রিয়ভাবে স্থগিত করা হয়েছে।"
                } else {
                    "অ্যাক্সেসিবিলিটি সার্ভিস সক্রিয় নেই। অনুগ্রহ করে পারমিশন স্ক্রিন থেকে অ্যাক্সেসিবিলিটি চালু করুন।"
                }
            )
        }

        _isRunning.value = true
        _liveReasoning.value = "লক্ষ্য শুরু হচ্ছে: $goal"
        val session = AgentSession(goal = goal, maxSteps = 4)

        try {
            while (session.currentStep < session.maxSteps && !session.isCompleted && _isRunning.value) {
                session.currentStep++
                _liveReasoning.value = "স্ক্রিন লোড হচ্ছে (ধাপ ${session.currentStep}/${session.maxSteps})..."
                delay(900) // Wait for screen transition

                val currentElements = service.getCurrentScreenElements()
                if (currentElements.isEmpty()) {
                    delay(1000)
                }

                _liveReasoning.value = "স্ক্রিনের উপাদান বিশ্লেষণ ও পরবর্তী পদক্ষেপ চিন্তা করা হচ্ছে..."
                val step = planner.planNextStep(goal, currentElements, session.currentStep)
                session.history.add(step)

                _liveReasoning.value = "চিন্তা: ${step.thought}"
                delay(600)

                when (val action = step.action) {
                    is AgentStepAction.ClickText -> {
                        _liveReasoning.value = "বাটনে ট্যাপ করা হচ্ছে: \"${action.text}\""
                        val clicked = service.clickOnText(action.text)
                        if (!clicked) {
                            return ToolResult.Failed("\"${action.text}\" বাটনে ট্যাপ করা সম্ভব হয়নি।")
                        }
                    }
                    is AgentStepAction.ClickId -> {
                        _liveReasoning.value = "উপাদান ট্যাপ করা হচ্ছে..."
                    }
                    is AgentStepAction.Scroll -> {
                        _liveReasoning.value = "স্ক্রল করা হচ্ছে..."
                    }
                    is AgentStepAction.Finish -> {
                        session.isCompleted = true
                        _liveReasoning.value = "কাজ সম্পন্ন: ${action.summary}"
                        return ToolResult.Success("এজেন্ট কাজ সফলভাবে সম্পন্ন করেছে: ${action.summary}")
                    }
                    is AgentStepAction.Abort -> {
                        _liveReasoning.value = "কাজ স্থগিত: ${action.reason}"
                        return ToolResult.Blocked("নিরাপত্তা নীতি অনুযায়ী এজেন্ট থেমে গেছে: ${action.reason}")
                    }
                }
            }
            return ToolResult.Success("এজেন্টের ধাপসমূহ সমাপ্ত হয়েছে।")
        } finally {
            _isRunning.value = false
        }
    }
}
