KwikQ Mobile — local dev notes

Run unit tests (JVM) and build debug APKs.

Run unit tests:

```powershell
cd mobile\KwikQ
.\gradlew.bat :app:test
```

Build debug APK (Android Studio recommended):

```powershell
cd mobile\KwikQ
.\gradlew.bat assembleEmulatorDebug
```

Notes:
- `BASE_URL` flavors: `emulator`, `device`, `prod` set BuildConfig.BASE_URL.
- Use Android Studio to run and debug on emulator (use `http://10.0.2.2:8080` to reach backend running on host).

Debug helpers added:
- `RetryLogActivity` — open from Books screen via "Debug: Retry Log" to inspect recorded retry events.
- `DebugOverlay` — lightweight overlay that shows a running retry count on main screens.
- Analytics exporter (opt-in): enable from Profile -> "Send retry analytics", set sampling % and events will be POSTed to `api/analytics/retry` on the backend when sampled.
 - Sampling Save: use the new `Save` button next to the Sampling % input to persist the sampling percent (or long-press the Debug button as legacy behavior).
 - Robolectric test: `ProfileActivityRobolectricTest` verifies sampling persistence. CI runs JVM tests.
