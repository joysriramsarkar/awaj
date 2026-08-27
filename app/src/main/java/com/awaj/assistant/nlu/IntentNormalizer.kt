package com.awaj.assistant.nlu

import java.util.Locale

object IntentNormalizer {

    private val banglaToEnglishDigitMap = mapOf(
        '০' to '0', '১' to '1', '২' to '2', '৩' to '3', '৪' to '4',
        '৫' to '5', '৬' to '6', '৭' to '7', '৮' to '8', '৯' to '9'
    )

    private val englishToBanglaDigitMap = banglaToEnglishDigitMap.entries.associate { (k, v) -> v to k }

    private val banglaWordToNumberMap = listOf(
        "বারো" to 12, "এগারো" to 11, "দশ" to 10, "নয়" to 9, "নয়" to 9,
        "আট" to 8, "সাত" to 7, "ছয়" to 6, "ছয়" to 6, "পাঁচ" to 5,
        "চার" to 4, "তিন" to 3, "দুই" to 2, "এক" to 1
    )

    fun convertBanglaDigitsToEnglish(text: String): String {
        var result = text
        for ((word, num) in banglaWordToNumberMap) {
            result = result.replace(Regex("\\b$word\\b|(?<=\\s)$word(?=টায়|টায়|টা|\\s)"), num.toString())
        }

        val sb = StringBuilder()
        for (char in result) {
            sb.append(banglaToEnglishDigitMap[char] ?: char)
        }
        return sb.toString()
    }

    fun convertEnglishDigitsToBangla(text: String): String {
        val sb = StringBuilder()
        for (char in text) {
            sb.append(englishToBanglaDigitMap[char] ?: char)
        }
        return sb.toString()
    }

