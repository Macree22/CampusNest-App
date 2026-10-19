package com.example.campusnestapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)

        // Check if the user is already logged in
        val sharedPref = getSharedPreferences("CampusNestPrefs", Context.MODE_PRIVATE)
        if (sharedPref.contains("API_TOKEN")) {
            goToMainActivity()
            return
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        progressBar.visibility = View.VISIBLE
        btnLogin.isEnabled = false
        btnLogin.text = "Logging in..."

        lifecycleScope.launch {
            try {
                val request = LoginRequest(email, password)
                val response = RetrofitClient.apiService.login(request)

                if (response.isSuccessful && response.body()?.status == "success") {
                    val body = response.body()!!

                    // Save the token and user info
                    val sharedPref = getSharedPreferences("CampusNestPrefs", Context.MODE_PRIVATE)
                    sharedPref.edit().apply {
                        putString("API_TOKEN", "Bearer ${body.token}")
                        putString("USER_NAME", body.name)
                        putString("USER_ROLE", body.role)
                        putInt("USER_ID", body.id ?: 0)
                        apply()
                    }

                    Toast.makeText(this@LoginActivity, "Welcome, ${body.name}", Toast.LENGTH_SHORT).show()
                    goToMainActivity()

                } else {
                    // Print the exact error from PHP to Logcat to help debug
                    val errorBody = response.errorBody()?.string() ?: "Unknown Server Error"
                    Log.e("API_ERROR", "Server Response: $errorBody")

                    // Show a shortened version on the screen
                    val shortMsg = if (errorBody.length > 50) errorBody.take(50) + "..." else errorBody
                    Toast.makeText(this@LoginActivity, "Server Error: $shortMsg", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                // Catch network failures (No internet, Ngrok offline, etc)
                Log.e("API_EXCEPTION", e.message.toString(), e)
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                progressBar.visibility = View.GONE
                btnLogin.isEnabled = true
                btnLogin.text = "Log In"
            }
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}