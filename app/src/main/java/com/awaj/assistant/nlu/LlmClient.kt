package com.awaj.assistant.nlu

import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class LlmClient(
    private var apiKey: String = ""
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    fun updateApiKey(key: String) {
        this.apiKey = key
    }

    suspend fun parseComplexCommand(banglaQuery: String): ActionRequest? = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext null
        }

        val systemPrompt = """
            তুমি Awaj নামের একটি বাংলা ভয়েস অ্যাসিস্ট্যান্টের টুল প্ল্যানার।
            ব্যবহারকারীর বাংলা বা বাংলা-ইংরেজি মিশ্রিত কথা শুনে উপযুক্ত টুল এবং প্যারামিটার বের করো।
            
            অনুমোদিত টুলসমূহ:
            1. open_app (params: app)
            2. make_call (params: contact) [risk: HIGH, confirmation: true]
            3. send_sms (params: contact, message) [risk: HIGH, confirmation: true]
            4. send_whatsapp (params: contact, message) [risk: HIGH, confirmation: true]
            5. set_alarm (params: hour (0-23), minute (0-59), is_tomorrow: boolean)
            6. set_timer (params: seconds: int, label: string)
            7. toggle_torch (params: state: "on"|"off"|"toggle")
            8. set_volume (params: direction: "up"|"down"|"mute"|"set", level: int)
            9. set_brightness (params: direction: "up"|"down"|"set", level: int)
            10. add_calendar_event (params: title: string)
            11. media_control (params: command: "play"|"pause"|"next"|"previous")
            12. web_search (params: query: string)
            13. get_weather (params: query: string)
            14. get_device_info (params: query: string)
            15. run_routine (params: routine_id: string, name: string)
            16. stop_all
            
            নিরাপত্তা সতর্কতা:
            - টাকা পাঠানো (bKash/Nagad/Bank), OTP, Password, পেমেন্ট সংক্রান্ত কোনো অটোমেশন করবে না। সেক্ষেত্রে action: "blocked" দিবে।
            
            আউটপুট শুধু বৈধ JSON ফরম্যাটে দাও:
            {
              "action": "string",
              "params": { ... },
              "risk": "LOW" | "MEDIUM" | "HIGH" | "BLOCKED",
              "confirmation_required": boolean,
              "summary_bangla": "বাংলায় এক লাইনে কাজের বিবরণ"
            }
        """.trimIndent()

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

        val requestJson = JsonObject().apply {
            val contents = com.google.gson.JsonArray()
            val contentObj = JsonObject()
            val parts = com.google.gson.JsonArray()
            
            val part1 = JsonObject()
            part1.addProperty("text", "$systemPrompt\n\nব্যবহারকারীর কথা: $banglaQuery")
            parts.add(part1)
            
            contentObj.add("parts", parts)
            contents.add(contentObj)
            add("contents", contents)

            val genConfig = JsonObject()
            genConfig.addProperty("response_mime_type", "application/json")
            add("generationConfig", genConfig)
        }

        try {
            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val responseBody = response.body?.string() ?: return@withContext null
            val jsonResponse = gson.fromJson(responseBody, JsonObject::class.java)

            val candidates = jsonResponse.getAsJsonArray("candidates")
            if (candidates == null || candidates.size() == 0) return@withContext null

            val candidate = candidates.get(0).asJsonObject
            val content = candidate.getAsJsonObject("content")
            val parsedParts = content.getAsJsonArray("parts")
            val textOutput = parsedParts.get(0).asJsonObject.get("text").asString

            val parsedJson = gson.fromJson(textOutput, JsonObject::class.java)
            val action = parsedJson.get("action")?.asString ?: "web_search"
            val riskStr = parsedJson.get("risk")?.asString ?: "LOW"
            val confirmation = parsedJson.get("confirmation_required")?.asBoolean ?: false
            val summary = parsedJson.get("summary_bangla")?.asString ?: banglaQuery

            val risk = when (riskStr.uppercase()) {
                "HIGH" -> RiskLevel.HIGH
                "MEDIUM" -> RiskLevel.MEDIUM
                "BLOCKED" -> RiskLevel.BLOCKED
                else -> RiskLevel.LOW
            }

            val paramsMap = mutableMapOf<String, Any>()
            val paramsObj = parsedJson.getAsJsonObject("params")
            if (paramsObj != null) {
                for (entry in paramsObj.entrySet()) {
                    if (entry.value.isJsonPrimitive) {
                        val prim = entry.value.asJsonPrimitive
                        if (prim.isBoolean) paramsMap[entry.key] = prim.asBoolean
                        else if (prim.isNumber) paramsMap[entry.key] = prim.asInt
                        else paramsMap[entry.key] = prim.asString
                    }
                }
            }

            return@withContext ActionRequest(
                action = action,
                params = paramsMap,
                risk = risk,
                confirmationRequired = confirmation,
                source = ActionSource.VOICE,
                rawQuery = banglaQuery,
                summaryBangla = summary
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}
