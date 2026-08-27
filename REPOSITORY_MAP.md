# Awaj (আওয়াজ) — Repository Architecture & Codebase Map

> This file is provided for AI models, bots, and developers to easily understand the full codebase layout, architectural patterns, safety models, and tool registry without requiring deep multi-file tree traversal.

---

## 🗺️ Complete Source Tree & Package Architecture

```
com.awaj.assistant/
│
├── AwajApplication.kt               # Application entrypoint & Dependency Injection bootstrap (AppModule)
├── MainActivity.kt                  # Single-Activity compose container with lock-screen display flags
│
├── accessibility/                   # Android Accessibility Service for Mode 2 & Mode 3
│   ├── AssistAccessibilityService.kt # Foreground accessibility engine with state listener
│   ├── GestureDispatcher.kt          # Programmatic click & scroll dispatch with password node protection
│   ├── NodeFinder.kt                 # UI hierarchy text & ID matching algorithms
│   └── ScreenTreeReader.kt           # Screen node extraction with dynamic password & PIN masking
│
├── agent/                           # Lab Mode ReAct Autonomous Agent Loop
│   ├── AgentLoop.kt                  # Multi-step goal planner & executor with APM safety bounds
│   ├── AgentPlanner.kt               # Goal to sub-action decomposition engine
│   └── AgentState.kt                 # Agent state machine (Idle, Thinking, Acting, Finished, Failed)
│
├── appfunctions/                    # Android AppFunctions / MCP Tool Exposure (Google I/O 2026)
│   └── AwajAppFunctions.kt           # Exposes 6 low-risk tools safely to system AI agents
│
├── data/                            # Local SQLite Storage & Preference Repository
│   ├── db/
│   │   ├── AppDatabase.kt           # Room Database definition with destructive migration fallback
│   │   └── CommandLogDao.kt         # Room DAOs with 30-day auto-retention log pruning
│   ├── models/
│   │   └── CommandLog.kt            # Entity models for local audit logs
│   └── repository/
│       └── CommandRepository.kt     # Repository pattern wrapping local SQLite & SharedPreferences
│
├── di/
│   └── AppModule.kt                 # Lightweight, pure Kotlin Dependency Injection container
│
├── nlu/                             # Bengali Natural Language Understanding & Gemini AI Engine
│   ├── IntentNormalizer.kt          # Digit converter (০-৯ -> 0-9), punctuation stripper, word cleaner
│   ├── LlmClient.kt                 # Gemini 1.5 Flash client for smart Q&A and multi-intent tool planning
│   ├── RuleParser.kt                # 25+ Ultra-fast on-device regex & rule parser
│   └── ToolSchema.kt                # ActionRequest, RiskLevel, ToolResult data models
│
├── notification/                    # Notification Listener Service
│   └── AssistantNotificationListener.kt # Local temporary buffer for reading notifications on demand
│
├── overlay/                         # System Alert Window Overlay
│   └── FloatingMicService.kt        # Draggable floating microphone bubble service
│
├── routines/                        # Morning & Night Automation Routines
│   └── RoutineManager.kt            # Sequential multi-tool execution engine
│
├── safety/                          # 4-Layer Safety & Security Gate
│   ├── ConfirmationManager.kt       # Two-way voice ("হ্যাঁ" / "না") & visual confirmation gate
│   ├── PermissionGate.kt            # Runtime permission evaluator with APM & OEM battery helper
│   ├── RiskClassifier.kt            # Risk level evaluator (LOW, MEDIUM, HIGH, BLOCKED)
│   └── SensitiveAppBlocker.kt       # UPI & Banking app safeguard (Google Pay, PhonePe, Paytm, bKash, etc.)
│
├── stt/                             # Speech-to-Text Engine
│   ├── SpeechState.kt               # State models (Idle, Listening, Processing, Speaking, Error)
│   └── SttManager.kt                # Android SpeechRecognizer wrapper with Bengali (bn-BD / bn-IN) locale
│
├── tts/                             # Text-to-Speech Engine
│   └── TtsManager.kt                # Android TextToSpeech engine with completion callbacks & fallback timer
│
├── tools/                           # 20+ Modular Tool Implementations
│   ├── Tool.kt                      # Base Tool interface (suspend fun execute)
│   ├── AiChatTool.kt                # AI Q&A conversational responses
│   ├── AlarmTool.kt                 # Clock alarm scheduler with OEM fallback
│   ├── BrightnessTool.kt            # Display brightness adjuster
│   ├── CalculatorTool.kt            # Bengali arithmetic calculator
│   ├── CalendarTool.kt              # Calendar event scheduler
│   ├── CallTool.kt                  # Phone dialer & direct call with confirmation
│   ├── CameraTool.kt                # Photo, selfie, and video recorder
│   ├── ConnectivityTool.kt          # WiFi, Bluetooth, Airplane mode toggles
│   ├── DeviceInfoTool.kt            # Battery percentage & system status
│   ├── HotspotTool.kt               # Mobile hotspot & tethering control
│   ├── MediaControlTool.kt          # Music play, pause, next track controls
│   ├── MusicPlayerTool.kt           # Spotify & YouTube song playback search
│   ├── OpenAppTool.kt               # Safe package launcher (strictly bare launch intents)
│   ├── ReadNotificationsTool.kt     # On-demand spoken notification reader
│   ├── SettingsTool.kt              # Android system settings navigator
│   ├── SmsTool.kt                   # SMS messaging with confirmation
│   ├── StopTool.kt                  # Emergency stop all actions
│   ├── TimerTool.kt                 # Countdown timer scheduler
│   ├── TorchTool.kt                 # Camera flashlight toggle
│   ├── WeatherTool.kt               # Weather inquiry tool
│   ├── WebSearchTool.kt             # Google web search launcher
│   ├── WhatsAppTool.kt              # WhatsApp message drafter & sender
│   └── ToolRegistry.kt              # Central registry coordinating tool dispatch and safety checks
│
├── ui/                              # Jetpack Compose Reactive UI Layer
│   ├── MainViewModel.kt             # Shared ViewModel coordinating STT, TTS, NLU, and safety gates
│   ├── components/
│   │   ├── ActionCard.kt            # Live result card with risk badge & status
│   │   ├── ConfirmationDialog.kt    # Visual confirmation popup
│   │   ├── GlowingMicOrb.kt         # Animated pulsating neon voice orb
│   │   └── SuggestionChips.kt       # Quick command and AI suggestion pills
│   ├── history/HistoryScreen.kt     # Local SQLite audit log viewer with clear history
│   ├── home/HomeScreen.kt           # Main voice assistant screen
│   ├── permissions/PermissionsScreen.kt # Transparent permission center with OEM battery options
│   ├── routines/RoutinesScreen.kt   # One-tap daily routine executor
│   ├── settings/SettingsScreen.kt   # Theme switcher, Gemini API key manager, & voice tuning
│   └── theme/                       # Design tokens, typography, and dark/light color schemes
│
└── voice/                           # Background Wake-Word & Voice Tuning
    ├── VoiceProfileManager.kt       # Acoustic feature calibration (RMS Energy, ZCR, Roughness)
    ├── VoiceService.kt              # Persistent foreground wake-word listening service
    └── WakeWordDetector.kt          # Continuous audio buffer listener for "হেই আওয়াজ" / "Hey Awaj"
```

---

## 🔒 Safety & Risk Classification Table

| Risk Level | Triggering Commands | Safety Mechanism |
| :--- | :--- | :--- |
| **LOW (Level 0)** | Torch, Volume, Brightness, Alarms, Timers, Math, Weather, App Launch, AI Q&A | Immediate Execution via Safe Intent |
| **MEDIUM (Level 1)** | Read Notifications, Calendar Events, Device Info | Requires Explicit User Request / Local Buffer |
| **HIGH (Level 2)** | Phone Calls, SMS, WhatsApp Messages | **Mandatory Confirmation Gate** ("হ্যাঁ" / "না" Voice or Tap) |
| **BLOCKED (Level 3)** | Money Transfers, UPI PINs, Passwords, OTPs, CVV, Banking Screen Automation | **100% Blocked** (Hardcoded & Dynamic Node Rejection) |
