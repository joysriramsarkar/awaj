# Awaj (আওয়াজ) — বাংলা ভয়েস অ্যাসিস্ট্যান্ট ও অটোমেশন অ্যান্ড্রয়েড ইঞ্জিন

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="100" alt="Awaj Logo" />
</p>

<p align="center">
  <b>একটি বাংলা-প্রথম, ভয়েস-ফার্স্ট অ্যান্ড্রয়েড অ্যাসিস্ট্যান্ট ও ইন্টেলিজেন্ট অটোমেশন ইঞ্জিন</b><br>
  বাংলা ও বাংলিশ ভয়েস কমান্ডে ফোন নিয়ন্ত্রণ, ভয়েস অ্যাকোস্টিক ফিল্টারিং, প্লে-স্টোর সেফ মোড ও ল্যাব অটোমেশন।
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.0-blue.svg?logo=kotlin" />
  <img src="https://img.shields.io/badge/Jetpack_Compose-Material_3-purple.svg?logo=android" />
  <img src="https://img.shields.io/badge/Architecture-Clean%20%2F%20MVI-brightgreen.svg" />
  <img src="https://img.shields.io/badge/Safety-Gate%20Protected-orange.svg" />
  <img src="https://img.shields.io/badge/Tests-23%20Passing-brightgreen.svg" />
  <img src="https://img.shields.io/badge/License-MIT-green.svg" />
</p>

---

## 📖 প্রকল্প পরিচিতি (Overview)

**Awaj (আওয়াজ)** একটি আধুনিক, নিরাপদ ও সম্পূর্ণ বাংলা ভাষা-ভিত্তিক অ্যান্ড্রয়েড অ্যাসিস্ট্যান্ট। সাধারণ ভয়েস অ্যাসিস্ট্যান্টগুলোর সীমাবদ্ধতা অতিক্রম করে এটি বাংলা সংখ্যা, আঞ্চলিক উচ্চারণ, সময় ও দৈনন্দিন জটিল কাজ অত্যন্ত দ্রুতগতিতে অন-ডিভাইস স্তরে সম্পাদন করে।

এটি **৩টি সুনির্দিষ্ট অপারেটিং মোড** এবং একটি শক্তিশালী **নিরাপত্তা ও প্রাইভেসি গেট** দ্বারা পরিচালিত, যাতে ব্যবহারকারীর ব্যক্তিগত গোপনীয়তা ও আর্থিক নিরাপত্তা ১০০% অক্ষুণ্ণ থাকে।

---

## 🌟 প্রধান বৈশিষ্ট্যসমূহ (Key Features)

### ১. কণ্ঠস্বর সংবেদনশীলতা ও ওয়েক-ফিল্টার (Personalized Wake Sensitivity Filter)
- **৮-মাত্রিক অ্যাকোস্টিক এম্বেডিং:** ৩টি প্রাথমিক ভয়েস নমুনার সাহায্যে ব্যবহারকারীর পিচ, স্পেকট্রাল রফনেস, শক্তি ও ফর্ম্যান্ট ডাইনামিক্সের ওপর ভিত্তি করে একটি **8D Normalized Acoustic Vector** তৈরি করা হয়।
- **কোসাইন সিমিলারিটি যাচাই:** লক-স্ক্রিন বা ব্যাকগ্রাউন্ডে ওয়েক-ওয়ার্ড সনাক্তকরণে কোসাইন দূরত্বের মাধ্যমে অনাকাঙ্ক্ষিত শব্দ ফিল্টার করে শুধুমাত্র প্রকৃত ব্যবহারকারীর ডাকে সাড়া দেয়।

### ২. ৩টি সমন্বিত অপারেটিং মোড (3 Operating Modes)
1. **Safe Mode (প্লে-স্টোর সেফ):** অ্যান্ড্রয়েড স্ট্যান্ডার্ড Intent, Settings API, AlarmManager ও Media Controller ব্যবহার করে ১০০% পলিসি-সম্মত নিরাপদ কাজ পরিচালনা করে।
2. **Accessibility Mode (সহায়ক মোড):** দৃষ্টি ও শারীরিকভাবে বিশেষ চাহিদাসম্পন্ন ব্যবহারকারীদের জন্য স্ক্রিনের উপাদান পড়ে শোনানো ও ভয়েস নেভিগেশন সহায়তা।
3. **Lab Agent Mode (ল্যাব মোড):** পাওয়ার ইউজার ও অটোমেশন গবেষকদের জন্য স্বায়ত্তশাসিত GUI ReAct Agent লুপ (`AgentPlanner`, `AgentLoop`), যা স্ক্রিনের লাইভ চিন্তা (`thought`) ও পদক্ষেপ ইউআই-তে প্রদর্শন করে।

