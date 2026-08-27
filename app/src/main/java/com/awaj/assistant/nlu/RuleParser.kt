package com.awaj.assistant.nlu

import java.util.Locale

class RuleParser {

    fun parse(rawInput: String): ActionRequest? {
        if (rawInput.isBlank()) return null

        val cleaned = IntentNormalizer.cleanBengaliPunctuation(rawInput)
        val text = IntentNormalizer.removeFillers(cleaned).lowercase(Locale.getDefault())
        val digitsConverted = IntentNormalizer.convertBanglaDigitsToEnglish(text)

        // 1. Emergency Stop / Cancel (Only standalone stop commands)
        if (isStopCommand(text)) {
            return ActionRequest(
                action = "stop_all",
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = "সব চলমান কাজ বাতিল করা হলো"
            )
        }

        // 1.1 Action Undo ("ভুল হয়ে গেছে, বাতিল করো", "আগেরটা ফেরত নাও", "আনডু")
        if (text.contains("ভুল হয়ে গেছে") || text.contains("আগেরটা বাতিল") || text.contains("আগেরটা ফেরত") || text.contains("আনডু") || text.contains("undo")) {
            return ActionRequest(
                action = "undo_action",
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = "পূর্ববর্তী কাজ বাতিল করার চেষ্টা করা হচ্ছে"
            )
        }

        // 1.2 Official Carrier USSD Balance Dial Shortcut
        if (text.contains("ব্যালেন্স") || text.contains("টাকা কত আছে") || text.contains("balance check")) {
            val ussdCode = when {
                text.contains("গ্রামীণ") || text.contains("gp") || text.contains("grameen") -> "*566#"
                text.contains("বাংলালিংক") || text.contains("banglalink") || text.contains("bl") -> "*124#"
                text.contains("রবি") || text.contains("robi") || text.contains("এয়ারটেল") || text.contains("airtel") -> "*778#"
                text.contains("টেলিটক") || text.contains("teletalk") -> "*152#"
                text.contains("জিও") || text.contains("jio") -> "*333#"
                else -> "*121#"
            }
            return ActionRequest(
                action = "make_call",
                params = mapOf("contact" to "অফিসিয়াল ব্যালেন্স USSD ($ussdCode)", "number" to ussdCode),
                risk = RiskLevel.HIGH,
                confirmationRequired = true,
                rawQuery = rawInput,
                summaryBangla = "মোবাইল ব্যালেন্স চেক করতে $ussdCode ডায়াল করা হবে"
            )
        }

        // 2. Hotspot & Tethering
        if (text.contains("হটস্পট") || text.contains("hotspot") || text.contains("টেথারিং")) {
            val state = when {
                text.contains("চালু") || text.contains("অন") || text.contains("on") -> "on"
                text.contains("বন্ধ") || text.contains("অফ") || text.contains("off") -> "off"
                else -> "open"
            }
            return ActionRequest(
                action = "toggle_hotspot",
                params = mapOf("state" to state),
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = if (state == "on") "হটস্পট চালু করা হচ্ছে" else if (state == "off") "হটস্পট বন্ধ করা হচ্ছে" else "হটস্পট সেটিংস খোলা হচ্ছে"
            )
        }

        // 3. Connectivity (WiFi, Bluetooth, Airplane mode)
        if (text.contains("ওয়াইফাই") || text.contains("ওয়াইফাই") || text.contains("wifi")) {
            val state = if (text.contains("বন্ধ") || text.contains("অফ") || text.contains("off")) "off" else "on"
            return ActionRequest(
                action = "control_connectivity",
                params = mapOf("target" to "wifi", "state" to state),
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = "ওয়াইফাই সেটিংস খোলা হচ্ছে"
            )
        }
        if (text.contains("ব্লুটুথ") || text.contains("bluetooth")) {
            val state = if (text.contains("বন্ধ") || text.contains("অফ") || text.contains("off")) "off" else "on"
            return ActionRequest(
                action = "control_connectivity",
                params = mapOf("target" to "bluetooth", "state" to state),
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = "ব্লুটুথ সেটিংস খোলা হচ্ছে"
            )
        }
        if (text.contains("ফ্লাইট মোড") || text.contains("এয়ারপ্লেন মোড") || text.contains("airplane mode")) {
            return ActionRequest(
                action = "control_connectivity",
                params = mapOf("target" to "airplane", "state" to "toggle"),
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = "ফ্লাইট মোড সেটিংস খোলা হচ্ছে"
            )
        }

        // 4. Camera, Selfie & Video
        if (text.contains("সেলফি") || text.contains("selfie") || text.contains("সামনের ক্যামেরা")) {
            return ActionRequest(
                action = "open_camera_mode",
                params = mapOf("mode" to "selfie"),
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = "সেলফি ক্যামেরা খোলা হচ্ছে"
            )
        }
        if (text.contains("ভিডিও রেকর্ড") || text.contains("ভিডিও তোলো") || text.contains("ভিডিও করো") || text.contains("record video")) {
            return ActionRequest(
                action = "open_camera_mode",
                params = mapOf("mode" to "video"),
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = "ভিডিও রেকর্ডিং মোড খোলা হচ্ছে"
            )
        }

        // 5. Arithmetic / Math Calculator (e.g. "১০ গুণ ২০ কত", "১০০ যোগ ৫০")
        val mathMatch = Regex("(\\d+)\\s*(যোগ|বিয়োগ|বিয়োগ|গুণ|গুন|ভাগ|\\+|\\-|\\*|\\/)\\s*(\\d+)").find(digitsConverted)
        if (mathMatch != null) {
            val num1 = mathMatch.groupValues[1].toDoubleOrNull() ?: 0.0
            val opBangla = mathMatch.groupValues[2]
            val num2 = mathMatch.groupValues[3].toDoubleOrNull() ?: 0.0

            val op = when {
                opBangla.contains("যোগ") || opBangla == "+" -> "add"
                opBangla.contains("বিয়োগ") || opBangla.contains("বিয়োগ") || opBangla == "-" -> "subtract"
                opBangla.contains("গুণ") || opBangla.contains("গুন") || opBangla == "*" -> "multiply"
                opBangla.contains("ভাগ") || opBangla == "/" -> "divide"
                else -> "add"
            }

            return ActionRequest(
                action = "calculate_math",
                params = mapOf("num1" to num1, "num2" to num2, "operation" to op),
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = "গাণিতিক হিসাব করা হচ্ছে"
            )
        }

        // 6. Music Playback Queries (e.g. "ইউটিউবে রবীন্দ্রসঙ্গীত চালাও", "স্পটিফাইতে গান লাগাও", "গান বাজাও")
        if (text.contains("গান চালাও") || text.contains("গান বাজাও") || text.contains("গান লাগাও") ||
            (text.contains("গান") && (text.contains("ইউটিউব") || text.contains("স্পটিফাই") || text.contains("চালাও") || text.contains("শোনাও")))
        ) {
            val platform = when {
                text.contains("ইউটিউব") -> "youtube"
                text.contains("স্পটিফাই") -> "spotify"
                else -> "default"
            }
            val songQuery = text.replace("ইউটিউবে", "").replace("স্পটিফাইতে", "").replace("চালাও", "").replace("বাজাও", "").replace("লাগাও", "").replace("শোনাও", "").trim()
            return ActionRequest(
                action = "play_music_query",
                params = mapOf("query" to songQuery.ifBlank { "Bangla Music" }, "platform" to platform),
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = "\"$songQuery\" গান বাজানো হচ্ছে"
            )
        }

        // 7. Predefined Routines (সুপ্রভাত / শুভ রাত্রি)
        if (text.contains("সুপ্রভাত") || text.contains("শুভ সকাল") || text.contains("good morning")) {
            return ActionRequest(
                action = "run_routine",
                params = mapOf("routine_id" to "morning_routine", "name" to "সুপ্রভাত রুটিন"),
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = "সকালের রুটিন চালানো হচ্ছে"
            )
        }
        if (text.contains("শুভ রাত্রি") || text.contains("শুভরাত্রি") || text.contains("good night") || text.contains("ঘুমের মোড")) {
            return ActionRequest(
                action = "run_routine",
                params = mapOf("routine_id" to "night_routine", "name" to "রাত্রিকালীন রুটিন"),
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = "রাতের রুটিন সক্রিয় করা হচ্ছে"
            )
        }

        // 8. Torch / Flashlight
        if (text.contains("টর্চ") || text.contains("ফ্ল্যাশলাইট") || text.contains("flashlight") || text.contains("torch")) {
            val stateOff = text.contains("নেভাও") || text.contains("অফ") || text.contains("বন্ধ") || text.contains("off")
            val stateOn = text.contains("জ্বালাও") || text.contains("অন") || text.contains("চালু") || text.contains("জ্বালো") || text.contains("on")
            val targetState = if (stateOff) "off" else if (stateOn) "on" else "toggle"

            return ActionRequest(
                action = "toggle_torch",
                params = mapOf("state" to targetState),
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = if (targetState == "on") "টর্চ জ্বালানো হচ্ছে" else if (targetState == "off") "টর্চ বন্ধ করা হচ্ছে" else "টর্চ পরিবর্তন করা হচ্ছে"
            )
        }

        // 9. Volume Control
        if (text.contains("ভলিউম") || text.contains("শব্দ") || text.contains("sound") || text.contains("volume")) {
            val isUp = text.contains("বাড়া") || text.contains("বাড়াও") || text.contains("increase") || text.contains("up")
            val isDown = text.contains("কমা") || text.contains("কমাও") || text.contains("decrease") || text.contains("down")
            val isMute = text.contains("মিউট") || text.contains("নীরব") || text.contains("mute")

            val levelMatch = Regex("(\\d{1,3})\\s*%?").find(digitsConverted)
            val specificLevel = levelMatch?.groupValues?.get(1)?.toIntOrNull()

            val direction = if (isMute) "mute" else if (isUp) "up" else if (isDown) "down" else if (specificLevel != null) "set" else "up"

            return ActionRequest(
                action = "set_volume",
                params = mapOf(
                    "direction" to direction,
                    "level" to (specificLevel ?: -1)
                ),
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = if (direction == "mute") "ভলিউম মিউট করা হচ্ছে" else if (direction == "up") "ভলিউম বাড়ানো হচ্ছে" else if (direction == "down") "ভলিউম কমানো হচ্ছে" else "ভলিউম $specificLevel% নির্ধারণ করা হচ্ছে"
            )
        }

        // 10. Brightness Control
        if (text.contains("ব্রাইটনেস") || text.contains("উজ্জ্বলতা") || text.contains("brightness")) {
            val isUp = text.contains("বাড়া") || text.contains("বাড়াও") || text.contains("up")
            val isDown = text.contains("কমা") || text.contains("কমাও") || text.contains("down")
            val levelMatch = Regex("(\\d{1,3})\\s*%?").find(digitsConverted)
            val level = levelMatch?.groupValues?.get(1)?.toIntOrNull()

            val direction = if (isUp) "up" else if (isDown) "down" else if (level != null) "set" else "down"

            return ActionRequest(
                action = "set_brightness",
                params = mapOf("direction" to direction, "level" to (level ?: -1)),
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = if (direction == "up") "ব্রাইটনেস বাড়ানো হচ্ছে" else if (direction == "down") "ব্রাইটনেস কমানো হচ্ছে" else "ব্রাইটনেস $level% নির্ধারণ করা হচ্ছে"
            )
        }

        // 11. Alarms
        if (text.contains("অ্যালার্ম") || text.contains("এলার্ম") || text.contains("alarm")) {
            val parsedTime = IntentNormalizer.extractTime(text)
            val hour = parsedTime?.first ?: 7
            val minute = parsedTime?.second ?: 0
            val isTomorrow = text.contains("কাল") || text.contains("আগামীকাল") || text.contains("tomorrow")

            val timeFormatted = String.format(Locale.US, "%02d:%02d", hour, minute)
            val timeBangla = IntentNormalizer.convertEnglishDigitsToBangla(timeFormatted)

            return ActionRequest(
                action = "set_alarm",
                params = mapOf(
                    "hour" to hour,
                    "minute" to minute,
                    "is_tomorrow" to isTomorrow,
                    "time_str" to timeFormatted
                ),
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = "$timeBangla টায় অ্যালার্ম সেট করা হচ্ছে"
            )
        }

        // 12. Timers
        if (text.contains("টাইমার") || text.contains("timer")) {
            val durationSeconds = IntentNormalizer.extractDurationSeconds(text) ?: 300
            val durationMinutes = durationSeconds / 60
            val durationBangla = IntentNormalizer.convertEnglishDigitsToBangla(durationMinutes.toString())

            return ActionRequest(
                action = "set_timer",
                params = mapOf(
                    "seconds" to durationSeconds,
                    "label" to "Awaj Assistant Timer"
                ),
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = "$durationBangla মিনিটের টাইমার সেট করা হচ্ছে"
            )
        }

        // 13. Device Info & Battery
        if (text.contains("ব্যাটারি") || text.contains("চার্জ") || text.contains("battery") || text.contains("চার্জ কত") || text.contains("ফোনের অবস্থা")) {
            return ActionRequest(
                action = "get_device_info",
                params = mapOf("query" to "battery"),
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = "ব্যাটারি ও ডিভাইসের তথ্য দেখা হচ্ছে"
            )
        }

        // 14. Weather
        if (text.contains("আবহাওয়া") || text.contains("আবহাওয়া") || text.contains("বৃষ্টি") || text.contains("weather") || text.contains("তাপমাত্রা")) {
            return ActionRequest(
                action = "get_weather",
                params = mapOf("query" to text),
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = "আবহাওয়ার তথ্য যাচাই করা হচ্ছে"
            )
        }

        // 15. WhatsApp Message (High Risk -> Confirmation required)
        if (text.contains("হোয়াটসঅ্যাপ") || text.contains("হোয়াটসঅ্যাপ") || text.contains("ওয়াটসঅ্যাপ") || text.contains("whatsapp")) {
            if (text.contains("মেসেজ") || text.contains("বলো") || text.contains("বল") || text.contains("লিখো") || text.contains("পাঠাও")) {
                val (contact, message) = extractContactAndMessage(rawInput, listOf("হোয়াটসঅ্যাপে", "হোয়াটসঅ্যাপে", "whatsapp এ", "whatsapp-এ", "মেসেজ", "বলো", "বল"))
                return ActionRequest(
                    action = "send_whatsapp",
                    params = mapOf(
                        "contact" to contact,
                        "message" to message
                    ),
                    risk = RiskLevel.HIGH,
                    confirmationRequired = true,
                    rawQuery = rawInput,
                    summaryBangla = "হোয়াটসঅ্যাপে $contact-কে মেসেজ: \"$message\" পাঠানো হবে"
                )
            }
        }

        // 16. Phone Call (High Risk -> Confirmation required)
        if (text.contains("কল") || text.contains("ফোন") || text.contains("call") || text.contains("dial")) {
            val contact = extractContactForCall(text)
            return ActionRequest(
                action = "make_call",
                params = mapOf("contact" to contact),
                risk = RiskLevel.HIGH,
                confirmationRequired = true,
                rawQuery = rawInput,
                summaryBangla = "$contact-কে ফোন কল করা হবে"
            )
        }

        // 17. SMS (High Risk -> Confirmation required)
        if (text.contains("এসএমএস") || text.contains("sms") || (text.contains("মেসেজ") && !text.contains("হোয়াটসঅ্যাপ") && !text.contains("হোয়াটসঅ্যাপ"))) {
            val (contact, message) = extractContactAndMessage(rawInput, listOf("এসএমএস", "sms", "মেসেজ", "পাঠাও", "দাও"))
            return ActionRequest(
                action = "send_sms",
                params = mapOf(
                    "contact" to contact,
                    "message" to message
                ),
                risk = RiskLevel.HIGH,
                confirmationRequired = true,
                rawQuery = rawInput,
                summaryBangla = "$contact-কে SMS পাঠানো হবে: \"$message\""
            )
        }

        // 18. Notifications Reader
        if (text.contains("নোটিফিকেশন") || text.contains("notification") || text.contains("মেসেজ কি আসছে") || text.contains("মেসেজ পড়ে শোনাও")) {
            return ActionRequest(
                action = "read_notifications",
                params = emptyMap(),
                risk = RiskLevel.MEDIUM,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = "সাম্প্রতিক নোটিফিকেশন পড়া হচ্ছে"
            )
        }

        // 19. Calendar Event
        if (text.contains("ক্যালেন্ডার") || text.contains("ইভেন্ট") || text.contains("meeting") || text.contains("মিটিং")) {
            val title = extractTitleAfterKeywords(rawInput, listOf("ক্যালেন্ডারে", "ইভেন্ট", "মিটিং"))
            return ActionRequest(
                action = "add_calendar_event",
                params = mapOf("title" to title.ifEmpty { "নতুন ইভেন্ট" }),
                risk = RiskLevel.MEDIUM,
                confirmationRequired = true,
                rawQuery = rawInput,
                summaryBangla = "ক্যালেন্ডারে \"$title\" ইভেন্ট যোগ করা হবে"
            )
        }

        // 20. Settings
        if (text.contains("সেটিংস") || text.contains("সেটিং") || text.contains("settings")) {
            val subSetting = when {
                text.contains("ওয়াইফাই") || text.contains("wifi") -> "wifi"
                text.contains("ব্লুটুথ") || text.contains("bluetooth") -> "bluetooth"
                text.contains("ডিসপ্লে") || text.contains("display") -> "display"
                text.contains("সাউন্ড") || text.contains("sound") -> "sound"
                else -> "general"
            }
            return ActionRequest(
                action = "open_settings",
                params = mapOf("setting" to subSetting),
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = "$subSetting সেটিংস খোলা হচ্ছে"
            )
        }

        // 21. Open App (Generic with explicit launch words)
        if (text.contains("খোলো") || text.contains("খোল") || text.contains("ওপেন") || text.contains("অন করো") || text.contains("open") || text.contains("launch")) {
            val appQuery = extractAppName(text)
            val resolvedApp = IntentNormalizer.resolveKnownApp(appQuery)
            return ActionRequest(
                action = "open_app",
                params = mapOf("app_query" to resolvedApp, "raw_name" to appQuery),
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = "$appQuery অ্যাপ খোলা হচ্ছে"
            )
        }

        // 22. If user says simple app name directly e.g. "গুগল পে", "ফোনপে", "স্পটিফাই", "ইউটিউব"
        val directApp = IntentNormalizer.resolveKnownApp(text)
        if (directApp != text && directApp.isNotBlank()) {
            return ActionRequest(
                action = "open_app",
                params = mapOf("app_query" to directApp, "raw_name" to text),
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = "$text অ্যাপ খোলা হচ্ছে"
            )
        }

        // 23. Web Search
        if (text.contains("সার্চ") || text.contains("খুঁজো") || text.contains("খোঁজ") || text.contains("search") || text.contains("গুগল") || text.contains("খবর") || text.contains("news")) {
            val query = extractTitleAfterKeywords(rawInput, listOf("সার্চ করো", "সার্চ কর", "খুঁজো", "search for", "গুগল", "খবর"))
            return ActionRequest(
                action = "web_search",
                params = mapOf("query" to query.ifEmpty { rawInput }),
                risk = RiskLevel.LOW,
                confirmationRequired = false,
                rawQuery = rawInput,
                summaryBangla = "\"$query\" লিখে ওয়েব সার্চ করা হচ্ছে"
            )
        }

        // Fallback to web search
        return ActionRequest(
            action = "web_search",
            params = mapOf("query" to rawInput),
            risk = RiskLevel.LOW,
            confirmationRequired = false,
            rawQuery = rawInput,
            summaryBangla = "\"$rawInput\" অনুসন্ধান করা হচ্ছে"
        )
    }