    fun cleanBengaliPunctuation(text: String): String {
        return text.replace(Regex("[।?!.,;:'\"\\-–—_()\\[\\]{}|/\\\\]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    fun removeFillers(text: String): String {
        val fillers = listOf("দয়া করে", "দয়া করে", "বলো তো", "বল তো", "একটু", "প্লিজ", "দেখি", "তো")
        var result = text
        for (filler in fillers) {
            result = result.replace(Regex("\\b$filler\\b", RegexOption.IGNORE_CASE), "")
        }
        return result.replace(Regex("\\s+"), " ").trim()
    }

    fun extractTime(text: String): Pair<Int, Int>? {
        val normalized = convertBanglaDigitsToEnglish(text).lowercase(Locale.getDefault())

        val timeRegex = Regex("(সকাল|দুপুর|বিকাল|সন্ধ্যা|রাত)?\\s*(\\d{1,2})(?::(\\d{1,2})|\\s*টা\\s*(\\d{1,2})?|\\s*টায়|\\s*টায়)?")
        val match = timeRegex.find(normalized) ?: return null

        val period = match.groups[1]?.value ?: ""
        val hourStr = match.groups[2]?.value ?: return null
        val minuteStr = match.groups[3]?.value ?: match.groups[4]?.value ?: "0"

        var hour = hourStr.toIntOrNull() ?: return null
        val minute = minuteStr.toIntOrNull() ?: 0

        if (period.contains("রাত") || period.contains("সন্ধ্যা") || period.contains("বিকাল")) {
            if (hour in 1..11) {
                hour += 12
            }
        } else if (period.contains("দুপুর")) {
            if (hour in 1..5) {
                hour += 12
            }
        } else if (period.contains("সকাল")) {
            if (hour == 12) {
                hour = 0
            }
        }

        return Pair(hour.coerceIn(0, 23), minute.coerceIn(0, 59))
    }

    fun extractDurationSeconds(text: String): Int? {
        val normalized = convertBanglaDigitsToEnglish(text)

        var totalSeconds = 0
        var found = false

        // Hours
        val hourMatch = Regex("(\\d+)\\s*(ঘণ্টা|ঘন্টা|hour)").find(normalized)
        if (hourMatch != null) {
            val hours = hourMatch.groupValues[1].toIntOrNull() ?: 0
            totalSeconds += hours * 3600
            found = true
        }

        // Minutes
        val minuteMatch = Regex("(\\d+)\\s*(মিনিট|মিনিটের|min|minute)").find(normalized)
        if (minuteMatch != null) {
            val minutes = minuteMatch.groupValues[1].toIntOrNull() ?: 0
            totalSeconds += minutes * 60
            found = true
        }

        // Seconds
        val secondMatch = Regex("(\\d+)\\s*(সেকেন্ড|সেকেন্ডের|sec|second)").find(normalized)
        if (secondMatch != null) {
            val seconds = secondMatch.groupValues[1].toIntOrNull() ?: 0
            totalSeconds += seconds
            found = true
        }

        return if (found && totalSeconds > 0) totalSeconds else null
    }

    fun resolveKnownApp(appNameQuery: String): String {
        val query = appNameQuery.lowercase(Locale.getDefault()).trim()
        return when {
            // Payment & UPI Apps
            query.contains("গুগল পে") || query.contains("জিপে") || query.contains("gpay") || query.contains("google pay") -> "gpay"
            query.contains("ফোনপে") || query.contains("ফোন পে") || query.contains("phonepe") -> "phonepe"
            query.contains("পেটিএম") || query.contains("paytm") -> "paytm"
            query.contains("ভীম") || query.contains("bhim") -> "bhim"
            query.contains("ক্রেডিটবি") || query.contains("kreditbee") -> "kreditbee"
            query.contains("বিকাশ") || query.contains("bkash") -> "bkash"
            query.contains("নগদ") || query.contains("nagad") -> "nagad"

            // Media & Streaming
            query.contains("স্পটিফাই") || query.contains("spotify") -> "spotify"
            query.contains("ভিএলসি") || query.contains("vlc") -> "vlc"
            query.contains("নেটফ্লিক্স") || query.contains("netflix") -> "netflix"
            query.contains("ইউটিউব") || query.contains("উটিউব") || query.contains("youtube") || query == "yt" -> "youtube"

            // Messaging & Social
            query.contains("হোয়াটসঅ্যাপ") || query.contains("হোয়াটসঅ্যাপ") || query.contains("ওয়াটসঅ্যাপ") || query.contains("whatsapp") || query == "wa" -> "whatsapp"
            query.contains("সিগন্যাল") || query.contains("signal") -> "signal"
            query.contains("ফেসবুক") || query.contains("facebook") || query == "fb" -> "facebook"
            query.contains("মেসেঞ্জার") || query.contains("messenger") -> "messenger"

            // Utilities & Tools
            query.contains("ব্লিনকিট") || query.contains("blinkit") -> "blinkit"
            query.contains("কনফার্ম টিকিট") || query.contains("confirmtkt") -> "confirmtkt"
            query.contains("বর্ণ") || query.contains("borno") -> "borno"
            query.contains("আইমু") || query.contains("imou") -> "imou"
            query.contains("জিও") || query.contains("jio") || query.contains("myjio") -> "jio"
            query.contains("ভিআই") || query.contains("ভোডাফোন") || query.contains("vi") -> "vi"
            query.contains("এয়ারটেল") || query.contains("airtel") -> "airtel"
            query.contains("ক্যামেরা") || query.contains("ছবি তোলার") || query.contains("camera") -> "camera"
            query.contains("ক্রোম") || query.contains("ব্রাউজার") || query.contains("chrome") -> "chrome"
            query.contains("ম্যাপ") || query.contains("ম্যাপস") || query.contains("maps") || query.contains("গুগল ম্যাপ") -> "maps"
            query.contains("জিমেইল") || query.contains("মেইল") || query.contains("gmail") || query.contains("email") -> "gmail"
            query.contains("গ্যালারি") || query.contains("ছবি") || query.contains("gallery") || query.contains("photos") -> "gallery"
            query.contains("ক্যালকুলেটর") || query.contains("calculator") -> "calculator"
            query.contains("সেটিংস") || query.contains("সেটিং") || query.contains("settings") -> "settings"
            query.contains("ক্যালেন্ডার") || query.contains("calendar") -> "calendar"
            query.contains("ফোন") || query.contains("ডায়ালার") || query.contains("phone") -> "phone"
            query.contains("প্লে স্টোর") || query.contains("প্লেস্টোর") || query.contains("playstore") -> "playstore"
            else -> query
        }
    }
}
