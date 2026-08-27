# Awaj (আওয়াজ) — বাংলা ভয়েস অ্যাসিস্ট্যান্ট ও অটোমেশন অ্যান্ড্রয়েড ইঞ্জিন

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" width="100" alt="Awaj Logo" />
</p>

<p align="center">
  <b>একটি বাংলা-প্রথম, ভয়েস-ফার্স্ট অ্যান্ড্রয়েড অ্যাসিস্ট্যান্ট ও ইন্টেলিজেন্ট অটোমেশন ইঞ্জিন</b><br>
  বাংলা ও বাংলিশ ভয়েস কমান্ডে ফোন নিয়ন্ত্রণ, ভয়েস বায়োমেট্রিক নিরাপত্তা, প্লে-স্টোর সেফ মোড ও ল্যাব অটোমেশন।
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.0-blue.svg?logo=kotlin" />
  <img src="https://img.shields.io/badge/Jetpack_Compose-Material_3-purple.svg?logo=android" />
  <img src="https://img.shields.io/badge/Architecture-Clean%20%2F%20MVI-brightgreen.svg" />
  <img src="https://img.shields.io/badge/Safety-Gate%20Protected-orange.svg" />
  <img src="https://img.shields.io/badge/License-MIT-green.svg" />
</p>

---

## 📖 প্রকল্প পরিচিতি (Overview)

**Awaj (আওয়াজ)** একটি আধুনিক, নিরাপদ ও সম্পূর্ণ বাংলা ভাষা-ভিত্তিক অ্যান্ড্রয়েড অ্যাসিস্ট্যান্ট। সাধারণ ভয়েস অ্যাসিস্ট্যান্টগুলোর সীমাবদ্ধতা অতিক্রম করে এটি বাংলা সংখ্যা, আঞ্চলিক উচ্চারণ, সময় ও জটিল দৈনন্দিন কাজ অত্যন্ত দ্রুতগতিতে ডিভাইস স্তরে সম্পাদন করে।

এটি **৩টি সুনির্দিষ্ট অপারেটিং মোড** এবং একটি শক্তিশালী **নিরাপত্তা ও প্রাইভেসি গেট** দ্বারা পরিচালিত, যাতে ব্যবহারকারীর ব্যক্তিগত গোপনীয়তা ও আর্থিক নিরাপত্তা ১০০% অক্ষুণ্ণ থাকে।

---

## 🌟 প্রধান বৈশিষ্ট্যসমূহ (Key Features)

### ১. ভয়েস বায়োমেট্রিক সিকিউরিটি (Speaker Voice Profile)
- **কণ্ঠস্বর শনাক্তকরণ:** ৩টি প্রাথমিক ভয়েস নমুনার সাহায্যে ব্যবহারকারীর কণ্ঠস্বরের বায়োমেট্রিক প্রোফাইল (Pitch, RMS Energy, Spectral Roughness) তৈরি করে।
- **লক-স্ক্রিন সুরক্ষা:** ফোন লক থাকা অবস্থায় শুধুমাত্র নিবন্ধিত মূল ব্যবহারকারীর কণ্ঠে **“হেই আওয়াজ”** বা **“Hey Awaj”** বললেই স্ক্রিন আলো জ্বলে উঠবে এবং কমান্ড নেবে। অপরিচিত কারো কণ্ঠে কোনো কমান্ড গৃহীত হবে না।

### ২. ৩টি সমন্বিত অপারেটিং মোড (3 Operating Modes)
1. **Safe Mode (প্লে-স্টোর সেফ):** অ্যান্ড্রয়েড স্ট্যান্ডার্ড Intent, Settings API, AlarmManager ও Media Controller ব্যবহার করে ১০০% পলিসি-সম্মত নিরাপদ কাজ পরিচালনা করে।
2. **Accessibility Mode (সহায়ক মোড):** দৃষ্টি ও শারীরিকভাবে বিশেষ চাহিদাসম্পন্ন ব্যবহারকারীদের জন্য স্ক্রিনের উপাদান পড়ে শোনানো ও ভয়েস নেভিগেশন সহায়তা।
3. **Lab Agent Mode (ল্যাব মোড):** পাওয়ার ইউজার ও অটোমেশন গবেষকদের জন্য স্বায়ত্তশাসিত GUI ReAct Agent লুপ (`AgentPlanner`, `AgentLoop`), যা স্ক্রিন বিশ্লেষণ করে কাজ সম্পন্ন করতে পারে। *(Android APM বা Restricted Settings কার্যকর থাকলে এটি গ্রেসফুলি ব্যর্থতা স্বীকার করে Safe Mode-এ ফিরে যায়)*।

