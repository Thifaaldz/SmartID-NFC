# NFC HCE Demo - Flutter Android Application

## 📋 Table of Contents
- [Overview](#overview)
- [System Architecture](#system-architecture)
- [Project Structure](#project-structure)
- [Technology Stack](#technology-stack)
- [How It Works](#how-it-works)
- [Key Components](#key-components)
- [NFC Communication Flow](#nfc-communication-flow)
- [APDU Protocol](#apdu-protocol)
- [Configuration](#configuration)
- [Supported Readers](#supported-readers)
- [Limitations](#limitations)
- [Development Setup](#development-setup)
- [Building & Deployment](#building--deployment)

---

## 🎯 Overview

**NFC HCE Demo** adalah aplikasi Android berbasis Flutter yang mengimplementasikan teknologi **Host Card Emulation (HCE)** untuk mengirim data NFC statis melalui protokol APDU (Application Protocol Data Unit).

Aplikasi ini memungkinkan perangkat Android untuk berinteraksi dengan NFC reader eksternal dengan mengirimkan payload data yang dapat dikonfigurasi, cocok untuk integrasi dengan sistem attendance, access control, dan payment terminals.

---

## 🏗 System Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         SYSTEM ARCHITECTURE                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                     FLUTTER LAYER (UI)                       │   │
│   │  ┌───────────────────────────────────────────────────────┐  │   │
│   │  │                  lib/main.dart                        │  │   │
│   │  │  ┌─────────────────────────────────────────────────┐  │  │   │
│   │  │  │           MaterialApp Widget Tree               │  │  │   │
│   │  │  │  ┌─────────────┐  ┌───────────────────────────┐  │  │   │
│   │  │  │  │   MyApp     │  │       Scaffold            │  │  │   │
│   │  │  │  │ (Stateless) │  │  ┌─────────────────────┐  │  │  │   │
│   │  │  │  └─────────────┘  │  │      AppBar         │  │  │  │   │
│   │  │  │                   │  │  ┌─────────────────┐│  │  │  │   │
│   │  │  │                   │  │  │     Body        ││  │  │  │   │
│   │  │  │                   │  │  │  ┌───────────┐  ││  │  │  │   │
│   │  │  │                   │  │  │  │   Text    │  ││  │  │  │   │
│   │  │  │                   │  │  │  └───────────┘  ││  │  │  │   │
│   │  │  │                   │  │  └─────────────────┘│  │  │  │   │
│   │  │  │                   │  └─────────────────────┘  │  │   │
│   │  │  └─────────────────────────────────────────────────┘  │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                              │                                        │
│                              ▼                                        │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                  ANDROID NATIVE LAYER                        │   │
│   │  ┌───────────────────────────────────────────────────────┐  │   │
│   │  │              MainActivity.kt                          │  │   │
│   │  │         (FlutterActivity Extension)                   │  │   │
│   │  └───────────────────────────────────────────────────────┘  │   │
│   │  ┌───────────────────────────────────────────────────────┐  │   │
│   │  │            MyHostApduService.kt                       │  │   │
│   │  │  ┌─────────────────────────────────────────────────┐  │  │   │
│   │  │  │              HostApduService                    │  │  │   │
│   │  │  │  ┌───────────────────────────────────────────┐  │  │  │   │
│   │  │  │  │  processCommandApdu()                     │  │  │  │   │
│   │  │  │  │  ┌─────────────────────────────────────┐  │  │  │  │   │
│   │  │  │  │  │  1. Receive SELECT APDU command     │  │  │  │  │   │
│   │  │  │  │  │  2. Log incoming command            │  │  │  │  │   │
│   │  │  │  │  │  3. Prepare payload response        │  │  │  │  │   │
│   │  │  │  │  │  4. Send payload + STATUS_OK (90 00)│  │  │  │  │   │
│   │  │  │  │  └─────────────────────────────────────┘  │  │  │  │   │
│   │  │  │  │                                           │  │  │  │   │
│   │  │  │  │  Variables:                               │  │  │  │   │
│   │  │  │  │  - payload: ByteArray (configurable)      │  │  │  │   │
│   │  │  │  │  - STATUS_OK: 0x9000                      │  │  │  │   │
│   │  │  │  │  - TAG: "MyHCE" (logging)                 │  │  │  │   │
│   │  │  │  └───────────────────────────────────────────┘  │  │  │   │
│   │  │  │                                               │  │  │   │
│   │  │  │  Callbacks:                                   │  │  │   │
│   │  │  │  - processCommandApdu()                       │  │  │   │
│   │  │  │  - onDeactivated()                            │  │  │   │
│   │  │  └─────────────────────────────────────────────────┘  │  │   │
│   │  └───────────────────────────────────────────────────────┘  │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                              │                                        │
│                              ▼                                        │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                   ANDROID SYSTEM LAYER                       │   │
│   │  ┌───────────────────────────────────────────────────────┐  │   │
│   │  │           NFC Controller (Hardware)                   │  │   │
│   │  │  ┌─────────────────────────────────────────────────┐  │  │   │
│   │  │  │              ISO 14443-4 Stack                   │  │  │   │
│   │  │  │  ┌─────────────┐  ┌───────────────────────────┐  │  │  │   │
│   │  │  │  │   Reader    │  │      PICC (Android)       │  │  │  │   │
│   │  │  │  │   (Initiator│  │      (Target)             │  │  │  │   │
│   │  │  │  │   - Polling)│  │      - HCE Service)       │  │  │  │   │
│   │  │  │  └─────────────┘  └───────────────────────────┘  │  │  │   │
│   │  │  └─────────────────────────────────────────────────┘  │  │   │
│   │  └───────────────────────────────────────────────────────┘  │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                              │                                        │
│                              ▼                                        │
│   ┌─────────────────────────────────────────────────────────────┐   │
│   │                   EXTERNAL READER                            │   │
│   │  ┌───────────────────────────────────────────────────────┐  │   │
│   │  │  ACR122U | Omnikey | ACR1252 | Identiv SCR3310       │  │   │
│   │  │  ┌─────────────────────────────────────────────────┐  │  │   │
│   │  │  │  1. NFC Polling (ISO 14443-4)                   │  │  │   │
│   │  │  │  2. Send SELECT APDU command                    │  │  │   │
│   │  │  │  3. Receive HCE response (payload + 90 00)      │  │  │   │
│   │  │  │  4. Process/Display data                        │  │  │   │
│   │  │  └─────────────────────────────────────────────────┘  │  │   │
│   │  └───────────────────────────────────────────────────────┘  │   │
│   └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 📂 Project Structure

```
nfc_hce_demo_fix/
├── lib/
│   └── main.dart                    # Flutter UI entry point
├── android/
│   ├── app/
│   │   ├── build.gradle.kts         # Android build configuration
│   │   └── src/main/
│   │       ├── AndroidManifest.xml  # App manifest with HCE service
│   │       ├── kotlin/
│   │       │   └── com/example/nfc_hce_demo_fix/
│   │       │       ├── MainActivity.kt          # Flutter activity
│   │       │       └── MyHostApduService.kt     # HCE service
│   │       └── res/
│   │           └── xml/
│   │               └── apduservice.xml  # AID configuration
├── pubspec.yaml                     # Flutter dependencies
├── analysis_options.yaml            # Dart linting rules
└── README.md                        # This documentation
```

---

## 🛠 Technology Stack

| Layer | Technology | Description |
|-------|------------|-------------|
| **Frontend** | Flutter 3.10+ | Cross-platform UI framework |
| **Language** | Dart | Flutter programming language |
| **Backend** | Android Native | Kotlin for HCE service |
| **Protocol** | APDU | Application Protocol Data Unit |
| **NFC Standard** | ISO 14443-4 | Contactless communication |
| **HCE** | Host Card Emulation | Software-based card emulation |

---

## 🔄 How It Works

### High-Level Flow

```
User Input          Android System           NFC Reader
    │                    │                      │
    │  1. Open App       │                      │
    │───────────────────>│                      │
    │                    │                      │
    │  2. NFC Tap        │                      │
    │<───────────────────│  3. Polling          │
    │                    │<─────────────────────│
    │                    │  4. SELECT APDU      │
    │                    │<─────────────────────│
    │                    │  5. Process APDU     │
    │                    │─────────┐            │
    │                    │         │            │
    │                    │<────────┘            │
    │                    │  6. Send Response    │
    │                    │  (payload + 90 00)   │
    │                    │─────────────────────>│
    │                    │                      │  7. Display Data
    │                    │                      │
```

---

## 🧩 Key Components

### 1. Flutter UI (`lib/main.dart`)

**Purpose**: Provides simple user interface with instructions

**Components**:
- `MyApp`: Root MaterialApp widget
- `Scaffold`: Basic app structure
- `AppBar`: Title bar showing "NFC HCE Demo"
- `Text`: Instruction text for users

**Code Location**: `/root/nfc_hce_demo_fix/lib/main.dart`

### 2. Host Apdu Service (`MyHostApduService.kt`)

**Purpose**: Handles NFC card emulation and APDU command processing

**Key Methods**:

| Method | Return Type | Description |
|--------|-------------|-------------|
| `processCommandApdu(commandApdu, extras)` | `ByteArray` | Processes incoming SELECT APDU and returns response |
| `onDeactivated(reason)` | `void` | Handles service deactivation |

**Key Variables**:

| Variable | Type | Description |
|----------|------|-------------|
| `payload` | `ByteArray` | Static data to send (default: `"3375775959\n"`) |
| `STATUS_OK` | `ByteArray` | Success status word `0x9000` |
| `TAG` | String | Log tag for debugging ("MyHCE") |

**Code Location**: `/root/nfc_hce_demo_fix/android/app/src/main/kotlin/com/example/nfc_hce_demo_fix/MyHostApduService.kt`

### 3. Android Manifest (`AndroidManifest.xml`)

**Purpose**: Declares NFC permissions, features, and HCE service registration

**Key Declarations**:

```xml
<!-- NFC Permissions -->
<uses-permission android:name="android.permission.NFC" />
<uses-feature android:name="android.hardware.nfc" android:required="true" />
<uses-feature android:name="android.hardware.nfc.hce" android:required="true" />

<!-- HCE Service -->
<service
    android:name=".MyHostApduService"
    android:exported="true"
    android:permission="android.permission.BIND_NFC_SERVICE">
    <intent-filter>
        <action android:name="android.nfc.cardemulation.action.HOST_APDU_SERVICE"/>
    </intent-filter>
    <meta-data
        android:name="android.nfc.cardemulation.host_apdu_service"
        android:resource="@xml/apduservice"/>
</service>
```

**Code Location**: `/root/nfc_hce_demo_fix/android/app/src/main/AndroidManifest.xml`

### 4. AID Configuration (`apduservice.xml`)

**Purpose**: Defines the AID (Application Identifier) for routing NFC commands

**Configuration**:
```xml
<host-apdu-service 
    android:description="@string/app_name"
    android:requireDeviceUnlock="false">
    
    <aid-group android:category="payment" android:description="@string/app_name">
        <aid-filter android:name="A0000002471001" />
    </aid-group>
</host-apdu-service>
```

**Parameters**:
| Parameter | Value | Description |
|-----------|-------|-------------|
| `android:description` | App name | Service description |
| `android:requireDeviceUnlock` | false | Allows HCE without unlocking |
| `aid-filter android:name` | A0000002471001 | Standard payment AID |

**Code Location**: `/root/nfc_hce_demo_fix/android/app/src/main/res/xml/apduservice.xml`

---

## 📡 NFC Communication Flow

### Step-by-Step Process

```
┌─────────────────────────────────────────────────────────────────────┐
│                    NFC COMMUNICATION SEQUENCE                        │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  PHASE 1: DISCOVERY & SELECTION                                      │
│  ─────────────────────────────                                       │
│                                                                      │
│  Reader                         Android HCE                          │
│    │                               │                                 │
│    │  1. RF Field On (13.56 MHz)   │                                 │
│    │──────────────────────────────>│                                 │
│    │                               │                                 │
│    │  2. Polling Commands          │                                 │
│    │  (REQA / WUPA)                │                                 │
│    │──────────────────────────────>│                                 │
│    │                               │                                 │
│    │  3. Answer to Select (ATS)    │                                 │
│    │<──────────────────────────────│                                 │
│    │                               │                                 │
│    │  4. SELECT PPSE (Proximity    │                                 │
│    │     Payment System Environment)                                │
│    │──────────────────────────────>│                                 │
│    │                               │                                 │
│    │  5. Process PPSE              │                                 │
│    │<──────────────────────────────│                                 │
│    │                               │                                 │
│                                                                      │
│  PHASE 2: AID SELECTION                                              │
│  ───────────────────────                                             │
│                                                                      │
│  Reader                         Android HCE                          │
│    │                               │                                 │
│    │  6. SELECT AID (A0000002471001)│                                │
│    │──────────────────────────────>│                                 │
│    │    Command: 00 A4 04 00 0E    │                                 │
│    │           A0000002471001      │                                 │
│    │                               │                                 │
│    │  7. MyHostApduService         │                                 │
│    │     processCommandApdu()      │                                 │
│    │     receives command          │                                 │
│    │                               │                                 │
│    │  8. Returns Response          │                                 │
│    │<──────────────────────────────│                                 │
│    │    payload + 90 00            │                                 │
│    │                               │                                 │
│                                                                      │
│  PHASE 3: DATA EXCHANGE (Optional)                                   │
│  ─────────────────────────────────                                   │
│                                                                      │
│  Reader                         Android HCE                          │
│    │                               │                                 │
│    │  9. READ RECORD / GET DATA    │                                 │
│    │──────────────────────────────>│                                 │
│    │                               │                                 │
│    │ 10. Return More Data          │                                 │
│    │<──────────────────────────────│                                 │
│    │                               │                                 │
│                                                                      │
│  PHASE 4: DEACTIVATION                                               │
│  ───────────────────────                                             │
│                                                                      │
│  Reader                         Android HCE                          │
│    │                               │                                 │
│    │ 11. RF Field Off / Deselect   │                                 │
│    │──────────────────────────────>│                                 │
│    │                               │                                 │
│    │ 12. onDeactivated() called    │                                 │
│    │                               │                                 │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 📜 APDU Protocol

### APDU Command Structure

```
┌─────────────────────────────────────────────────────────────────────┐
│                     APDU COMMAND FRAME                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌──────┬──────┬──────┬──────────┬──────────┬─────────┐            │
│  │ CLA  │ INS  │  P1  │    P2    │  Lc/Data │   Le   │            │
│  │ (1B) │ (1B) │ (1B) │   (1B)   │  (Var)   │ (0-2B) │            │
│  └──────┴──────┴──────┴──────────┴──────────┴─────────┘            │
│                                                                      │
│  Example: SELECT AID Command                                         │
│  ─────────────────────────────                                       │
│  CLA  = 00 (ISO 7816-4 class)                                       │
│  INS  = A4 (SELECT command)                                         │
│  P1   = 04 (Select by AID)                                          │
│  P2   = 00 (First or only occurrence)                               │
│  Lc   = 0E (14 bytes of AID)                                        │
│  Data = A0000002471001 (PayPass AID)                                │
│  Le   = 00 (Expect maximum response)                                │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### APDU Response Structure

```
┌─────────────────────────────────────────────────────────────────────┐
│                     APDU RESPONSE FRAME                              │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌──────────────┬──────────┐                                        │
│  │ Response Data│  SW1 SW2 │                                        │
│  │    (Var)     │  (2B)    │                                        │
│  └──────────────┴──────────┘                                        │
│                                                                      │
│  Status Words (SW1 SW2):                                             │
│  ─────────────────────────                                           │
│  90 00 = Success                                                     │
│  61 XX = Success with XX bytes available                             │
│  6A 82 = File not found                                              │
│  6A 86 = Incorrect parameters                                        │
│                                                                      │
│  Example Response from this App:                                     │
│  ─────────────────────────────────                                   │
│  Data   = 33 37 35 37 37 35 39 35 39 0A  ("3375775959\n")           │
│  SW1 SW2 = 90 00 (OK)                                                │
│                                                                      │
│  Full Hex Response:                                                  │
│  33 37 35 37 37 35 39 35 39 0A 90 00                                 │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### Payload Customization

**Current Implementation** (`MyHostApduService.kt`):

```kotlin
// Option 1: ASCII format (recommended for most readers)
private val payload = "3375775959\n".toByteArray(Charsets.US_ASCII)

// Option 2: Raw BCD/Hex format
// private val payload = byteArrayOf(0x33, 0x75, 0x77, 0x59, 0x59)

// Response is always: payload + STATUS_OK (90 00)
```

---

## ⚙ Configuration

### Modifying the AID

To change the AID for your reader, edit `/root/nfc_hce_demo_fix/android/app/src/main/res/xml/apduservice.xml`:

```xml
<aid-filter android:name="YOUR_AID_HERE" />
```

### Common AIDs

| Service | AID | Purpose |
|---------|-----|---------|
| PayPass | A0000000041010 | Mastercard PayPass |
| PayWave | A0000000031010 | Visa PayWave |
| ExpressPay | A0000000044020 | American Express |
| Discover | A0000003241010 | Discover |
| Custom | YOUR_AID | Application specific |

### Modifying Payload Data

Edit `/root/nfc_hce_demo_fix/android/app/src/main/kotlin/com/example/nfc_hce_demo_fix/MyHostApduService.kt`:

```kotlin
// For ASCII payload
private val payload = "YOUR_CODE_HERE\n".toByteArray(Charsets.US_ASCII)

// For Hex/Binary payload
private val payload = byteArrayOf(0x01, 0x02, 0x03, 0x04)
```

---

## 🔳 Supported Readers

### ✅ Fully Compatible Readers (APDU Support)

These readers support ISO-14443-4 and can read HCE payload:

| Reader | Model | Interface | Notes |
|--------|-------|-----------|-------|
| **ACS** | ACR122U | USB PC/SC | Most popular, excellent compatibility |
| **ACS** | ACR1252U | USB/BT | Bluetooth option available |
| **ACS** | ACR1255 | Bluetooth | Mobile-friendly |
| **HID** | Omnikey 5022 | USB PC/SC | Enterprise grade |
| **HID** | Omnikey 5427 | USB PC/SC | Multi-protocol |
| **Identiv** | SCR3310v2 | USB PC/SC | With NFC module |
| **ACS** | ACR1281 | USB | Dual interface |
| **ACS** | ACR1283 | USB | Contactless only |

### ❌ Incompatible Readers (UID Only)

These readers only read Mifare UID, not APDU payload:

| Reader | Limitation |
|--------|------------|
| Cardteck R201MF | Reads UID only |
| Generic RFID Mifare UID Reader | Reads UID only |
| Simple 125kHz readers | Wrong frequency |
| Most Chinese UID cloners | No ISO-DEP support |

---

## ⚠ Limitations

### Important Constraints

1. **Android Cannot Change UID**
   - Device UID is hardware-baked and immutable
   - HCE only emulates application data, not UID

2. **Reader Compatibility Required**
   - Reader must support ISO-DEP (ISO 14443-4)
   - Reader must support APDU exchange
   - UID-only readers will NOT work

3. **Screen Must Be On**
   - NFC is disabled when screen is locked (unless configured otherwise)
   - Set `android:requireDeviceUnlock="false"` for unlocked access

4. **No Background HCE**
   - App must be in foreground or service must be running
   - Some devices have power-saving restrictions

5. **AID Routing**
   - Only one HCE service can claim an AID
   - Multiple payment apps may conflict

---

## 🛠 Development Setup

### Prerequisites

```
Flutter SDK: 3.10.0 or higher
Dart SDK: 3.0.0 or higher
Android Studio: Hedgehog or higher
Android SDK: API 24+ (Android 7.0)
Gradle: 8.0+
Java: JDK 17
```

### Installation Steps

1. **Clone the repository**
   ```bash
   cd /root/nfc_hce_demo_fix
   ```

2. **Install dependencies**
   ```bash
   flutter pub get
   ```

3. **Run on Android device/emulator**
   ```bash
   flutter run
   ```

### Testing Setup

```
┌─────────────────────────────────────────────────────────────────────┐
│                     TEST ENVIRONMENT                                 │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│   ┌─────────────────┐        ┌─────────────────┐                    │
│   │  Development    │        │   Test Reader   │                    │
│   │  Machine        │        │   (ACR122U)     │                    │
│   │                 │        │                 │                    │
│   │  Flutter App    │        │  ┌───────────┐  │                    │
│   │  on Android     │───────>│  │ USB/PC/SC │  │                    │
│   │                 │  NFC   │  └───────────┘  │                    │
│   └─────────────────┘        └─────────────────┘                    │
│          │                            │                              │
│          │                            ▼                              │
│          │                   ┌─────────────────┐                    │
│          │                   │   PC Software   │                    │
│          │                   │  (PuTTY/minicom)│                    │
│          │                   │  or Custom App  │                    │
│          │                   └─────────────────┘                    │
│          │                                                      │
│          │                                                      │
│          ▼                                                      │
│   ┌─────────────────────────────────────────────────────────┐     │
│   │                    LOGCAT OUTPUT                         │     │
│   │  D/MyHCE: processCommandApdu() command: 0,0,174,4,0,14  │     │
│   │  D/MyHCE: Sending response (len=13): 51,55,53,55,53...  │     │
│   │  D/MyHCE: HCE deactivated, reason=2                     │     │
│   └─────────────────────────────────────────────────────────┘     │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 📦 Building & Deployment

### Debug Build

```bash
flutter build apk --debug
```

### Release Build

```bash
flutter build apk --release
```

### APK Location

```
build/app/outputs/flutter-apk/app-release.apk
```

### Signing (for Play Store)

1. Create signing key:
   ```bash
   keytool -genkey -v -keystore key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias nfc-hce
   ```

2. Configure signing in `android/app/build.gradle.kts`

3. Build signed APK:
   ```bash
   flutter build apk --release
   ```

---

## 📊 Data Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                     COMPLETE DATA FLOW                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  User Input               Application              HCE Service       │
│      │                        │                        │             │
│      │  "3375775959"          │                        │             │
│      │───────────────────────>│                        │             │
│      │                        │                        │             │
│      │                        │   [Static Payload]     │             │
│      │                        │   "3375775959\n"       │             │
│      │                        │───────────────────────>│             │
│      │                        │                        │             │
│      │                        │     NFC Tap Event      │             │
│      │                        │<───────────────────────│             │
│      │                        │                        │             │
│      │                        │                        │  Reader    │
│      │                        │                        │  SELECT    │
│      │                        │                        │  AID APDU  │
│      │                        │                        │<───────────│
│      │                        │                        │             │
│      │                        │     processCommandApdu │             │
│      │                        │     receives: 00A4040E │             │
│      │                        │     A0000002471001     │             │
│      │                        │<───────────────────────│             │
│      │                        │                        │             │
│      │                        │     Returns:          │             │
│      │                        │     payload + 90 00   │             │
│      │                        │───────────────────────>│             │
│      │                        │                        │             │
│      │                        │                        │  Response: │
│      │                        │                        │  33 37 35  │
│      │                        │                        │  37 37 35  │
│      │                        │                        │  39 35 39  │
│      │                        │                        │  0A 90 00  │
│      │                        │                        │───────────>│
│      │                        │                        │             │
│      │                        │                        │  Reader    │
│      │                        │                        │  Displays  │
│      │                        │                        │  "3375775959│
│      │                        │                        │             │
│      ▼                        ▼                        ▼             │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 🔧 Troubleshooting

### Common Issues

| Issue | Cause | Solution |
|-------|-------|----------|
| Reader shows UID only | No APDU support | Use compatible reader |
| No response from HCE | Screen locked | Unlock device or set `requireDeviceUnlock="false"` |
| AID not routed | Multiple HCE apps | Uninstall conflicting apps |
| Payload not received | Wrong payload format | Check ASCII vs Hex encoding |

### Log Analysis

```bash
# View HCE logs
adb logcat -s MyHCE:D

# Expected output
D/MyHCE: processCommandApdu() command: 0,0,174,4,0,14,...
D/MyHCE: Sending response (len=13): 51,55,53,55,53,...
D/MyHCE: HCE deactivated, reason=2
```

---

## 📈 Future Enhancements

- [ ] Dual-mode: UID simulation (rooted devices)
- [ ] Complete transaction logging
- [ ] Auto-detect Hex/Decimal format
- [ ] Backend API integration
- [ ] Multi-payload support
- [ ] Time-based dynamic codes
- [ ] Encrypted payload support

---

## 📄 License

This project is a demonstration application for NFC HCE technology.

---

## 👤 Author

Created for NFC HCE demonstration purposes.

---

## 📞 Support

For issues related to:
- **APDU Protocol**: Check ISO 7816-4 specification
- **HCE Service**: Android NFC Card Emulation documentation
- **Reader Compatibility**: Contact reader manufacturer

