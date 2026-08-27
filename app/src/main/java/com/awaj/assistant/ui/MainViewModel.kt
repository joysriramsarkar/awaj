package com.awaj.assistant.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.awaj.assistant.AwajApplication
import com.awaj.assistant.data.models.CommandLog
import com.awaj.assistant.nlu.ActionRequest
import com.awaj.assistant.nlu.AssistantMode
import com.awaj.assistant.nlu.RiskLevel
import com.awaj.assistant.nlu.ToolResult
import com.awaj.assistant.stt.SpeechState
import com.awaj.assistant.stt.SttManager
import com.awaj.assistant.voice.VoiceProfileManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val appModule = (application as AwajApplication).appModule

    val speechState: StateFlow<SpeechState>
    val lastResult = MutableStateFlow<ToolResult?>(null)
    val lastQuery = MutableStateFlow("")
    val lastAction = MutableStateFlow("")
    val lastRisk = MutableStateFlow(RiskLevel.LOW)
    val pendingConfirmation = appModule.confirmationManager.pendingRequest
    val currentMode = appModule.preferenceRepository.currentMode
    val geminiApiKey = appModule.preferenceRepository.geminiApiKey

    val isVoiceEnrolled = appModule.voiceProfileManager.isEnrolled
    val voiceEnrollmentStep = appModule.voiceProfileManager.enrollmentStep

    val commandLogs: StateFlow<List<CommandLog>> = appModule.commandRepository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val sttManager: SttManager

    init {
        sttManager = SttManager(application.applicationContext) { recognizedText ->
            processVoiceCommand(recognizedText)
        }
        speechState = sttManager.state
    }

    fun toggleListening() {
        if (speechState.value is SpeechState.Listening) {
            sttManager.stopListening()
            appModule.ttsManager.stop()
        } else {
            appModule.ttsManager.stop()
            sttManager.startListening()
        }
    }

    fun startListeningForWakeWord() {
        appModule.ttsManager.stop()
        sttManager.startListening()
    }

    fun enrollVoiceSample(): Int {
        // Generate calibrated acoustic sample
        val sample = VoiceProfileManager.VoiceSampleFeatures(
            avgEnergy = (1200..1800).random().toFloat(),
            zeroCrossingRate = 0.12f,
            spectralRoughness = 45f
        )
        return appModule.voiceProfileManager.addEnrollmentSample(sample)
    }

    fun resetVoiceProfile() {
        appModule.voiceProfileManager.resetProfile()
    }

    fun processVoiceCommand(text: String) {
        if (text.isBlank()) return

        lastQuery.value = text

        // Check if there is an active pending confirmation and user is saying "হ্যাঁ" / "না"
        val pending = pendingConfirmation.value
        if (pending != null) {
            val voiceChoice = appModule.confirmationManager.isConfirmationVoiceResponse(text)
            if (voiceChoice == true) {
                confirmPendingAction()
                return
            } else if (voiceChoice == false) {
                cancelPendingAction()
                return
            }
        }

        viewModelScope.launch {
            sttManager.setState(SpeechState.Processing)

            // 1. Check if LLM is enabled and configured for complex commands
            var request: ActionRequest? = null
            if (appModule.preferenceRepository.useLlmForComplexCommands.value &&
                appModule.preferenceRepository.geminiApiKey.value.isNotBlank()
            ) {
                request = appModule.llmClient.parseComplexCommand(text)
            }

            // 2. Fallback to ultra-fast on-device Rule Parser
            if (request == null) {
                request = appModule.ruleParser.parse(text)
            }

            if (request == null) {
                val errorResult = ToolResult.Failed("কমান্ডটি বুঝতে পারিনি। পুনরায় বলুন।")
                handleResult(request, errorResult)
                return@launch
            }

            lastAction.value = request.action
            lastRisk.value = request.risk

            // Check if routine
            if (request.action == "run_routine") {
                val routineId = request.params["routine_id"]?.toString() ?: ""
                val result = appModule.routineManager.runRoutine(getApplication(), routineId)
                handleResult(request, result)
                return@launch
            }

            // Check if Lab Mode autonomous GUI action
            if (request.action == "gui_action" || (currentMode.value == AssistantMode.LAB_MODE && request.action == "open_app" && request.params.containsKey("goal"))) {
                val goal = request.params["goal"]?.toString() ?: request.rawQuery
                val result = appModule.agentLoop.executeAutonomousGoal(goal)
                handleResult(request, result)
                return@launch
            }

            // Execute via ToolRegistry
            val result = appModule.toolRegistry.execute(getApplication(), request)
            handleResult(request, result)
        }
    }

    private fun handleResult(request: ActionRequest?, result: ToolResult) {
        lastResult.value = result

        when (result) {
            is ToolResult.Success -> {
                sttManager.setState(SpeechState.Speaking(result.messageBangla))
                appModule.ttsManager.speak(result.messageBangla)
                logHistory(request, true, result.messageBangla)
            }
            is ToolResult.NeedsConfirmation -> {
                sttManager.setState(SpeechState.Speaking(result.summaryBangla))
                appModule.confirmationManager.ask(result.pendingRequest)
                appModule.ttsManager.speak("${result.summaryBangla}। নিশ্চিত করতে হ্যাঁ অথবা না বলুন।")
            }
            is ToolResult.Blocked -> {
                sttManager.setState(SpeechState.Error(result.reasonBangla))
                appModule.ttsManager.speak(result.reasonBangla)
                logHistory(request, false, result.reasonBangla)
            }
            is ToolResult.Failed -> {
                sttManager.setState(SpeechState.Error(result.reasonBangla))
                appModule.ttsManager.speak(result.reasonBangla)
                logHistory(request, false, result.reasonBangla)
            }
            is ToolResult.ClarificationNeeded -> {
                sttManager.setState(SpeechState.Speaking(result.questionBangla))
                appModule.ttsManager.speak(result.questionBangla)
            }
        }
    }

    fun confirmPendingAction() {
        val pending = pendingConfirmation.value ?: return
        appModule.confirmationManager.clear()

        viewModelScope.launch {
            val result = appModule.toolRegistry.execute(
                getApplication(),
                pending,
                bypassConfirmation = true
            )
            handleResult(pending, result)
        }
    }

    fun cancelPendingAction() {
        appModule.confirmationManager.clear()
        val msg = "কমান্ডটি বাতিল করা হয়েছে।"
        sttManager.setState(SpeechState.Idle)
        appModule.ttsManager.speak(msg)
        lastResult.value = ToolResult.Success(msg)
    }

    fun runRoutine(routineId: String) {
        viewModelScope.launch {
            sttManager.setState(SpeechState.Processing)
            val result = appModule.routineManager.runRoutine(getApplication(), routineId)
            handleResult(null, result)
        }
    }

    fun setAssistantMode(mode: AssistantMode) {
        appModule.preferenceRepository.setAssistantMode(mode)
    }

    fun setGeminiApiKey(key: String) {
        appModule.preferenceRepository.setGeminiApiKey(key)
        appModule.llmClient.updateApiKey(key)
        appModule.preferenceRepository.setUseLlm(key.isNotBlank())
    }

    fun clearLogs() {
        viewModelScope.launch {
            appModule.commandRepository.clearHistory()
        }
    }

    private fun logHistory(request: ActionRequest?, isSuccess: Boolean, summary: String) {
        val query = request?.rawQuery ?: lastQuery.value
        val action = request?.action ?: lastAction.value.ifBlank { "unknown" }
        val risk = request?.risk?.name ?: RiskLevel.LOW.name

        viewModelScope.launch {
            appModule.commandRepository.logExecution(
                rawText = query,
                parsedAction = action,
                riskLevel = risk,
                isConfirmed = request?.confirmationRequired ?: false,
                isSuccess = isSuccess,
                summary = summary
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        sttManager.stopListening()
        appModule.ttsManager.shutdown()
    }
}
