package com.awaj.assistant.safety

import com.awaj.assistant.nlu.ActionRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class ConfirmationManager {

    private val _pendingRequest = MutableStateFlow<ActionRequest?>(null)
    val pendingRequest: StateFlow<ActionRequest?> = _pendingRequest.asStateFlow()

    fun ask(request: ActionRequest) {
        _pendingRequest.value = request
    }

    fun clear() {
        _pendingRequest.value = null
    }

    fun isConfirmationVoiceResponse(text: String): Boolean? {
        val lower = text.lowercase(Locale.getDefault()).trim()
        val yesKeywords = listOf("হ্যাঁ", "হ্যা", "পাঠাও", "করো", "কর", "কল দাও", "দাও", "হাঁ", "yes", "confirm", "proceed", "send")
        val noKeywords = listOf("না", "বাতিল", "থামো", "করোনা", "করো না", "দরকার নেই", "no", "cancel", "stop", "abort")

        if (yesKeywords.any { lower == it || lower.contains(it) }) return true
        if (noKeywords.any { lower == it || lower.contains(it) }) return false

        return null
    }
}
