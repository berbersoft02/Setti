# SettiFlow 💧

> **Accessible, voice-assisted hydration companion and safety tracker designed for elderly individuals and family caregivers.**

[![Android CI](https://github.com/berbersoft02/Setti/actions/workflows/android.yml/badge.svg)](https://github.com/berbersoft02/Setti/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)

---

## 📖 Overview

Maintaining proper daily hydration is essential for health, especially for elderly individuals who frequently experience reduced thirst perception, mobility limitations, or difficulty reading small smartphone screens.

**SettiFlow** bridges this gap by transforming an Android device into an autonomous, voice-guided hydration reminder. Designed specifically for senior users (including those who cannot read or operate complex mobile interfaces), SettiFlow sounds audible speech reminders, displays high-contrast visual cues, and keeps family members informed if prolonged periods without water intake occur.

---

## ✨ Key Features

* **🔊 Autonomous Voice & Full-Screen Alarms:**
  Wakes the screen even when locked, sounds an audible alert, and speaks reminders clearly via Text-to-Speech (*"C'est l'heure de boire de l'eau ! Buvez un verre d'eau maintenant !"*) repeated 3 times.

* **🎯 Elderly-Friendly Accessibility:**
  Features oversized buttons, large typography, pulsing visual indicators, and auto-dismiss after 3 minutes so users never get trapped in an unresponsive screen state.

* **🌙 Smart Quiet Hours:**
  Built-in quiet hours protection (default 21:00 to 09:00) that seamlessly spans across midnight, guaranteeing uninterrupted night sleep.

* **🚨 Caregiver Safety Alert (SMS):**
  Monitors drinking intervals via background tasks. If no hydration activity is recorded for 6+ consecutive waking hours, an emergency SMS is automatically dispatched to the designated caregiver's phone.

* **📊 Offline-First Hydration History:**
  Full daily progress tracking, glass logs, and personalized target management stored locally using Room Database with zero reliance on cloud servers or external account setups.

---

## 🏗️ Architecture

SettiFlow follows Google's recommended modern Android architecture with clear separation of concerns:

```
┌────────────────────────────────────────────────────────┐
│                   UI Layer (Compose)                   │
│      DrinkScreen  •  HistoryScreen  •  SettingsScreen   │
└──────────────────────────┬─────────────────────────────┘
                           │ StateFlow / Events
┌──────────────────────────▼─────────────────────────────┐
│                 ViewModel Layer                        │
│                  WaterViewModel                        │
└──────────────────────────┬─────────────────────────────┘
                           │ Coroutines
┌──────────────────────────▼─────────────────────────────┐
│               Repository & Domain Logic                │
│       WaterRepository  •  UserSettings Domain Model    │
└──────────────┬───────────────────────────┬─────────────┘
               │                           │
┌──────────────▼─────────────┐ ┌───────────▼─────────────┐
│      Persistence (Room)    │ │   Background Operations │
│ AppDatabase  •  WaterDao   │ │ ReminderScheduler       │
│ WaterLog     • UserSettings│ │ WaterReminderWorker     │
└────────────────────────────┘ │ EmergencyCheckWorker    │
                               └─────────────────────────┘
```

---

## 🛠️ Tech Stack

* **Language:** [Kotlin](https://kotlinlang.org/)
* **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3
* **Asynchronous Streams:** Kotlin Coroutines & `StateFlow`
* **Local Persistence:** [Room Database](https://developer.android.com/training/data-storage/room)
* **Background Scheduling:** [Android WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) & `AlarmManager`
* **Speech Engine:** Android `TextToSpeech`
* **Build System:** Gradle (Kotlin DSL `build.gradle.kts`)
* **Continuous Integration:** GitHub Actions

---

## 🚀 Getting Started

### Prerequisites
* Android Studio Hedgehog or newer
* JDK 17
* Android SDK 34+ (Target SDK 36, Min SDK 24)

### Building the Project
Clone the repository and build the debug APK:

```bash
git clone https://github.com/berbersoft02/Setti.git
cd Setti

# Build debug APK
./gradlew assembleDebug
```

The compiled APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 🧪 Testing

Execute host-side unit tests via Gradle:

```bash
./gradlew testDebugUnitTest
```

Unit test suites cover:
* `UserSettingsTest`: Hydration goal calculations, progress percentages, edge cases, and midnight-spanning quiet hours evaluation.
* `ExampleUnitTest`: Basic environment sanity verification.

---

## 📄 License

This project is open-source under the [MIT License](LICENSE).
