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

        val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat).coerceAtLeast(3200)

        try {
            audioRecord = try {
                val rec = AudioRecord(
                    MediaRecorder.AudioSource.VOICE_RECOGNITION,
                    sampleRate,
                    channelConfig,
                    audioFormat,
                    minBufferSize * 2
                )
                if (rec.state == AudioRecord.STATE_INITIALIZED) rec else {
                    rec.release()
                    AudioRecord(
                        MediaRecorder.AudioSource.MIC,
                        sampleRate,
                        channelConfig,
                        audioFormat,
                        minBufferSize * 2
                    )
                }
            } catch (e: Exception) {
                AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    channelConfig,
                    audioFormat,
                    minBufferSize * 2
                )
            }

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                audioRecord?.release()
                audioRecord = null
                return
            }

            audioRecord?.startRecording()
            _isListening.value = true

            recordingJob = scope.launch {
                val buffer = ShortArray(minBufferSize)
                val windowSize = sampleRate * 1 // 1.0 second sliding window (16000 samples)
                val accumulatedWindow = ShortArray(windowSize)
                var accumulatedSamples = 0
                var activeSpeechFrames = 0

                while (isActive && _isListening.value) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        var sumSquare = 0.0
                        for (i in 0 until read) {
                            val sample = buffer[i].toInt()
                            sumSquare += sample * sample
                        }
                        val rms = sqrt(sumSquare / read)

                        // Convert energy to dB
                        val db = if (rms > 1.0) 20 * Math.log10(rms) else 0.0

                        // Append to sliding window
                        if (accumulatedSamples + read <= windowSize) {
                            System.arraycopy(buffer, 0, accumulatedWindow, accumulatedSamples, read)
                            accumulatedSamples += read
                        } else {
                            val shift = (accumulatedSamples + read) - windowSize
                            System.arraycopy(accumulatedWindow, shift, accumulatedWindow, 0, windowSize - shift)
                            System.arraycopy(buffer, 0, accumulatedWindow, windowSize - read, read)
                            accumulatedSamples = windowSize
                        }

                        // Voice activity detection threshold (human speech typically > 42 dB)
                        if (db > 42.0) {
                            activeSpeechFrames++
                            if (activeSpeechFrames >= 3 && accumulatedSamples >= 8000) {
                                activeSpeechFrames = 0

                                // Check personalized acoustic embedding similarity across accumulated window
                                val isPersonalMatch = if (voiceProfileManager != null && voiceProfileManager.isEnrolled.value) {
                                    voiceProfileManager.verifySpeaker(accumulatedWindow, accumulatedSamples)
                                } else {
                                    true
                                }

                                if (isPersonalMatch) {
                                    accumulatedSamples = 0
                                    onWakeWordDetected("হেই আওয়াজ")
                                }
                            }
                        } else {
                            if (activeSpeechFrames > 0) activeSpeechFrames--
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