### ৩. পেমেন্ট ও ব্যাংকিং অ্যাপ সুরক্ষা গেট (Financial Safeguard)
- **সুরক্ষিত অ্যাপসমূহ:** Google Pay (GPay), PhonePe, Paytm, BHIM UPI, YONO SBI, HDFC, ICICI, Axis, Kotak, বিকাশ (`bKash`), নগদ (`Nagad`), রকেট, সেলফিন ইত্যাদি।
- **কঠোর নীতিমালা:**
  - ব্যবহারকারী বললে অ্যাপটি নিরাপদে ওপেন হবে (যেমন: *"গুগল পে খোলো"*), যাতে ব্যবহারকারী নিজের বায়োমেট্রিক বা ফিঙ্গারপ্রিন্ট দিয়ে নিজে লেনদেন করতে পারেন।
  - যেকোনো স্বয়ংক্রিয় টাকা পাঠানো, ইউপিআই পিন (UPI PIN), ওটিপি (OTP), পাসওয়ার্ড ইনপুট বা স্ক্রিন রিডিং **১০০% কঠোরভাবে ব্লকড**।

### ৪. অ্যাকশন Undo ও রিভার্সিবল কন্ট্রোল
- ব্যবহারকারী *"ভুল হয়ে গেছে, বাতিল করো"*, *"আগেরটা ফেরত নাও"*, বা *"আনডু"* বললে পূর্ববর্তী রিভার্সিবল অ্যাকশন (যেমন: টর্চ, ভলিউম ইত্যাদি) সাথে সাথে পূর্বের অবস্থায় ফিরিয়ে নেয়।

### ৫. অফলাইন রেজিলিয়েন্স ও ফলব্যাক ইঞ্জিন
- দুর্বল নেটওয়ার্ক বা ইন্টারনেট সংযোগ না থাকলেও অন-ডিভাইস `RuleParser` ব্যবহার করে টর্চ, অ্যালার্ম, মিউজিক, সেটিংস, ব্রাইটনেস ও ব্যালেন্স ডায়াল কমান্ড শতভাগ নিখুঁতভাবে নির্বাহ হয়।

### ৬. কুইক সেটিংস টাইল ও TalkBack অ্যাক্সেসিবিলিটি
- **Android Quick Settings Tile:** নোটিফিকেশন বার থেকে এক ট্যাপে সহকারী চালু করার সুবিধা (`AwajQuickTileService`)।
- **সম্পূর্ণ TalkBack সিম্যান্টিক্স:** দৃষ্টিহীন ও স্ক্রিন-রিডার ব্যবহারকারীদের জন্য `GlowingMicOrb`-এ পূর্ণাঙ্গ অডিও গাইডেন্স।

### ৭. ৩০ দিনের স্বয়ংক্রিয় অডিট লগ রিটেনশন পলিসি
- ব্যবহারকারীর প্রাইভেসি সুরক্ষায় ডিভাইসে সংরক্ষিত সকল কমান্ড হিস্ট্রি ৩০ দিন পর স্বয়ংক্রিয়ভাবে ডাটাবেস থেকে মুছে ফেলা হয়।

---

## 🏗️ আর্কিটেকচার ও প্রজেক্ট স্ট্রাকচার (Project Structure)