### ৩. পেমেন্ট ও ব্যাংকিং অ্যাপ সুরক্ষা গেট (Financial Safeguard)
- **সুরক্ষিত অ্যাপসমূহ:** Google Pay (GPay), PhonePe, Paytm, BHIM UPI, YONO SBI, HDFC, ICICI, Axis, Kotak, বিকাশ (`bKash`), নগদ (`Nagad`), রকেট, সেলফিন ইত্যাদি।
- **কঠোর নীতিমালা:**
  - ব্যবহারকারী বললে অ্যাপটি নিরাপদে ওপেন হবে (যেমন: *"গুগল পে খোলো"*), যাতে ব্যবহারকারী নিজের বায়োমেট্রিক বা ফিঙ্গারপ্রিন্ট দিয়ে নিজে লেনদেন করতে পারেন।
  - কিন্তু যেকোনো ধরনের স্বয়ংক্রিয় টাকা পাঠানো, ইউপিআই পিন (UPI PIN), ওটিপি (OTP), সিভিভি (CVV) বা পাসওয়ার্ড ইনপুট কমান্ড **১০০% স্বয়ংক্রিয়ভাবে ব্লকড**।

### ৪. ২৫+ শক্তিশালী বাংলা ভয়েস কমান্ড (Built-in Commands)
- 📱 **অ্যাপ লঞ্চার:** *"হোয়াটসঅ্যাপ খোলো"*, *"ইউটিউব ওপেন করো"*, *"স্পটিফাই চালাও"*, *"ব্লিনকিট খোলো"*, *"গুগল পে ওপেন কর"*
- 📶 **কানেক্টিভিটি ও হটস্পট:** *"হটস্পট চালু করো"*, *"হটস্পট বন্ধ করো"*, *"ওয়াইফাই সেটিংস"*, *"ব্লুটুথ অন করো"*, *"ফ্লাইট মোড"*
- 🎵 **মিউজিক ও গান:** *"ইউটিউবে রবীন্দ্রসঙ্গীত লাগাও"*, *"স্পটিফাইতে গান চালাও"*, *"গান বাজাও"*, *"গান পজ করো"*, *"পরের গান"*
- 🧮 **বাংলা অঙ্ক ও ক্যালকুলেটর:** *"১০ গুণ ২০ কত"*, *"১০০ যোগ ৫০"*, *"৫০ বিয়োগ ২০"*, *"১০০ ভাগ ৪"*
- 📸 **ক্যামেরা ও সেলফি:** *"সেলফি তোলো"*, *"ভিডিও রেকর্ড করো"*, *"ক্যামেরা খোলো"*
- ⏰ **অ্যালার্ম ও টাইমার:** *"কাল সকাল আটটায় অ্যালার্ম দাও"*, *"রাত ৯:৩০ এ অ্যালার্ম"*, *"৫ মিনিটের টাইমার দাও"*
- 💡 **টর্চ ও ডিসপ্লে:** *"টর্চ জ্বালাও"*, *"টর্চ বন্ধ করো"*, *"ভলিউম বাড়াও"*, *"ভলিউম ৫০% করো"*, *"ব্রাইটনেস কমাও"*
- 📞 **কল ও মেসেজ (নিশ্চিতকরণসহ):** *"মাকে কল করো"*, *"রাহুলকে এসএমএস পাঠাও: আমি পৌঁছে গেছি"*, *"হোয়াটসঅ্যাপে মেসেজ দাও"*
- ☀️ **তথ্য ও আবহাওয়া:** *"আজকের আবহাওয়া কেমন"*, *"আজ কি বৃষ্টি হবে"*, *"ব্যাটারি চার্জ কত শতাংশ"*
- 🔄 **দৈনন্দিন রুটিন:** *"সুপ্রভাত"* (দিনের আবহাওয়া, ব্যাটারি ও ব্রাইটনেস সেট করে), *"শুভ রাত্রি"* (নীরব মোড, টর্চ অফ ও সকালের অ্যালার্ম)
- 🛑 **এমার্জেন্সি বাতিল:** *"থামো"*, *"বাতিল"*, *"স্টপ"*

