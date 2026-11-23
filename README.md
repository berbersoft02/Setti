# SettiFlow

Android application for scheduled hydration monitoring with offline data persistence and caregiver alerts.

## Features

* Hydration Tracking: Log water intake with customizable volume presets.
* Streak & Consistency Analytics: Real-time calculation of daily goals, streaks, averages, and completion metrics.
* Voice and Visual Alarms: Full-screen reminders using Android Text-to-Speech (TTS).
* Background Scheduling: Managed via Android WorkManager and AlarmManager.
* Caregiver Notifications: Automated SMS alerts if hydration thresholds are missed during active hours.
* Offline Persistence: Local storage using Room database with exportable history.
* UI: Built entirely with Jetpack Compose and Material 3 design components.

## Architecture

```
UI Layer (Jetpack Compose)
  └── DrinkScreen, HistoryScreen, SettingsScreen
        │ StateFlow / Events
        ▼
ViewModel Layer
  └── WaterViewModel
        │ Coroutines
        ▼
Domain & Repository Layer
  ├── HydrationAnalytics (daily summaries, streaks, safety triggers)
  ├── UserSettings
  └── WaterRepository
        │
        ├── Persistence (Room Database: AppDatabase, WaterDao, WaterLog)
        └── Background Operations (ReminderScheduler, WaterReminderWorker, EmergencyCheckWorker)
```

## Setup and Installation

Prerequisites: Android Studio, JDK 17, Android SDK (minSdk 26, targetSdk 34).

```bash
# Clone the repository
git clone https://github.com/berbersoft02/Setti.git
cd Setti

# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew testDebugUnitTest
```

## License

MIT