```
com.awaj.assistant/
├── accessibility/           # সহায়ক মোড, সেনসিটিভ নোড মাস্কিং ও স্ক্রিন রিডার
│   ├── AssistAccessibilityService.kt
│   ├── GestureDispatcher.kt
│   ├── NodeFinder.kt
│   └── ScreenTreeReader.kt
├── agent/                   # ল্যাব মোড ReAct অটোনোমাস এজেন্ট লুপ
│   ├── AgentLoop.kt
│   ├── AgentPlanner.kt
│   └── AgentState.kt
├── appfunctions/            # Android AppFunctions / MCP Tool Exposure
│   └── AwajAppFunctions.kt
├── data/                    # Room SQLite লোকাল অডিট লগ ও ৩০-দিনের অটো-প্রুনিং
│   ├── db/AppDatabase.kt
│   ├── models/CommandLog.kt
│   └── repository/CommandRepository.kt
├── di/                      # ডিপেন্ডেন্সি ইনজেকশন কন্টেইনার
│   └── AppModule.kt
├── nlu/                     # বাংলা NLU, রুল পার্সার ও Gemini AI ইন্টিগ্রেশন
│   ├── IntentNormalizer.kt
│   ├── LlmClient.kt
│   ├── RuleParser.kt
│   └── ToolSchema.kt
├── notification/            # নোটিফিকেশন লিসেনার ও প্রমিনেন্ট ডিসক্লোজার
│   └── AssistantNotificationListener.kt
├── overlay/                 # ভাসমান মাইক সার্ভিস
│   └── FloatingMicService.kt
├── routines/                # সুপ্রভাত ও শুভ রাত্রি অটোমেশন রুটিন
│   └── RoutineManager.kt
├── safety/                  # সিকিউরিটি গেট, রিস্ক ক্লাসিফায়ার ও পেমেন্ট গার্ড
│   ├── ConfirmationManager.kt
│   ├── PermissionGate.kt
│   ├── RiskClassifier.kt
│   └── SensitiveAppBlocker.kt
├── service/                 # কুইক সেটিংস টাইল সার্ভিস
│   └── AwajQuickTileService.kt
├── stt/                     # বাংলা স্পিচ-টু-টেক্সট ইঞ্জিন ও স্পিচ স্টেট
│   ├── SpeechState.kt
│   └── SttManager.kt
├── tools/                   # ২৫+ অন-ডিভাইস টুলস ও UndoRegistry
│   ├── AlarmTool.kt
│   ├── CallTool.kt
│   ├── TorchTool.kt
│   ├── UndoTool.kt
│   ├── VolumeTool.kt
│   └── ToolRegistry.kt
├── tts/                     # বাংলা টেক্সট-টু-স্পিচ ম্যানেজার
│   └── TtsManager.kt
├── ui/                      # Jetpack Compose ইউআই ও থিম
│   ├── components/GlowingMicOrb.kt
│   ├── home/HomeScreen.kt
│   ├── settings/SettingsScreen.kt
│   └── MainViewModel.kt
└── voice/                   # ৮-মাত্রিক অ্যাকোস্টিক ফিল্টারিং ও ওয়েক-ওয়ার্ড
    ├── VoiceProfileManager.kt
    └── WakeWordDetector.kt
```

---

## 🧪 টেস্ট স্যুট ও কোয়ালিটি অ্যাসিওরেন্স (Automated Tests)

প্রজেক্টে ২৩টি ইউনিট ও সেফটি টেস্ট অন্তর্ভুক্ত যা প্রতিটি বিল্ডে নিশ্চিত করে:
1. **সংবেদনশীল নোড মাস্কিং (`••••••`) ও পাসওয়ার্ড ব্লকিং**
2. **আর্থিক লেনদেন ইনটেন্টের স্বয়ংক্রিয় রিস্ক ইভ্যালুয়েশন**
3. **৮ডি অ্যাকোস্টিক কোসাইন সিমিলারিটি টেস্ট**
4. **ডায়নামিক ফিনটেক প্যাকেজ ব্লকলিস্ট রেজিস্ট্রেশন**
5. **UndoRegistry রিভার্সিবল অ্যাকশন ভ্যালিডেশন**
6. **বাংলা সংখ্যা ও সময় পার্সিং নিখুঁতকরণ**

```bash
# ইউনিট ও সেফটি টেস্ট রান করার কমান্ড:
./gradlew testDebugUnitTest
```

---

## 🚀 লোকাল বিল্ড ও সেটআপ (Getting Started)

1. **রিপোজিটরি ক্লোন করুন:**
   ```bash
   git clone https://github.com/joysriramsarkar/awaj.git
   cd awaj
   ```
2. **Gemini API Key সেট করুন (ঐচ্ছিক):**
   `local.properties` ফাইলে যোগ করুন:
   ```properties
   GEMINI_API_KEY=your_actual_gemini_api_key_here
   ```
3. **ডিবাগ APK বিল্ড করুন:**
   ```bash
   ./gradlew assembleDebug
   ```
   আউটপুট APK পাবেন: `app/build/outputs/apk/debug/app-debug.apk`

---

## 📄 লাইসেন্স (License)
এই প্রকল্পটি [MIT License](LICENSE)-এর অধীনে উন্মুক্ত ও পরিচালিত।
