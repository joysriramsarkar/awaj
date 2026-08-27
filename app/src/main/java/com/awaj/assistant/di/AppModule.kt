package com.awaj.assistant.di

import android.content.Context
import com.awaj.assistant.agent.AgentLoop
import com.awaj.assistant.agent.AgentPlanner
import com.awaj.assistant.data.db.AppDatabase
import com.awaj.assistant.data.repository.CommandRepository
import com.awaj.assistant.data.repository.PreferenceRepository
import com.awaj.assistant.nlu.LlmClient
import com.awaj.assistant.nlu.RuleParser
import com.awaj.assistant.routines.RoutineManager
import com.awaj.assistant.safety.ConfirmationManager
import com.awaj.assistant.safety.RiskClassifier
import com.awaj.assistant.tools.ToolRegistry
import com.awaj.assistant.tts.TtsManager
import com.awaj.assistant.voice.VoiceProfileManager

class AppModule(private val context: Context) {

    val database by lazy { AppDatabase.getDatabase(context) }
    val commandRepository by lazy { CommandRepository(database.commandLogDao()) }
    val preferenceRepository by lazy { PreferenceRepository(context) }
    val voiceProfileManager by lazy { VoiceProfileManager(context) }

    val ruleParser by lazy { RuleParser() }
    val llmClient by lazy { LlmClient(preferenceRepository.geminiApiKey.value) }

    val confirmationManager by lazy { ConfirmationManager() }
    val toolRegistry by lazy { ToolRegistry() }
    val routineManager by lazy { RoutineManager(toolRegistry) }

    val agentPlanner by lazy { AgentPlanner() }
    val agentLoop by lazy { AgentLoop(agentPlanner) }

    val ttsManager by lazy { TtsManager(context) }
}
