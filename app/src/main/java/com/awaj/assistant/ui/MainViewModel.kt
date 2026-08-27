package com.awaj.assistant.ui

import android.app.Application
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
import com.awaj.assistant.ui.theme.AppThemeMode
import com.awaj.assistant.voice.LiveVoiceEnrollmentRecorder
import com.awaj.assistant.voice.VoiceService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    val themeMode = appModule.preferenceRepository.themeMode
    val geminiApiKey = appModule.preferenceRepository.geminiApiKey

    val isVoiceEnrolled = appModule.voiceProfileManager.isEnrolled
    val voiceEnrollmentStep = appModule.voiceProfileManager.enrollmentStep

    val isRecordingVoiceSample = MutableStateFlow(false)
    val voiceEnrollmentProgress = MutableStateFlow(0f)
    val voiceEnrollmentDb = MutableStateFlow(0f)

    val agentLiveReasoning = appModule.agentLoop.liveReasoning
    val isAgentRunning = appModule.agentLoop.isRunning

    val commandLogs: StateFlow<List<CommandLog>> = appModule.commandRepository.allLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val sttManager: SttManager
    private val voiceRecorder = LiveVoiceEnrollmentRecorder(application.applicationContext, appModule.voiceProfileManager)
    private var autoResetJob: Job? = null

    init {
        sttManager = SttManager(application.applicationContext) { recognizedText ->
            processVoiceCommand(recognizedText)
        }
        speechState = sttManager.state

        // Register TTS completion callback to cleanly transition back to Idle
        appModule.ttsManager.onSpeechCompletedListener = {
            autoResetJob?.cancel()
            if (speechState.value is SpeechState.Speaking) {
                sttManager.setState(SpeechState.Idle)
            }
        }
    }

    fun toggleListening() {
        autoResetJob?.cancel()
        val current = speechState.value

        when (current) {
            is SpeechState.Listening -> {
                sttManager.stopListening()
                appModule.ttsManager.stop()
                sttManager.setState(SpeechState.Idle)
            }
            is SpeechState.Speaking -> {
                appModule.ttsManager.stop()
                sttManager.setState(SpeechState.Idle)
            }
            else -> {
                appModule.ttsManager.stop()
                sttManager.startListening()
            }
        }
    }

    fun startListeningForWakeWord() {
        autoResetJob?.cancel()
        appModule.ttsManager.stop()
        sttManager.startListening()
    }

    fun recordRealVoiceSample(
        onSuccess: (step: Int) -> Unit,
        onError: (message: String) -> Unit
    ) {
        if (isRecordingVoiceSample.value) return

        viewModelScope.launch {
            isRecordingVoiceSample.value = true
            voiceEnrollmentProgress.value = 0f

            val result = voiceRecorder.recordEnrollmentSample { progress, db ->
                voiceEnrollmentProgress.value = progress
                voiceEnrollmentDb.value = db
            }

            isRecordingVoiceSample.value = false
            voiceEnrollmentProgress.value = 0f

            result.onSuccess { step ->
                if (step >= 3) {
                    // Auto start background voice service once enrolled
                    try {
                        VoiceService.start(getApplication())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    speakAndScheduleReset("আপনার কণ্ঠস্বর সফলভাবে সেভ করা হয়েছে। এখন লক থাকা অবস্থাতেও হেই আওয়াজ শুনলে আমি সাড়া দেব।")
                } else {
                    speakAndScheduleReset("নমুনা $step গৃহীত হয়েছে। পরবর্তী নমুনা রেকর্ড করতে আবার চাপুন।")
                }
                onSuccess(step)
            }.onFailure { error ->
                val errorMsg = error.localizedMessage ?: "রেকর্ডিং ব্যর্থ হয়েছে।"
                speakAndScheduleReset(errorMsg)
                onError(errorMsg)
            }
        }
    }

    fun resetVoiceProfile() {
        appModule.voiceProfileManager.resetProfile()
    }

    fun emergencyStopAgent() {
        appModule.agentLoop.emergencyStop()
    }

    fun setThemeMode(mode: AppThemeMode) {
        appModule.preferenceRepository.setThemeMode(mode)
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

            var request: ActionRequest? = null

            // 1. Try Gemini LLM for smart Q&A + Multi-intent if API Key is available
            if (appModule.preferenceRepository.geminiApiKey.value.isNotBlank()) {
                try {
                    request = appModule.llmClient.parseComplexCommand(text)
                } catch (e: Exception) {
                    // Graceful fallback to on-device RuleParser when offline / network error
                    request = null
                }
            }

            // 2. Fast on-device Rule Parser (100% Offline)
            if (request == null) {
                request = appModule.ruleParser.parse(text)
            }

            // 3. If RuleParser matched ai_chat without direct answer, ask Gemini or fallback to on-device knowledge
            if (request?.action == "ai_chat" && !request.params.containsKey("answer")) {
                val question = request.params["question"]?.toString() ?: text
                if (appModule.preferenceRepository.geminiApiKey.value.isNotBlank()) {
                    try {
                        val aiResponse = appModule.llmClient.askAiQuestion(question)
                        if (!aiResponse.isNullOrBlank()) {
                            request = ActionRequest(
                                action = "ai_chat",
                                params = mapOf("answer" to aiResponse),
                                risk = RiskLevel.LOW,
                                confirmationRequired = false,
                                rawQuery = text,
                                summaryBangla = aiResponse
                            )
                        }
                    } catch (e: Exception) {
                        // Keep request as ai_chat to let AiChatTool provide on-device Bengali answer
                    }
                }
            }

            if (request == null) {
                val isOnline = appModule.preferenceRepository.geminiApiKey.value.isNotBlank()
                val errorMsg = if (!isOnline) {
                    "অফলাইন মোড সক্রিয়। সহজ কমান্ড বলুন, যেমন: টর্চ জ্বালাও, আজকের তারিখ কত, অ্যালার্ম দাও, বা গান চালাও।"
                } else {
                    "কমান্ডটি বুঝতে পারিনি। পুনরায় বলুন।"
                }
                val errorResult = ToolResult.Failed(errorMsg)
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
                speakAndScheduleReset(result.messageBangla)
                logHistory(request, true, result.messageBangla)
            }
            is ToolResult.NeedsConfirmation -> {
                sttManager.setState(SpeechState.Speaking(result.summaryBangla))
                appModule.confirmationManager.ask(result.pendingRequest)
                speakAndScheduleReset("${result.summaryBangla}। নিশ্চিত করতে হ্যাঁ অথবা না বলুন।")
            }
            is ToolResult.Blocked -> {
                sttManager.setState(SpeechState.Error(result.reasonBangla))
                speakAndScheduleReset(result.reasonBangla)
                logHistory(request, false, result.reasonBangla)
            }
            is ToolResult.Failed -> {
                sttManager.setState(SpeechState.Error(result.reasonBangla))
                speakAndScheduleReset(result.reasonBangla)
                logHistory(request, false, result.reasonBangla)
            }
            is ToolResult.ClarificationNeeded -> {
                sttManager.setState(SpeechState.Speaking(result.questionBangla))
                speakAndScheduleReset(result.questionBangla)
            }
        }
    }

    private fun speakAndScheduleReset(message: String) {
        sttManager.setState(SpeechState.Speaking(message))
        appModule.ttsManager.speak(message)

        // Safety fallback timer: reset to Idle after speaking duration even if TTS callback is missed
        autoResetJob?.cancel()
        autoResetJob = viewModelScope.launch {
            val estimatedDurationMs = ((message.length * 95L) + 2000L).coerceAtLeast(3500L)
            delay(estimatedDurationMs)
            if (speechState.value is SpeechState.Speaking) {
                sttManager.setState(SpeechState.Idle)
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
        speakAndScheduleReset(msg)
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
        autoResetJob?.cancel()
        sttManager.stopListening()
        appModule.ttsManager.shutdown()
    }
}
