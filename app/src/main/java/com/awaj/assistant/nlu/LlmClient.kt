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
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(18, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    fun updateApiKey(key: String) {
        this.apiKey = key
    }

    fun hasApiKey(): Boolean = apiKey.isNotBlank()

    /**
     * Answers any general knowledge, science, literature, daily query or conversational prompt in natural Bengali.
     */
    suspend fun askAiQuestion(banglaQuery: String): String? = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext null

        val systemPrompt = """
            তুমি Awaj (আওয়াজ) নামের একটি অত্যন্ত বুদ্ধিমান, অমায়িক ও সহায়ক বাংলা ভয়েস অ্যাসিস্ট্যান্ট।
            ব্যবহারকারী তোমাকে বাংলায় যেকোনো প্রশ্ন (সাধারণ জ্ঞান, বিজ্ঞান, ইতিহাস, গণিত, প্রযুক্তি, দৈনন্দিন পরামর্শ, গল্প বা সাধারণ আড্ডা) করতে পারে।
            
            তোমার দায়িত্ব:
            ১. সহজ, স্পষ্ট ও মিষ্টি বাংলায় প্রাসঙ্গিক ও সঠিক উত্তর দেওয়া।
            ২. উত্তরটি ২ থেকে ৪টি বাক্যের মধ্যে সংক্ষিপ্ত ও প্রাঞ্জল রাখবে যেন স্পিচ/ভয়েসে পড়ে শোনাতে চমৎকার লাগে।
            ৩. কোনো অপ্রয়োজনীয় ভূমিকা বা অবান্তর টেক্সট দেবে না, সরাসরি উত্তর দেবে।
        """.trimIndent()

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

        val requestJson = JsonObject().apply {
            val contents = com.google.gson.JsonArray()
            val contentObj = JsonObject()
            val parts = com.google.gson.JsonArray()

            val part1 = JsonObject()
            part1.addProperty("text", "$systemPrompt\n\nব্যবহারকারীর প্রশ্ন: $banglaQuery\nতোমার সংক্ষিপ্ত উত্তর:")
            parts.add(part1)

            contentObj.add("parts", parts)
            contents.add(contentObj)
            add("contents", contents)
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

            return@withContext textOutput.trim()
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    /**
     * Parses complex multi-intent or tool calling requests.
     */
    suspend fun parseComplexCommand(banglaQuery: String): ActionRequest? = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext null
        }

        val systemPrompt = """
            তুমি Awaj নামের একটি বাংলা ভয়েস অ্যাসিস্ট্যান্টের টুল প্ল্যানার ও এআই ইঞ্জিন।
            ব্যবহারকারীর কথা শুনে সিদ্ধান্ত নাও এটি ডিভাইসের কোনো কাজ নাকি কোনো সাধারণ জ্ঞান/তথ্যভিত্তিক প্রশ্ন।
            
            যদি কোনো প্রশ্ন বা সাধারণ কথোপকথন হয় (যেমন: "বাংলাদেশের রাজধানী কি", "কেমন আছো", "চাঁদের বয়স কত"):
            - action হবে "ai_chat"
            - params এ "answer" ফিল্ডে সংক্ষেপে (২-৩ বাক্য) বাংলায় সঠিক উত্তর দেবে।
            
            যদি ডিভাইসের কাজ হয়, তবে নিচের অনুমোদিত টুল নির্বাচন করো:
            1. open_app (params: app_query)
            2. make_call (params: contact) [risk: HIGH, confirmation: true]
            3. send_sms (params: contact, message) [risk: HIGH, confirmation: true]
            4. send_whatsapp (params: contact, message) [risk: HIGH, confirmation: true]
            5. set_alarm (params: hour (0-23), minute (0-59), is_tomorrow: boolean)
            6. set_timer (params: seconds: int, label: string)
            7. toggle_torch (params: state: "on"|"off"|"toggle")
            8. set_volume (params: direction: "up"|"down"|"mute"|"set", level: int)
            9. set_brightness (params: direction: "up"|"down"|"set", level: int)
            10. toggle_hotspot (params: state: "on"|"off"|"open")
            11. control_connectivity (params: target: "wifi"|"bluetooth"|"airplane", state: "on"|"off"|"toggle")
            12. play_music_query (params: query: string, platform: "youtube"|"spotify"|"default")
            13. calculate_math (params: num1: double, num2: double, operation: "add"|"subtract"|"multiply"|"divide")
            14. open_camera_mode (params: mode: "photo"|"selfie"|"video")
            15. add_calendar_event (params: title: string)
            16. web_search (params: query: string)
            17. get_weather (params: query: string)
            18. get_device_info (params: query: string)
            19. run_routine (params: routine_id: string, name: string)
            20. stop_all
            
            নিরাপত্তা সতর্কতা:
            - টাকা পাঠানো (bKash/Nagad/Bank/UPI), OTP, Password, পেমেন্ট সংক্রান্ত কোনো অটোমেশন করবে না। সেক্ষেত্রে action: "blocked" দিবে।
            
            আউটপুট শুধু বৈধ JSON ফরম্যাটে দাও:
            {
              "action": "string",
              "params": { ... },
              "risk": "LOW" | "MEDIUM" | "HIGH" | "BLOCKED",
              "confirmation_required": boolean,
              "summary_bangla": "বাংলায় কাজের বা উত্তরের বিবরণ"
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
            val action = parsedJson.get("action")?.asString ?: "ai_chat"
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
                        else if (prim.isNumber) paramsMap[entry.key] = prim.asDouble
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
