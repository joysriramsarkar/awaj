package com.awaj.assistant.stt

sealed class SpeechState {
    object Idle : SpeechState()
    data class Listening(val rmsDb: Float = 0f) : SpeechState()
    object Processing : SpeechState()
    data class Recognized(val text: String) : SpeechState()
    data class Speaking(val text: String) : SpeechState()
    data class Error(val messageBangla: String) : SpeechState()
}
