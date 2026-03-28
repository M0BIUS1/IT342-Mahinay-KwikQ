# IT342-Mahinay-KwikQ

KwikQ is a Library Queue Management project for IT342 with:
- Phase 1: Web backend + web authentication UI
- Phase 2: Android mobile authentication app integrated with the same backend

## Repository Structure
- `web/kwikq` - Spring Boot backend and web pages
- `mobile/KwikQ` - Android app (Kotlin + XML)
- `docs/` - project documentation

## Phase 2 Scope Completed
### Mobile Registration
- Name, Email, Password, Confirm Password inputs
- Client-side validation
- Calls backend `POST /api/auth/register`
- Displays error/success feedback

### Mobile Login
- Email and Password inputs
- Client-side validation
- Calls backend `POST /api/auth/login`
- Handles invalid credentials and network errors
- Redirects to dashboard/home after success

### Session Handling
- Saves JWT session locally (SharedPreferences)
- Auto-login when a valid session exists
- Logout clears session

## Backend API Used
- `POST /api/auth/register`
- `POST /api/auth/login`

## Local Development Setup
### Prerequisites
- Java 17+
- Maven Wrapper (included)
- Android Studio / Android SDK
- Android Emulator or device

## Run Backend (Spring Boot)
From repository root:

```powershell
cd web/kwikq
.\mvnw.cmd spring-boot:run
```

Backend base URL (local):
- `http://localhost:8080`

## Run Mobile App (Android)
From repository root:

```powershell
cd mobile/KwikQ
.\gradlew.bat assembleDebug
```

Open the app in Android Studio and run on emulator/device.

## Mobile-to-Backend Connection Note
The app uses local backend URL through BuildConfig:
- Emulator: `http://10.0.2.2:8080/`

If using a physical device, update the mobile base URL to your machine LAN IP and keep the same port.

## Final Phase 2 Commits
- `IT342 Phase 2 – Mobile Development Completed`
  - Hash: `6d35e3fee2648282a897113f9db0fd04c4b57e91`
  - Link: https://github.com/M0BIUS1/IT342-Mahinay-KwikQ/commit/6d35e3fee2648282a897113f9db0fd04c4b57e91

- `IT342 Phase 2 – Mobile UI and UX Polish`
  - Hash: `0ed41df222aff830bacb76a88f5d3e482ae7a346`
  - Link: https://github.com/M0BIUS1/IT342-Mahinay-KwikQ/commit/0ed41df222aff830bacb76a88f5d3e482ae7a346