# Project Plan

Build a complete Android application named "PhishGuard" (CyberSafe) - a free cyber safety tool for Indian users. 
Key features include:
- AI Text Scanner (using Gemini 2.5 Flash) to analyze scam messages (UPI fraud, KYC scams, etc.).
- Deepfake/AI Image Detector (using Gemini 2.5 Flash) to identify AI-generated or manipulated images.
- Password Guard: Offline strength analyzer and breach checker (using HaveIBeenPwned k-anonymity).
- URL/Link Scanner: Multi-layer scan using Android SafetyNet, VirusTotal API, and Gemini AI.
- QR Code Scanner for safe URL extraction.
- Email Breach Checker (HaveIBeenPwned).
- App Permission Analyzer and WiFi Safety Check.
- Emergency SOS and India Cyber Helplines directory.
- Scan History (SQLite) and Firebase Authentication.
- Material Design 3 UI with a dark, energetic theme (Deep Navy, Neon Green, Warning Orange, Danger Red).
- Architecture: MVVM + Repository + Coroutines.
- Detailed file structure and dependencies provided.

## Project Brief

# PhishGuard (CyberSafe) Project Brief

A specialized cyber safety utility designed to protect Indian users from modern digital threats, including UPI fraud, phishing, and AI-manipulated media.

## Features
- **AI-Powered Scam Scanner:** Utilizes Gemini AI to analyze suspicious text messages (SMS, WhatsApp) for UPI fraud and KYC scams, alongside a Deepfake detector to identify AI-generated or manipulated images.
- **Secure Link & QR Guard:** Multi-layered scanning of URLs extracted from text or QR codes using VirusTotal API, Android SafetyNet, and AI analysis to block phishing attempts.
- **Identity & Password Shield:** Offline password strength analyzer and an email breach checker leveraging the HaveIBeenPwned k-anonymity API to verify data exposure.
- **Cyber Safety Toolkit:** A centralized dashboard featuring an App Permission Analyzer, WiFi safety checks, and an Emergency SOS directory for Indian Cyber Helplines.

## High-Level Tech Stack
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material Design 3) with Edge-to-Edge support.
- **Architecture:** MVVM (Model-View-ViewModel) + Repository Pattern.
- **Asynchronous Tasks:** Kotlin Coroutines & Flow.
- **Networking:** Retrofit & OkHttp for API integrations (Gemini, VirusTotal, HaveIBeenPwned).
- **AI Integration:** Google Generative AI SDK (Gemini 2.5 Flash).
- **Persistence:** Room Database (leveraging **KSP** for code generation) to store scan history.
- **Hardware Integration:** CameraX for QR code and image scanning.

## Implementation Steps

### Task_1_Core_Infrastructure: Set up the project's foundation including the data layer, networking, and authentication. Configure Room database for scan history, Retrofit for external APIs (VirusTotal, HaveIBeenPwned), integrate the Gemini AI SDK, and set up Firebase Authentication.
- **Status:** COMPLETED
- **Updates:** Successfully set up the core infrastructure for PhishGuard (com.uri.phishguard).
- **Acceptance Criteria:**
  - Room database and DAO for scan history implemented
  - Retrofit clients for VirusTotal and HIBP configured
  - Gemini AI SDK (Generative AI) integrated
  - Firebase Authentication setup (Login/Signup logic)
  - Repository pattern implemented for all data sources

### Task_2_AI_and_Scanning_Engines: Implement the core scanning features: AI Text Scanner for scams, Deepfake Detector for image analysis, and Link/QR Scanner. Integrate Gemini 2.5 Flash for analysis and CameraX for QR/image capture.
- **Status:** COMPLETED
- **Updates:** Implemented the core scanning features for PhishGuard:
- **Acceptance Criteria:**
  - AI Text Scanner accurately identifies scam messages using Gemini
  - Deepfake Detector identifies AI-generated/manipulated images
  - QR Scanner successfully extracts and validates URLs
  - Link Scanner integrates VirusTotal and AI analysis for safety checks
- **Duration:** 33m 35s

### Task_3_Security_and_Safety_Tools: Develop the Identity Shield and Safety Toolkit features. This includes the Password Guard (offline strength + HIBP check), Email Breach Checker, App Permission Analyzer, WiFi Safety Check, and the Emergency SOS directory.
- **Status:** COMPLETED
- **Updates:** Developed the Identity Shield and Safety Toolkit features for PhishGuard:
- Password Guard: Implemented `PasswordViewModel` and `PasswordScreen` with offline strength analysis and HIBP k-anonymity check.
- Email Breach Checker: Created `EmailBreachViewModel` and `EmailBreachScreen` using HIBP API.
- App Permission Analyzer: Developed `PermissionViewModel` and `PermissionScreen` to list and assess app permissions.
- WiFi Safety Check: Implemented `WifiViewModel` and `WifiScreen` to detect unsecured network configurations.
- Emergency SOS Directory: Created `EmergencyViewModel` and `EmergencyScreen` populated with Indian cyber helplines and click-to-call functionality.
- UI: All screens follow Material 3 Design with a dark energetic theme.
- Navigation: Integrated all new toolkit screens into the app's navigation graph.
- **Acceptance Criteria:**
  - Password Guard provides strength feedback and checks HIBP database
  - Email breach checker functional via HIBP API
  - Permission Analyzer correctly identifies and lists sensitive app permissions
  - WiFi safety check detects unsecured network configurations
  - Emergency SOS directory populated with Indian cyber helplines
- **Duration:** N/A

### Task_4_UI_UX_Implementation: Create the user interface using Jetpack Compose following Material Design 3 guidelines. Implement the Dashboard, Authentication screens, Scan History, and Feature screens. Apply the dark energetic theme and full edge-to-edge display.
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - Main Dashboard provides intuitive access to all features
  - Scan History screen correctly displays past results
  - Material 3 theme with specified colors (Deep Navy, Neon Green) applied
  - Full Edge-to-Edge display implemented
  - Adaptive app icon created and functional
- **StartTime:** 2026-04-07 01:08:59 IST

### Task_5_Final_Run_and_Verify: Perform a comprehensive build and execution of the PhishGuard app. Verify all features against the requirements, check for stability, and ensure the UI aligns with the energetic branding.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Project builds successfully and app does not crash
  - All core features (AI Scanners, Security Tools, SOS) verified functional
  - UI is consistent with Material 3 and specified color scheme
  - All existing tests pass

