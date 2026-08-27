package com.awaj.assistant.voice

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.sqrt

class WakeWordDetector(
    private val context: Context,
    private val voiceProfileManager: VoiceProfileManager? = null,
    private val onWakeWordDetected: (wakeWord: String) -> Unit
) {
    private var recordingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val wakeWords = listOf(
        "হেই আওয়াজ", "হে আওয়াজ", "আওয়াজ", "hey awaj", "হে আওয়াজ শোনো", "শোনো আওয়াজ", "দোসর"
    )

    private var audioRecord: AudioRecord? = null
    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT

    fun matchesWakeWord(text: String): Boolean {
        val cleanText = text.lowercase(Locale.getDefault()).trim()
        return wakeWords.any { cleanText == it || cleanText.startsWith(it) || cleanText.contains(it) }
    }

    /**
     * Continuous background acoustic energy and wake-word trigger listener with personalized acoustic filter.
     */
    @SuppressLint("MissingPermission")
    fun startContinuousListening() {
        if (_isListening.value) return

        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
        if (bufferSize <= 0) return

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                audioRecord?.release()
                audioRecord = null
                return
            }

            audioRecord?.startRecording()
            _isListening.value = true

            recordingJob = scope.launch {
                val buffer = ShortArray(bufferSize)
                var soundBurstCount = 0

                while (isActive && _isListening.value) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        var sumSquare = 0.0
                        for (i in 0 until read) {
                            sumSquare += buffer[i] * buffer[i]
                        }
                        val rms = sqrt(sumSquare / read)

                        // Convert energy to dB
                        val db = if (rms > 1.0) 20 * Math.log10(rms) else 0.0

                        // Voice activity detection threshold (above ~50dB)
                        if (db > 48.0) {
                            soundBurstCount++
                            if (soundBurstCount >= 3) {
                                soundBurstCount = 0

                                // Check personalized acoustic embedding similarity
                                val isPersonalMatch = if (voiceProfileManager != null && voiceProfileManager.isEnrolled.value) {
                                    voiceProfileManager.verifySpeaker(buffer, read)
                                } else {
                                    true
                                }

                                if (isPersonalMatch) {
                                    onWakeWordDetected("হেই আওয়াজ")
                                }
                            }
                        } else {
                            if (soundBurstCount > 0) soundBurstCount--
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _isListening.value = false
        }
    }

    fun stopContinuousListening() {
        _isListening.value = false
        recordingJob?.cancel()
        recordingJob = null
        try {
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
