# SettiFlow Architecture Overview

SettiFlow is architected using modern Android best practices, following Google's official recommended app architecture.

## Architecture Layers

* **UI Layer:** Built with Jetpack Compose & Material 3 (`DrinkScreen`, `HistoryScreen`).
* **ViewModel:** `WaterViewModel` exposing observable UI state via Kotlin StateFlow.
* **Repository Layer:** `WaterRepository` coordinating between local database and notifications.
* **Persistence Layer:** Room Database (`AppDatabase`, `WaterDao`, `WaterLog`).

## Background Work & Notifications

* `ReminderScheduler` schedules exact alarms.
* `WaterReminderWorker` triggers notification alerts.
* `EmergencyCheckWorker` checks hydration safety intervals.
