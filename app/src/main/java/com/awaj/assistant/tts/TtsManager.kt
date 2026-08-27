package com.awaj.assistant.tts

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(
    context: Context
) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    var onSpeechCompletedListener: (() -> Unit)? = null

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                // Try Bengali BD first, then Bengali IN, then default
                val banglaBD = Locale("bn", "BD")
                val banglaIN = Locale("bn", "IN")

                val result = tts?.setLanguage(banglaBD)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.setLanguage(banglaIN)
                }

                tts?.setSpeechRate(0.95f)
                tts?.setPitch(1.0f)

                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                        mainHandler.post {
                            onSpeechCompletedListener?.invoke()
                        }
                    }

                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                        mainHandler.post {
                            onSpeechCompletedListener?.invoke()
                        }
                    }
                })
            } else {
                isInitialized = false
            }
        }
    }

    fun speak(textBangla: String) {
        if (textBangla.isBlank()) {
            onSpeechCompletedListener?.invoke()
            return
        }

        if (!isInitialized || tts == null) {
            // TTS engine not available or Bengali not installed on device
            _isSpeaking.value = false
            // Give brief delay for UI readability before auto-resetting
            mainHandler.postDelayed({
                onSpeechCompletedListener?.invoke()
            }, 2500)
            return
        }

        stop()
        val utteranceId = "Awaj_${System.currentTimeMillis()}"
        val result = tts?.speak(textBangla, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        if (result != TextToSpeech.SUCCESS) {
            _isSpeaking.value = false
            mainHandler.postDelayed({
                onSpeechCompletedListener?.invoke()
            }, 2000)
        }
    }

    fun stop() {
        if (tts?.isSpeaking == true) {
            tts?.stop()
        }
        _isSpeaking.value = false
    }

    fun setSpeechRate(rate: Float) {
        tts?.setSpeechRate(rate)
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
    }
}