    private fun isStopCommand(text: String): Boolean {
        val standaloneStopWords = listOf("থামো", "বাতিল", "স্টপ", "stop", "cancel", "halt")
        if (standaloneStopWords.any { text == it }) return true

        if ((text == "বন্ধ করো" || text == "বন্ধ কর" || text == "সব বন্ধ করো") &&
            !text.contains("টর্চ") && !text.contains("গান") && !text.contains("অ্যাপ") && !text.contains("হটস্পট") && !text.contains("ওয়াইফাই")
        ) {
            return true
        }

        return false
    }

    private fun extractContactForCall(text: String): String {
        var contact = text
        val removeKeywords = listOf("কে", "রে", "কল", "করো", "কর", "দে", "দাও", "ফোন", "দিয়ে", "দিয়ে", "call", "dial", "to")
        for (kw in removeKeywords) {
            contact = contact.replace(Regex("\\b$kw\\b", RegexOption.IGNORE_CASE), "")
        }
        contact = contact.replace("আম্মু", "আম্মু").replace("মা", "মা").trim()
        return if (contact.isNotBlank()) contact else "আম্মু"
    }

    private fun extractContactAndMessage(raw: String, keywords: List<String>): Pair<String, String> {
        var contact = "আম্মু"
        var message = "আমি রওনা হয়েছি"

        if (raw.contains(":")) {
            val parts = raw.split(":", limit = 2)
            contact = extractContactForCall(parts[0])
            message = parts[1].trim()
            return Pair(contact, message)
        }

        val tellMatch = Regex("(বলো|বল|যে)\\s+(.+)").find(raw)
        if (tellMatch != null) {
            message = tellMatch.groupValues[2].trim()
            val beforePart = raw.substring(0, tellMatch.range.first)
            contact = extractContactForCall(beforePart)
            return Pair(contact, message)
        }

        return Pair(contact, message)
    }

    private fun extractTitleAfterKeywords(text: String, keywords: List<String>): String {
        var result = text
        for (kw in keywords) {
            val idx = result.indexOf(kw)
            if (idx != -1) {
                result = result.substring(idx + kw.length).trim()
                break
            }
        }
        return result.trim()
    }

    private fun extractAppName(text: String): String {
        var clean = text
        val openKeywords = listOf("ওপেন করো", "ওপেন কর", "চালু করো", "অন করো", "খোলো", "খোল", "open", "launch")
        for (kw in openKeywords) {
            clean = clean.replace(Regex("\\b$kw\\b|$kw"), "")
        }
        // Only strip standalone "অ্যাপ" or "app"
        clean = clean.replace(Regex("\\s+অ্যাপ\\b|\\bঅ্যাপ\\s+|\\s+app\\b|\\bapp\\s+"), "")
        return clean.trim()
    }
}
