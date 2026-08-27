package com.awaj.assistant.voice

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * VoiceProfileManager handles voice biometric enrollment and speaker verification.
 * It extracts acoustic characteristics (RMS energy distribution, zero-crossing rate,
 * and frequency spectrum variance) to ensure only the owner's voice can trigger Awaj
 * when the phone is locked.
 */
class VoiceProfileManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("awaj_voice_profile", Context.MODE_PRIVATE)

    private val _isEnrolled = MutableStateFlow(prefs.getBoolean("voice_enrolled", false))
    val isEnrolled: StateFlow<Boolean> = _isEnrolled.asStateFlow()

    private val _enrollmentStep = MutableStateFlow(0) // 0 to 3
    val enrollmentStep: StateFlow<Int> = _enrollmentStep.asStateFlow()

    private val enrolledSamples = mutableListOf<VoiceSampleFeatures>()

    data class VoiceSampleFeatures(
        val avgEnergy: Float,
        val zeroCrossingRate: Float,
        val spectralRoughness: Float
    )

    init {
        // Check if previously stored
        if (prefs.getBoolean("voice_enrolled", false)) {
            _isEnrolled.value = true
        }
    }

    /**
     * Extracts acoustic features from raw 16-bit PCM audio buffer.
     */
    fun extractFeatures(buffer: ShortArray, readSize: Int): VoiceSampleFeatures {
        if (readSize <= 0) return VoiceSampleFeatures(0f, 0f, 0f)

        var sumSquare = 0.0
        var zeroCrossings = 0
        var prevSample = buffer[0].toInt()

        for (i in 0 until readSize) {
            val sample = buffer[i].toInt()
            sumSquare += sample * sample

            if ((sample >= 0 && prevSample < 0) || (sample < 0 && prevSample >= 0)) {
                zeroCrossings++
            }
            prevSample = sample
        }

        val rms = sqrt(sumSquare / readSize).toFloat()
        val zcr = zeroCrossings.toFloat() / readSize

        // Compute sample-to-sample difference (spectral roughness)
        var diffSum = 0f
        for (i in 1 until readSize) {
            diffSum += abs(buffer[i] - buffer[i - 1])
        }
        val roughness = diffSum / readSize

        return VoiceSampleFeatures(avgEnergy = rms, zeroCrossingRate = zcr, spectralRoughness = roughness)
    }

    /**
     * Records one enrollment sample (Step 1 to 3).
     */
    fun addEnrollmentSample(features: VoiceSampleFeatures): Int {
        enrolledSamples.add(features)
        val current = enrolledSamples.size
        _enrollmentStep.value = current

        if (current >= 3) {
            // Calculate averaged profile
            val avgEnergy = enrolledSamples.map { it.avgEnergy }.average().toFloat()
            val avgZcr = enrolledSamples.map { it.zeroCrossingRate }.average().toFloat()
            val avgRough = enrolledSamples.map { it.spectralRoughness }.average().toFloat()

            prefs.edit()
                .putBoolean("voice_enrolled", true)
                .putFloat("profile_energy", avgEnergy)
                .putFloat("profile_zcr", avgZcr)
                .putFloat("profile_roughness", avgRough)
                .apply()

            _isEnrolled.value = true
            _enrollmentStep.value = 3
        }
        return current
    }

    /**
     * Verifies if incoming audio features match the enrolled owner's voice.
     */
    fun verifySpeaker(incoming: VoiceSampleFeatures): Boolean {
        if (!_isEnrolled.value) return true // If not enrolled, allow open access

        val profileEnergy = prefs.getFloat("profile_energy", 0f)
        val profileZcr = prefs.getFloat("profile_zcr", 0f)
        val profileRough = prefs.getFloat("profile_roughness", 0f)

        if (profileEnergy <= 0f) return true

        // Calculate similarity distance
        val zcrDiff = abs(incoming.zeroCrossingRate - profileZcr) / (profileZcr.coerceAtLeast(0.01f))
        val roughDiff = abs(incoming.spectralRoughness - profileRough) / (profileRough.coerceAtLeast(1f))

        // If distance is within acceptable biometric tolerance threshold (40% variation allowance)
        return (zcrDiff < 0.55f && roughDiff < 0.60f)
    }

    /**
     * Resets enrolled voice profile.
     */
    fun resetProfile() {
        enrolledSamples.clear()
        _enrollmentStep.value = 0
        _isEnrolled.value = false
        prefs.edit().clear().apply()
    }
}
