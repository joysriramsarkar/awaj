package com.awaj.assistant.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.awaj.assistant.data.db.CommandLogDao
import com.awaj.assistant.data.models.CommandLog
import com.awaj.assistant.nlu.AssistantMode
import com.awaj.assistant.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CommandRepository(
    private val commandLogDao: CommandLogDao
) {
    val allLogs: Flow<List<CommandLog>> = commandLogDao.getAllLogs()

    suspend fun logExecution(
        rawText: String,
        parsedAction: String,
        riskLevel: String,
        isConfirmed: Boolean,
        isSuccess: Boolean,
        summary: String
    ) {
        val log = CommandLog(
            rawText = rawText,
            parsedAction = parsedAction,
            riskLevel = riskLevel,
            isConfirmed = isConfirmed,
            isSuccess = isSuccess,
            resultSummary = summary
        )
        commandLogDao.insertLog(log)
    }

    suspend fun clearHistory() {
        commandLogDao.clearAllLogs()
    }
}

class PreferenceRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("awaj_preferences", Context.MODE_PRIVATE)

    private val _currentMode = MutableStateFlow(loadMode())
    val currentMode: StateFlow<AssistantMode> = _currentMode.asStateFlow()

    private val _themeMode = MutableStateFlow(loadThemeMode())
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private val _geminiApiKey = MutableStateFlow(prefs.getString("gemini_api_key", "") ?: "")
    val geminiApiKey: StateFlow<String> = _geminiApiKey.asStateFlow()

    private val _useLlmForComplexCommands = MutableStateFlow(prefs.getBoolean("use_llm", true))
    val useLlmForComplexCommands: StateFlow<Boolean> = _useLlmForComplexCommands.asStateFlow()

    private fun loadMode(): AssistantMode {
        val modeStr = prefs.getString("assistant_mode", AssistantMode.SAFE_MODE.name)
        return try {
            AssistantMode.valueOf(modeStr ?: AssistantMode.SAFE_MODE.name)
        } catch (e: Exception) {
            AssistantMode.SAFE_MODE
        }
    }

    private fun loadThemeMode(): AppThemeMode {
        val themeStr = prefs.getString("theme_mode", AppThemeMode.DARK.name)
        return try {
            AppThemeMode.valueOf(themeStr ?: AppThemeMode.DARK.name)
        } catch (e: Exception) {
            AppThemeMode.DARK
        }
    }

    fun setAssistantMode(mode: AssistantMode) {
        prefs.edit().putString("assistant_mode", mode.name).apply()
        _currentMode.value = mode
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _themeMode.value = mode
    }

    fun setGeminiApiKey(key: String) {
        prefs.edit().putString("gemini_api_key", key).apply()
        _geminiApiKey.value = key
    }

    fun setUseLlm(enable: Boolean) {
        prefs.edit().putBoolean("use_llm", enable).apply()
        _useLlmForComplexCommands.value = enable
    }
}
