package com.example.campusnestapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPref = getSharedPreferences("CampusNestPrefs", Context.MODE_PRIVATE)
        if (sharedPref.getBoolean("IS_DARK_MODE", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progressBar = findViewById(R.id.progressBar)
        val tvGoToRegister = findViewById<TextView>(R.id.tvGoToRegister)

        if (sharedPref.contains("API_TOKEN")) {
            goToMainActivity()
            return
        }

        tvGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
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

    @SuppressLint("SetTextI18n")
    private fun loginUser(email: String, password: String) {
        progressBar.visibility = View.VISIBLE
        btnLogin.isEnabled = false
        btnLogin.text = "Logging in..."

        lifecycleScope.launch {
            try {
                val request = LoginRequest(email, password)
                val response = RetrofitClient.apiService.login(request)

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body?.status == "success") {
                        // SUCCESSFUL LOGIN
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
                        // FAILED LOGIN (Wrong Password)
                        // This reads the actual error message from PHP!
                        val errorMsg = body?.message ?: "Invalid email or password"
                        Toast.makeText(this@LoginActivity, errorMsg, Toast.LENGTH_LONG).show()
                    }
                } else {
                    // SERVER ERROR (500, 404, etc)
                    val errorBody = response.errorBody()?.string() ?: "Server Error: ${response.code()}"
                    Log.e("API_ERROR", errorBody)
                    Toast.makeText(this@LoginActivity, "Server Error", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Log.e("API_EXCEPTION", e.message.toString(), e)
                Toast.makeText(this@LoginActivity, "Network Error: ${e.message}", Toast.LENGTH_LONG).show()
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