# PrivateChat - Android App (Chat, Photos, Videos & Video/Audio Calls)

Yeh ek complete **Android Studio Project** hai jisme aap aur aapke friends:
1. **Private Chatting** kar sakte hain (End-to-End Encrypted).
2. **Photos & Videos** share kar sakte hain.
3. **HD Audio Call & Video Call** kar sakte hain (Picture-in-Picture PIP aur Camera Flip ke sath).
4. **App Privacy Lock**: 4-Digit secret PIN code lock jisse aapke bina koi app na khol sake.
5. **Built-in AI Companion Bot**: Aap aur aapke doston ke liye ek 24/7 smart AI chat assistant.

---

## 📁 Project Structure

```
PrivateChatApp/
├── app/
│   ├── build.gradle.kts           # App level build config & dependencies
│   ├── src/main/
│   │   ├── AndroidManifest.xml    # Permissions (Camera, Audio, Storage, Internet)
│   │   ├── java/com/example/privatechat/
│   │   │   ├── MainActivity.kt    # Entry point & permissions handling
│   │   │   ├── PrivateChatApp.kt  # Application base class
│   │   │   ├── data/
│   │   │   │   ├── model/         # User, Message, CallSession models
│   │   │   │   ├── repository/    # ChatRepository, WebRtcClient
│   │   │   │   └── security/      # AES-256-GCM EncryptionUtils
│   │   │   ├── ui/
│   │   │   │   ├── navigation/    # AppNavigation.kt
│   │   │   │   ├── screens/       # PinLock, Auth, ChatList, ChatDetail, CallScreen
│   │   │   │   └── theme/         # Modern Dark Privacy Theme
│   │   └── res/                   # Drawables, Icons, Colors, Strings
├── gradle/
│   ├── libs.versions.toml         # Version catalog
│   └── wrapper/                   # Gradle wrapper
├── build.gradle.kts               # Root build file
├── settings.gradle.kts            # Project settings
└── README.md
```

---

## 🚀 Android Studio me kaise Open aur Run karein? (Step-by-Step)

### Step 1: Android Studio Kholein
1. Apne computer me **Android Studio** open karein.
2. **Open** button par click karein.
3. Is folder path ko select karein:
   ```
   C:\Users\dell\.gemini\antigravity\scratch\PrivateChatApp
   ```
4. Android Studio automatically Gradle sync start kar dega aur sari dependencies download kar lega.

### Step 2: Friends ke liye APK File Kaise Banayein (Downloadable App)
Aapke friends is app ko download karke install kar sakein, uske liye:
1. Android Studio ke top menu me jayein:
   **`Build` -> `Build Bundle(s) / APK(s)` -> `Build APK(s)`**
2. 1-2 minute me APK generate ho jayegi.
3. Niche notification me **`locate`** par click karein.
   (Aapko `app-debug.apk` file mil jayegi).
4. Yeh APK file aap apne **WhatsApp, Telegram, Google Drive ya Bluetooth** se apne friends ko bhej sakte hain!
5. Friend apne phone me "Install" par click karke direct app use kar sakta hai.

---

## 🔒 Privacy & Features Details

1. **Private PIN Lock Screen (`PinLockScreen.kt`)**:
   - Pehli baar kholne par aap apna 4-digit secret PIN set karte hain.
   - Har baar app open karne par ya Top Bar me Lock icon dabane par app lock ho jata hai.
   - SHA-256 secure hash ke through PIN protect rehta hai.

2. **Photos & Videos Sharing (`ChatDetailScreen.kt`)**:
   - Chat room me niche **Photo (Gallery icon)** aur **Video (Camera icon)** ke buttons hain.
   - Android ke modern Media Picker se directly phone ki koi bhi photo ya video send ki ja sakti hai.

3. **Audio & Video Calling (`CallScreen.kt`)**:
   - Chat me top right corner me **Phone** (Audio call) aur **Video Camera** (Video call) ke icons hain.
   - Video call me:
     - Remote friend ki full screen video preview.
     - Corner me aapka floating selfie camera view (PIP window).
     - Front aur Back camera switch karne ka button.
     - Mic mute/unmute button.
     - Red End Call button.

4. **Multi-device Live Cloud Backend (Optional Setup)**:
   - Agar aapko globally different Wi-Fi/networks par apne doston se connect hona hai, toh `google-services.json` (Firebase Console se) download karke `app/` folder me paste kar dein aur `app/build.gradle.kts` me Firebase dependencies uncomment kar dein.

---

## 💡 Support & Notes
- Android Version: Android 7.0 (API 24) se lekar Android 14+ (API 34) tak fully compatible.
- UI Framework: Jetpack Compose with Material 3 (Fast, Smooth, Modern Dark Privacy Theme).
