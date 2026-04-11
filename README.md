<div align="center">

<!-- BANNER / LOGO -->
<img src="docs/banner.png" alt="PhishGuard Banner" width="100%"/>

<br/>

# 🛡️ PhishGuard
### *Free AI-Powered Cybersecurity App for Android*

**Detect Scams · Expose Deepfakes · Scan URLs · Guard Passwords**

<br/>

<!-- BADGES ROW 1 — Status -->
![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Language](https://img.shields.io/badge/Kotlin-100%25-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Min SDK](https://img.shields.io/badge/Min%20SDK-26%20(Android%208.0)-blue?style=for-the-badge&logo=android&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

<!-- BADGES ROW 2 — Tech -->
![Gemini](https://img.shields.io/badge/Gemini%202.5%20Flash-886FBF?style=for-the-badge&logo=googlegemini&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![VirusTotal](https://img.shields.io/badge/VirusTotal-394EFF?style=for-the-badge&logo=virustotal&logoColor=white)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-orange?style=for-the-badge)

<!-- BADGES ROW 3 — Meta -->
![Stars](https://img.shields.io/github/stars/harsh9105/PhishGuard?style=for-the-badge&color=yellow)
![Forks](https://img.shields.io/github/forks/harsh9105/PhishGuard?style=for-the-badge&color=blue)
![Issues](https://img.shields.io/github/issues/harsh9105/PhishGuard?style=for-the-badge&color=red)
![Last Commit](https://img.shields.io/github/last-commit/harsh9105/PhishGuard?style=for-the-badge&color=brightgreen)

<br/>

[**📱 Download APK**](#-installation) · [**🚀 Features**](#-features) · [**🏗️ Architecture**](#-architecture) · [**⚙️ Setup**](#-setup--installation) · [**🤝 Contribute**](#-contributing)

</div>

---

## 🎯 What is PhishGuard?

> **PhishGuard is a 100% free, open-source Android app that puts AI-powered cybersecurity in everyone's hands.**

India sees **1.5 million+ cybercrime complaints per year**. Most victims are ordinary people who couldn't tell a scam message from a real one, or a deepfake image from a real photo.

PhishGuard fixes that. Paste any suspicious message, upload any image, or scan any link — and get an instant AI verdict in plain language. No subscription. No technical knowledge needed. **Completely free.**

```
📨 Got a suspicious WhatsApp message?  → Paste it. AI tells you if it's a scam.
🖼️ Unsure if an image is AI-generated? → Upload it. Gemini analyses 14 forensic signals.
🔗 Got a sketchy link?                  → Paste it. 3-layer scan: Safe Browsing + VirusTotal + AI.
🔑 Worried about your password?        → Check it. K-anonymity breach check — password never leaves your phone.
```

---

## ✨ Features

<table>
<tr>
<td width="50%">

### 🤖 AI Scam Text Scanner
- Analyses SMS, WhatsApp, email text
- Detects **12 India-specific scam types** (UPI fraud, fake KYC, TRAI impersonation, OTP theft, job scam, lottery...)
- Shows **red flags**, psychological tricks used, and exact action to take
- Powered by **Gemini 2.5 Flash** — free tier
- Offline keyword pre-filter saves API quota

</td>
<td width="50%">

### 🖼️ Deepfake / AI Image Detector
- Detects AI-generated images & face-swap deepfakes
- **14-point forensic analysis**: skin texture, eye reflections, lighting, edge artifacts...
- Separate probability scores: Real / AI-Generated / Deepfake
- Identifies likely AI tool used (Midjourney, DALL-E, Stable Diffusion...)
- Multimodal Gemini API — image + forensic prompt

</td>
</tr>
<tr>
<td width="50%">

### 🔗 3-Layer URL Scanner
- **Layer 1:** Google Safe Browsing (instant, no quota)
- **Layer 2:** VirusTotal — 70+ antivirus engines
- **Layer 3:** Gemini structural phishing analysis
- Detects typosquatting, IP hostnames, brand impersonation
- Combined verdict: flag if **any** layer raises concern

</td>
<td width="50%">

### 🔑 Password Guard
- **Strength Analyser** — real-time score 0–100, offline
- **Breach Checker** — HaveIBeenPwned k-anonymity
- Password **never leaves your device** (SHA-1, 5-char prefix only)
- Top-500 common password blocklist
- Time-to-crack estimate

</td>
</tr>
<tr>
<td width="50%">

### 📱 App Permission Analyser
- Scans all installed apps for **dangerous permissions**
- Flags READ_SMS, RECORD_AUDIO, ACCESS_FINE_LOCATION, CAMERA...
- Apps with 3+ dangerous permissions → **HIGH RISK** badge
- 100% offline — uses Android PackageManager

</td>
<td width="50%">

### 📡 WiFi Safety Checker
- Detects open/unsecured networks
- Flags suspicious SSIDs ("Free WiFi", "Public", "Guest"...)
- Warns before banking on unsafe networks
- No API needed — native WifiManager

</td>
</tr>
</table>

**Plus:**
- 🚨 **Rotating Emergency Alert Bar** — current India scam alerts
- 📋 **Scan History** — SQLite, offline, filterable, swipe-to-delete
- 📞 **Emergency Contacts** — 1930 Helpline, cybercrime.gov.in, CERT-In (one-tap dial)
- 🌙 **Dark / Light Mode** — Material Design 3
- 📷 **QR Code Scanner** — scan → auto URL safety check

---

## 📸 Screenshots

<div align="center">

| Splash | Login | Home Dashboard |
|:---:|:---:|:---:|
| ![Splash](CyberSafe%20Screenshot/splash.jpg) | ![Login](CyberSafe%20Screenshot/login.jpg) | ![Home](CyberSafe%20Screenshot/home.jpg) |

| AI Text Scanner | Deepfake Result | URL Scanner |
|:---:|:---:|:---:|
| ![Scanner](CyberSafe%20Screenshot/text.jpg) | ![Deepfake](CyberSafe%20Screenshot/deepfake.jpg) | ![URL](CyberSafe%20Screenshot/url.jpg) |

| Password Guard | History | Contact / Helplines |
|:---:|:---:|:---:|
| ![Password](CyberSafe%20Screenshot/password.jpg) | ![History](CyberSafe%20Screenshot/memory.jpg) | ![Contact](CyberSafe%20Screenshot/contact.jpg) |

</div>

---

## 🏗️ Architecture

PhishGuard follows **MVVM (Model-View-ViewModel)** — Google's recommended Android architecture.

```
┌─────────────────────────────────────────────────────────┐
│                     VIEW LAYER (UI)                     │
│   SplashActivity · LoginActivity · MainActivity         │
│   HomeFragment · HistoryFragment · ContactFragment      │
│   SettingsFragment · ResultBottomSheet · QuickScan FAB  │
└───────────────────────┬─────────────────────────────────┘
                        │  observes LiveData / StateFlow
                        ▼
┌─────────────────────────────────────────────────────────┐
│                   VIEWMODEL LAYER                       │
│   HomeViewModel · HistoryViewModel · SettingsViewModel  │
│   Kotlin Coroutines (Dispatchers.IO) · QuotaManager     │
└───────────────────────┬─────────────────────────────────┘
                        │  calls repository
                        ▼
┌──────────────────────────────────────┬──────────────────┐
│         NETWORK / REMOTE             │  LOCAL / STORAGE  │
│  GeminiHelper (text + image)         │  SQLite Database  │
│  SafeBrowsingHelper (Google)         │  ScanResult model │
│  VirusTotalHelper (Retrofit)         │  EncryptedPrefs   │
│  HibpHelper (k-anonymity)            │  ThreatLevel enum │
└──────────────────────────────────────┴──────────────────┘
```

### 📁 Project Structure

```
com.uri.phishguard/
├── SplashActivity.kt
├── LoginActivity.kt
├── MainActivity.kt
│
├── model/
│   ├── ScanResult.kt           # Data class for all scan results
│   └── ThreatLevel.kt          # Enum: SAFE / SUSPICIOUS / DANGEROUS
│
├── db/
│   └── DatabaseHelper.kt       # SQLite CRUD (scan history)
│
├── network/
│   ├── GeminiHelper.kt         # Gemini 2.5 Flash (text + image)
│   ├── SafeBrowsingHelper.kt   # Google Safe Browsing API
│   ├── VirusTotalHelper.kt     # VirusTotal API v3 (Retrofit)
│   └── HibpHelper.kt           # HaveIBeenPwned k-anonymity
│
├── ui/
│   ├── HomeFragment.kt         # 4-Grid dashboard + FAB
│   ├── HistoryFragment.kt      # Scan history (RecyclerView)
│   ├── ContactFragment.kt      # Emergency helplines
│   └── SettingsFragment.kt     # Dark mode, API keys, privacy tools
│
└── utils/
    ├── ResultBottomSheet.kt    # Reusable scan result dialog
    ├── QuotaManager.kt         # Daily API quota tracking + auto-fallback
    ├── EncryptedPrefs.kt       # Secure API key storage
    └── NetworkUtils.kt         # Connectivity helper
```

---

## 🛠️ Tech Stack

| Category | Technology | Free? |
|---|---|:---:|
| Language | Kotlin 2.0+ | ✅ |
| IDE | Android Studio | ✅ |
| AI Engine (Primary) | Gemini 2.5 Flash API | ✅ 250 req/day |
| AI Engine (Fallback) | Gemini 2.5 Flash-Lite | ✅ 1000 req/day |
| Authentication | Firebase Auth (Spark Plan) | ✅ |
| Local Database | SQLite (Android built-in) | ✅ |
| URL Safety | SafetyNet Safe Browsing | ✅ No quota |
| Threat Intelligence | VirusTotal API v3 | ✅ 500 req/day |
| Breach Check | HaveIBeenPwned API v3 | ✅ |
| QR Scanning | ZXing Android Embedded | ✅ |
| Animations | Lottie | ✅ |
| HTTP Client | Retrofit 2 + OkHttp 4 | ✅ |
| Image Loading | Glide 4 | ✅ |
| Navigation | Android Navigation Component | ✅ |
| UI Framework | Material Design 3 | ✅ |
| Security | EncryptedSharedPreferences | ✅ |

---

## ⚙️ Setup & Installation

### Prerequisites
- Android Studio **Hedgehog** or newer
- Android device / emulator running **Android 8.0+ (API 26+)**
- A free **Google AI Studio** account → [aistudio.google.com](https://aistudio.google.com)

### Step 1 — Clone the repo
```bash
git clone https://github.com/harsh9105/PhishGuard.git
cd PhishGuard
```

### Step 2 — Add your Gemini API key
Create or open `local.properties` in the project root and add:
```properties
GEMINI_API_KEY=your_gemini_api_key_here
```
> 🔑 Get your **free** Gemini API key at [aistudio.google.com/app/apikey](https://aistudio.google.com/app/apikey)

### Step 3 — Connect Firebase
1. Go to [console.firebase.google.com](https://console.firebase.google.com)
2. Create a new project → Add Android app → package: `com.uri.phishguard`
3. Download `google-services.json` and place it in `app/`
4. Enable **Email/Password** sign-in in Firebase Authentication

### Step 4 — Build & Run
```bash
# Open in Android Studio and click ▶️ Run
# OR build APK via:
./gradlew assembleDebug
```

### Step 5 — (Optional) Add VirusTotal API Key
1. Create a free account at [virustotal.com](https://www.virustotal.com)
2. Go to your profile → API Key
3. Open **PhishGuard → Settings** → paste your key

> ⚡ The app works without a VirusTotal key — it just skips Layer 2 URL scanning.

---

## 🔑 API Keys Summary

| API | Where to Get | Cost |
|---|---|---|
| **Gemini 2.5 Flash** | [aistudio.google.com](https://aistudio.google.com) | 🆓 Free |
| **VirusTotal** | [virustotal.com](https://www.virustotal.com) | 🆓 Free |
| **Google Safe Browsing** | Built into Play Services | 🆓 No key needed |
| **HaveIBeenPwned** | [haveibeenpwned.com](https://haveibeenpwned.com) | 🆓 No key needed |
| **Firebase Auth** | [console.firebase.google.com](https://console.firebase.google.com) | 🆓 Free Spark Plan |

---

## 🔐 Privacy & Security

PhishGuard is built **privacy-first**:

- 🔒 **API keys** stored in `EncryptedSharedPreferences` — never in plain text
- 🔒 **Passwords** are SHA-1 hashed locally — only the first 5 characters of the hash are ever sent over the network (k-anonymity model)
- 🔒 **No user data** is sent to third-party servers beyond the specific, necessary API calls
- 🔒 **HTTPS enforced** for all network connections (`android:usesCleartextTraffic="false"`)
- 🔒 **No ads, no tracking, no analytics**
- 🔒 **Scan history** stored only locally in SQLite — never uploaded

---

## 🗺️ Roadmap

- [x] AI Scam Text Scanner (Gemini 2.5 Flash)
- [x] Deepfake / AI Image Detector (Multimodal)
- [x] 3-Layer URL Scanner
- [x] Password Guard + Breach Check
- [x] App Permission Analyser
- [x] WiFi Safety Checker
- [x] Scan History (SQLite)
- [x] Emergency Helplines (1930, CERT-In)
- [ ] 🔜 Video deepfake detection
- [ ] 🔜 Multilingual support (Hindi, Punjabi, Bengali)
- [ ] 🔜 Background SMS scan (Android Work Profile)
- [ ] 🔜 Community scam reporting database
- [ ] 🔜 WhatsApp share target integration
- [ ] 🔜 Play Store release

---

## 🤝 Contributing

Contributions are what make open source amazing. Any contribution you make is **greatly appreciated**.

1. **Fork** the repository
2. Create your feature branch: `git checkout -b feature/AmazingFeature`
3. Commit your changes: `git commit -m 'Add AmazingFeature'`
4. Push to the branch: `git push origin feature/AmazingFeature`
5. Open a **Pull Request**

### 💡 Good first issues to work on
- Add more Indian scam keywords to the offline pre-filter
- Translate UI strings to Hindi / regional languages
- Add more Cyber Tips content
- Write unit tests for `GeminiHelper` and `DatabaseHelper`
- Improve the UI wireframe for tablets

---

## 📊 Cyber Stats That Motivated This

| Stat | Source |
|---|---|
| 1.5M+ cybercrime complaints in India (2023) | I4C, Ministry of Home Affairs |
| 550% increase in deepfake incidents (2020–2024) | Coalition Against Online Violence |
| ₹11,269 crore lost to cyber fraud in India (2023) | RBI Annual Report |
| 1930 Helpline receives thousands of calls daily | CERT-In |
| Most victims had no tool to verify before clicking | This app solves that ⬆️ |

---

## 📞 Cybercrime Helplines (India)

| Helpline | Number / Link |
|---|---|
| 🚨 National Cybercrime Helpline | **1930** |
| 🌐 Report Cybercrime Online | [cybercrime.gov.in](https://cybercrime.gov.in) |
| 📧 CERT-In Incident Reporting | report@cert-in.org.in |
| 📱 Forward Scam SMS | **7726** (works on all carriers) |

---

## 📄 License

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for more information.

---

## 👨‍💻 Author

**Aman Khurshid** — Computer Science Engineering Student, LPU Punjab

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://linkedin.com/in/your-profile)
[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/harsh9105)
[![Email](https://img.shields.io/badge/Email-D14836?style=for-the-badge&logo=gmail&logoColor=white)](mailto:your@email.com)

---

<div align="center">

**If PhishGuard helped you, please ⭐ star this repo — it helps others find it!**

*Built with ❤️ for a safer internet. Free forever.*

![Made in India](https://img.shields.io/badge/Made%20in-India%20🇮🇳-orange?style=for-the-badge)
![Open Source](https://img.shields.io/badge/Open%20Source-❤️-red?style=for-the-badge)
![Free Forever](https://img.shields.io/badge/Free-Forever-brightgreen?style=for-the-badge)

</div>
