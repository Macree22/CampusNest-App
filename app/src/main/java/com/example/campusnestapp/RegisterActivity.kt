package com.example.campusnestapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etFirstName = findViewById<TextInputEditText>(R.id.etRegFirstName)
        val etLastName = findViewById<TextInputEditText>(R.id.etRegLastName)
        val etEmail = findViewById<TextInputEditText>(R.id.etRegEmail)
        val etStudentNumber = findViewById<TextInputEditText>(R.id.etRegStudentNumber)
        val etPassword = findViewById<TextInputEditText>(R.id.etRegPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarReg)

        btnRegister.setOnClickListener {
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val studentNumber = etStudentNumber.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || studentNumber.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            btnRegister.isEnabled = false

            lifecycleScope.launch {
                try {
                    val request = RegisterRequest(firstName, lastName, email, password, studentNumber)
                    val response = RetrofitClient.apiService.register(request)

                    if (response.isSuccessful && response.body()?.status == "success") {
                        Toast.makeText(this@RegisterActivity, "Account Created! Please Log In.", Toast.LENGTH_LONG).show()
                        finish() // Go back to Login
                    } else {
                        Toast.makeText(this@RegisterActivity, response.body()?.message ?: "Error", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@RegisterActivity, "Network Error", Toast.LENGTH_SHORT).show()
                } finally {
                    progressBar.visibility = View.GONE
                    btnRegister.isEnabled = true
                }
            }
        }
    }
}