### ৫. Google I/O 2026 AppFunctions (Android MCP Integration)
- Awaj-এর মৌলিক টুলসমূহ (`open_app`, `set_alarm`, `set_timer`, `toggle_torch`, `set_volume`, `get_device_info`) অ্যান্ড্রয়েডের নতুন **AppFunctions** স্কিমায় এক্সপোজড, যার ফলে সিস্টেম AI ও Gemini এজেন্ট সরাসরি Awaj-এর বাংলা টুল ব্যবহার করতে পারে।

### ৬. আধুনিক ডার্ক গ্লাস-মরফিজম ইউআই (Jetpack Compose)
- অ্যানিমেটেড পালসিং গ্লোয়িং মাইক অর্ব (ভয়েস ওয়েভফর্মের সাথে স্পন্দিত)।
- ৫টি বটম ন্যাভিগেশন ট্যাব: **সহকারী**, **রুটিন**, **ইতিহাস**, **পারমিশন সেন্টার**, এবং **সেটিংস**।
- এপিআই কী পাসওয়ার্ড মাস্কিং, নোটিফিকেশন লিসেনারের লাইভ পারমিশন আপডেট ও সরাসরি রিফ্রেশ সুবিধা।

---

## 🏗️ আর্কিটেকচার ও প্রজেক্ট স্ট্রাকচার (Project Structure)

```
com.awaj.assistant/
├── accessibility/           # সহায়ক মোড ও স্ক্রিন নোড রিডার
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
├── data/                    # Room SQLite লোকাল অডিট লগ ও প্রেফারেন্স
│   ├── db/AppDatabase.kt
│   ├── models/CommandLog.kt
│   └── repository/CommandRepository.kt
├── nlu/                     # বাংলা NLU, রুল পার্সার ও Gemini ইন্টিগ্রেশন
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
├── stt/ & tts/              # স্পিচ টু টেক্সট ও টেক্সট টু স্পিচ ইঞ্জিন
│   ├── SttManager.kt
│   └── TtsManager.kt
├── tools/                   # ২০+ মডিউলার টুলস বাস্তবায়ন
│   ├── AlarmTool.kt, BrightnessTool.kt, CalculatorTool.kt, CameraTool.kt
│   ├── ConnectivityTool.kt, DeviceInfoTool.kt, HotspotTool.kt
│   ├── MusicPlayerTool.kt, OpenAppTool.kt, ReadNotificationsTool.kt
│   └── ToolRegistry.kt ...
├── ui/                      # জেটপ্যাক কম্পোজ স্ক্রিন ও গ্লোয়িং অর্ব
│   ├── components/ (GlowingMicOrb, ActionCard, SuggestionChips)
│   ├── history/HistoryScreen.kt
│   ├── home/HomeScreen.kt
│   ├── permissions/PermissionsScreen.kt
│   ├── routines/RoutinesScreen.kt
│   └── settings/SettingsScreen.kt
└── voice/                   # ওয়েক-ওয়ার্ড লিসেনার ও ভয়েস বায়োমেট্রিক প্রোফাইল
    ├── VoiceProfileManager.kt
    ├── VoiceService.kt
    └── WakeWordDetector.kt
```

---

## 🔒 নিরাপত্তা ও পারমিশন নীতি (Safety & Permission Matrix)

