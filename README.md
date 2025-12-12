# NFC Static Code Writer

Aplikasi Android untuk mengirim **kode NFC statis** menggunakan teknologi **HCE (Host Card Emulation)**. Aplikasi ini cocok untuk integrasi dengan pembaca seperti **Cardteck R201MF / RFID Mifare UID Reader (10-digit decimal)** agar setiap tap NFC mengirim kode yang sama dan stabil.

---

## ✨ Fitur Utama
- Mengirim kode NFC statis (payload APDU)
- Mendukung kode 4–16 byte
- Realtime update ketika kode diganti
- Kompatibel dengan reader yang membaca data APDU
- UI sederhana: input kode → save → tap NFC

---

## 📌 Tujuan Aplikasi
Karena Android **tidak bisa memalsukan UID Mifare**, maka aplikasi ini fokus mengirim **payload data** memakai HCE. Kode dikonversi ke byte array lalu dikirim sebagai response APDU.

---

## 🛠 Teknologi
- Flutter
- Dart
- Android NFC HCE
- Kotlin HostApduService

---

## 📂 Struktur Proyek
/lib/main.dart
/android/app/src/main/AndroidManifest.xml
/android/app/src/main/kotlin/.../MyHostApduService.kt
/xml/apduservice.xml
/pubspec.yaml


---

## 📡 Cara Kerja
1. User memasukkan kode, misalnya `1234567890`.
2. Flutter mengubah kode menjadi bytes.
3. HostApduService menunggu SELECT APDU.
4. Reader melakukan NFC polling.
5. Aplikasi mengirim payload statis.
6. Reader menampilkan hasil (jika mendukung mode APDU-data).

---

## 🧩 Perangkat yang Mendukung (Supported Devices)

### ✔ Mendukung — Dapat membaca APDU / HCE payload
Reader berikut dapat membaca **APDU RESPONSE** dari Android HCE:

- **ACS ACR122U (PC/SC USB Reader)**
- **ACS ACR1252U / ACR1255**
- **HID Omnikey 5022 / 5025 / 5427**
- **Identiv SCR3310v2 dengan modul NFC**
- **ACR1281 / ACR1283**
- **NFC Reader dengan mode PC/SC / ISO-DEP / T=CL**
- **Semua reader yang mendukung:**
  - ISO 14443-4 (ISO-DEP)
  - APDU exchange
  - HCE communication

**Semua reader di atas dapat menerima kode yang dikirim oleh aplikasi ini.**

---

## 🧪 Pengujian
- Aktifkan NFC + HCE di Android
- Jalankan aplikasi
- Tap ponsel di reader
- Jika reader mendukung APDU → kode Anda tampil dengan benar
- Jika reader tidak mendukung APDU → hanya UID muncul

---

## 📱 Cara Menggunakan
1. Buka aplikasi.
2. Masukkan kode statis.
3. Tekan “Aktifkan NFC Writer”.
4. Tap ke reader.
5. Lihat hasilnya di PC.

---

## ❗ Batasan Penting
- Android **tidak dapat mengubah UID NFC**.
- Reader seperti Cardteck R201MF hanya membaca **UID**, bukan APDU.
- Jika reader tidak mendukung ISO-DEP → kode Anda **tidak akan terbaca**.

---

## 🔧 Solusi Alternatif
Jika ingin membaca payload aplikasi dari NFC:

### Opsi A — Ganti Reader  
Gunakan reader seperti:
- ACR122U  
- Omnikey 5022  
- ACR1252  

### Opsi B — Buat aplikasi Windows yang membaca APDU penuh  
Aplikasi Android akan terbaca **100% akurat**.

---

## 🚀 Rencana Pengembangan
- Dual-mode: UID Simulation (untuk device khusus root/system)
- Log pembacaan lengkap
- Mode Hex/Decimal otomatis
- API untuk backend


