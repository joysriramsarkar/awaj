package com.awaj.assistant.agent

import com.awaj.assistant.accessibility.UiElement

sealed class AgentStepAction {
    data class ClickText(val text: String) : AgentStepAction()
    data class ClickId(val viewId: String) : AgentStepAction()
    data class Scroll(val direction: String) : AgentStepAction()
    data class Finish(val summary: String) : AgentStepAction()
    data class Abort(val reason: String) : AgentStepAction()
}

data class AgentStep(
    val stepNumber: Int,
    val thought: String,
    val action: AgentStepAction
)

data class AgentSession(
    val goal: String,
    val maxSteps: Int = 4,
    var currentStep: Int = 0,
    val history: MutableList<AgentStep> = mutableListOf(),
    var isCompleted: Boolean = false
)