| পারমিশন | ব্যবহারের কারণ | মোড | ঝুঁকি স্তর |
| :--- | :--- | :--- | :--- |
| `RECORD_AUDIO` | ব্যবহারকারীর বাংলা ভয়েস কমান্ড ও ওয়েক-ওয়ার্ড শোনা | সর্বজনীন | **LOW** |
| `READ_CONTACTS` | নাম অনুযায়ী কন্টাক্ট নম্বর খুঁজে কল/এসএমএস ড্রাফট করা | Safe/Access | **MEDIUM** |
| `CALL_PHONE` | ভয়েস নিশ্চিতকরণের পর সরাসরি ফোন কল দেওয়া | Safe | **HIGH (নিশ্চিতকরণ আবশ্যক)** |
| `SEND_SMS` | ভয়েস নিশ্চিতকরণের পর এসএমএস বার্তা পাঠানো | Safe | **HIGH (নিশ্চিতকরণ আবশ্যক)** |
| `NOTIFICATION_LISTENER` | সাম্প্রতিক নোটিফিকেশন পড়ে শোনানো (লোকাল মেমোরি) | Safe/Access | **MEDIUM (বিশেষ সম্মতি)** |
| `ACCESSIBILITY_SERVICE` | স্ক্রিনের উপাদান পড়া ও ল্যাব অটোমেশন | Lab / Access | **RESTRICTED (ল্যাব মোড)** |

---

## 🚀 ইন্সটলেশন ও রান করার নিয়ম (Build & Installation)

### ১. সোর্স কোড ক্লোন করুন
```bash
git clone https://github.com/joysriramsarkar/awaj.git
cd awaj
```

### ২. Android Studio-তে ওপেন করুন
- `File -> Open -> awaj` ডিরেক্টরিটি নির্বাচন করুন।
- Gradle Sync সম্পন্ন হতে দিন (JDK 21 রিকমেন্ডেড)।

### ৩. ইউনিট টেস্ট চালান
```bash
./gradlew testDebugUnitTest
```
*(১৭টি টেস্টের ১৭টিই সফলভাবে পাস করবে)*

### ৪. ডিবাগ APK বিল্ড করুন
```bash
./gradlew assembleDebug
```
তৈরিকৃত APK পাওয়া যাবে: `app/build/outputs/apk/debug/app-debug.apk`

---

## 🛠️ ব্যবহারের নির্দেশিকা (Getting Started)

1. অ্যাপটি চালু করে প্রয়োজনীয় মাইক্রোফোন পারমিশন দিন।
2. **সেটিংস** ট্যাবে গিয়ে **“ভয়েস সিকিউরিটি ও প্রোফাইল”**-এ ৩ বার আপনার কণ্ঠে “হেই আওয়াজ” বলে এনরোল করুন।
3. **“ব্যাকগ্রাউন্ড ভয়েস সার্ভিস”** চালু করুন।
4. এখন ফোন ব্যবহার করার সময় বা ফোন লক থাকা অবস্থায় বাংলায় কথা বলুন:
   - *"হেই আওয়াজ"*
   - *"টর্চ জ্বালাও"*
   - *"ইউটিউবে গান চালাও"*
   - *"হটস্পট অন করো"*
   - *"১০ গুণ ২০ কত"*
   - *"কাল সকাল ৭টায় অ্যালার্ম দাও"*

---

## 📄 প্রাইভেসি পলিসি (Privacy Policy)
Awaj ব্যবহারকারীর গোপনীয়তাকে সর্বোচ্চ অগ্রাধিকার দেয়। বিস্তারিত তথ্যের জন্য আমাদের [PRIVACY_POLICY.md](file:///c:/Users/joysr/Documents/awaj/PRIVACY_POLICY.md) ফাইলটি দেখুন।

---

## 🤝 অবদান (Contributing)
আমরা মুক্ত-উৎস কমিউনিটির যেকোনো অবদানকে স্বাগত জানাই! আপনি নতুন কোনো বাংলা কমান্ড যোগ করতে চাইলে বা বাগ ফিক্স করতে চাইলে Pull Request পাঠাতে পারেন।

---

## 📜 লাইসেন্স (License)
এই প্রকল্পটি [MIT License](LICENSE)-এর অধীনে উন্মুক্ত।
