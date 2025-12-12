package com.example.nfc_hce_demo_fix

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log

class MyHostApduService : HostApduService() {
    private val TAG = "MyHCE"

    // ======= Sesuaikan payload di sini =========
    // Paling kompatibel untuk banyak reader: kirim ASCII + newline
    private val payload = "3375775959\n".toByteArray(Charsets.US_ASCII)

    // Jika reader butuh raw bytes / BCD, ganti payload menjadi byteArrayOf(...)
    // private val payload = byteArrayOf(0x33, 0x75, 0x77, 0x59, 0x59)

    // Status word (SW1 SW2)
    private val STATUS_OK = byteArrayOf(0x90.toByte(), 0x00.toByte())

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        Log.d(TAG, "processCommandApdu() command: ${commandApdu?.joinToString(",")}")

        // Jika command null, kembalikan STATUS_OK (atau payload jika ingin)
        if (commandApdu == null) {
            return STATUS_OK
        }

        // Balas payload + status OK
        val response = payload + STATUS_OK
        Log.d(TAG, "Sending response (len=${response.size}): ${response.joinToString(",")}")
        return response
    }

    override fun onDeactivated(reason: Int) {
        Log.d(TAG, "HCE deactivated, reason=$reason")
    }
}
C