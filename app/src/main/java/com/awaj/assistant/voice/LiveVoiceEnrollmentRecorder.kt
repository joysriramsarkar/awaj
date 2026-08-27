package com.awaj.assistant.voice

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.sqrt

class LiveVoiceEnrollmentRecorder(
    private val context: Context,
    private val voiceProfileManager: VoiceProfileManager
) {
    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val recordDurationMs = 2400L // 2.4 seconds per sample

    @SuppressLint("MissingPermission")
    suspend fun recordEnrollmentSample(
        onProgress: (progress: Float, volumeDb: Float) -> Unit
    ): Result<Int> = withContext(Dispatchers.IO) {
        val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat).coerceAtLeast(3200)
        val audioRecord = try {
            val rec = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                minBufferSize * 2
            )
            if (rec.state == AudioRecord.STATE_INITIALIZED) rec else {
                rec.release()
                AudioRecord(
                    MediaRecorder.AudioSource.VOICE_RECOGNITION,
                    sampleRate,
                    channelConfig,
                    audioFormat,
                    minBufferSize * 2
                )
            }
        } catch (e: Exception) {
            return@withContext Result.failure(Exception("মাইক্রোফোন চালু করতে সমস্যা হয়েছে: ${e.localizedMessage}"))
        }

        if (audioRecord.state != AudioRecord.STATE_INITIALIZED) {
            audioRecord.release()
            return@withContext Result.failure(Exception("মাইক্রোফোন ইনিশিয়ালাইজ করা সম্ভব হয়নি। পারমিশন চেক করুন।"))
        }

        val totalSamples = (sampleRate * (recordDurationMs / 1000.0)).toInt()
        val accumulatedPcm = ShortArray(totalSamples)
        var totalRead = 0

        try {
            audioRecord.startRecording()
            val chunkBuffer = ShortArray(minBufferSize)
            val startTime = System.currentTimeMillis()

            var sumSquareAll = 0.0

            while (totalRead < totalSamples && (System.currentTimeMillis() - startTime) < (recordDurationMs + 500)) {
                val read = audioRecord.read(chunkBuffer, 0, minBufferSize.coerceAtMost(totalSamples - totalRead))
                if (read > 0) {
                    System.arraycopy(chunkBuffer, 0, accumulatedPcm, totalRead, read)
                    totalRead += read

                    var chunkSum = 0.0
                    for (i in 0 until read) {
                        val s = chunkBuffer[i].toInt()
                        chunkSum += s * s
                    }
                    sumSquareAll += chunkSum
                    val chunkRms = sqrt(chunkSum / read)
                    val db = if (chunkRms > 1.0) (20 * Math.log10(chunkRms)).toFloat() else 0f

                    val progress = (totalRead.toFloat() / totalSamples.toFloat()).coerceIn(0f, 1f)
                    withContext(Dispatchers.Main) {
                        onProgress(progress, db)
                    }
                }
                delay(40)
            }

            audioRecord.stop()

            if (totalRead <= 1600) {
                return@withContext Result.failure(Exception("পর্যাপ্ত অডিও রেকর্ড করা যায়নি।"))
            }

            val overallRms = sqrt(sumSquareAll / totalRead)
            if (overallRms < 120.0) {
                return@withContext Result.failure(Exception("কণ্ঠস্বর খুব মৃদু বা নীরব ছিল। মাইক্রোফোনের কাছে এসে স্পষ্ট কণ্ঠে 'হেই আওয়াজ' বলুন।"))
            }

            val currentStep = voiceProfileManager.addEnrollmentSample(accumulatedPcm, totalRead)
            return@withContext Result.success(currentStep)
        } catch (e: Exception) {
            return@withContext Result.failure(e)
        } finally {
            try {
                audioRecord.release()
            } catch (e: Exception) {
                // Ignore cleanup error
            }
        }
    }
}
