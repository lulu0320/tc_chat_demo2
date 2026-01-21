package com.example.chatdemo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.tencent.imsdk.v2.V2TIMCallback
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMSDKConfig
import com.tencent.imsdk.v2.V2TIMSDKListener

class LoginActivity : AppCompatActivity() {

    private lateinit var etUserId: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar

    // Hardcoded SDK App ID
    private val SDK_APP_ID = 20032706

    // Pre-configured UserSigs for different users
    // IMPORTANT: Replace these with fresh UserSigs from Tencent Console
    // UserSigs expire after some time, regenerate them if login fails
    private val userCredentials = mapOf(
        "user1" to "eJwtzM0KgkAUBeB3udtCrmMz-oCLoE0YEY3hWp1r3CIbdIwgevdMXZ7vHM4H8oP2XtRBAsJDWE*ZDbWOG5546Knzl6I399JaNpAIxECEqGant*WOIPGllGODszp*-C1UUSwjXyzbnq-ja355xhtVZKWqi93N0TmocGiCdrWVdYX6mJHO3MkaHe9T*P4AuaswTA__",
        "user2" to "eJyrVgrxCdYrSy1SslIy0jNQ0gHzM1NS80oy0zLBwqXFqUVGUInilOzEgoLMFCUrIwMDYyNzAzOIeGpFQWZRqpKVoampKVDGACJakpkLEjM3s7A0tTA0hYoWZ6YDTS1JLff1Ljc0LXDT9g7MzPPOScqvSkkOCMwF6tauDLUw9YhMSUrJcQoMTrdVqgUA0pEw*g__"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        etUserId = findViewById(R.id.etUserId)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener {
            val userId = etUserId.text.toString().trim()

            if (userId.isEmpty()) {
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Get the pre-configured UserSig for this user
            val userSig = userCredentials[userId]

            if (userSig == null) {
                Toast.makeText(this, "User '$userId' not configured. Available users: ${userCredentials.keys.joinToString(", ")}", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (userSig.startsWith("REPLACE_WITH")) {
                Toast.makeText(this, "Please update the UserSig for '$userId' in LoginActivity.kt", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            performLogin(SDK_APP_ID, userId, userSig)
        }
    }

    private fun performLogin(sdkAppId: Int, userId: String, userSig: String) {
        progressBar.visibility = View.VISIBLE
        btnLogin.isEnabled = false

        // Initialize Tencent IM SDK
        val config = V2TIMSDKConfig()
        config.logLevel = V2TIMSDKConfig.V2TIM_LOG_DEBUG

        val initSuccess = V2TIMManager.getInstance().initSDK(
            this,
            sdkAppId,
            config,
            object : V2TIMSDKListener() {
                override fun onConnecting() {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Connecting...", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onConnectSuccess() {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Connected", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onConnectFailed(code: Int, error: String?) {
                    runOnUiThread {
                        Toast.makeText(
                            this@LoginActivity,
                            "Connection failed: $error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        )

        if (!initSuccess) {
            progressBar.visibility = View.GONE
            btnLogin.isEnabled = true
            Toast.makeText(this, "SDK initialization failed", Toast.LENGTH_SHORT).show()
            return
        }

        // Login to Tencent IM
        V2TIMManager.getInstance().login(userId, userSig, object : V2TIMCallback {
            override fun onSuccess() {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true
                    Toast.makeText(this@LoginActivity, R.string.login_success, Toast.LENGTH_SHORT)
                        .show()

                    // Navigate to conversation list
                    val intent = Intent(this@LoginActivity, ConversationListActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onError(code: Int, desc: String?) {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    btnLogin.isEnabled = true
                    Toast.makeText(
                        this@LoginActivity,
                        getString(R.string.login_failed, desc),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
}
