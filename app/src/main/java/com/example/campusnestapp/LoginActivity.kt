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
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button
    private lateinit var btnGoogleSignIn: Button
    private lateinit var progressBar: ProgressBar

    // Firebase Auth instance
    private lateinit var firebaseAuth: FirebaseAuth

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Apply Dark Mode setting BEFORE the UI loads
        val sharedPref = getSharedPreferences("CampusNestPrefs", Context.MODE_PRIVATE)
        if (sharedPref.getBoolean("IS_DARK_MODE", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 2. Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance()

        // 3. Link UI components
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn)
        progressBar = findViewById(R.id.progressBar)
        val tvGoToRegister = findViewById<TextView>(R.id.tvGoToRegister)

        // 4. Check if the user is already logged in
        if (sharedPref.contains("API_TOKEN")) {
            goToMainActivity()
            return
        }

        // 5. Handle "Register here" click
        tvGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // 6. Set login click listeners
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUserWithEmail(email, password)
            } else {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // 7. Set Google Sign-In click listener
        btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
        }
    }

    // ==========================================
    // STANDARD EMAIL/PASSWORD LOGIN (PHP API)
    // ==========================================
    @SuppressLint("SetTextI18n")
    private fun loginUserWithEmail(email: String, password: String) {
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
                        saveUserSessionAndNavigate(body.token, body.name, body.role, body.id)
                    } else {
                        Toast.makeText(this@LoginActivity, body?.message ?: "Invalid credentials", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Server Error: ${response.code()}", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Network Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                progressBar.visibility = View.GONE
                btnLogin.isEnabled = true
                btnLogin.text = "Log In"
            }
        }
    }

    // FIREBASE GOOGLE SIGN-IN (SSO)
    private fun signInWithGoogle() {
        val credentialManager = CredentialManager.create(this)
        val webClientId = "728484417746-nme7onatppdcpup7i7i0vj78k1a4f1j4.apps.googleusercontent.com"

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                // Show loading state
                progressBar.visibility = View.VISIBLE
                btnGoogleSignIn.isEnabled = false

                val result = credentialManager.getCredential(
                    request = request,
                    context = this@LoginActivity
                )

                val credential = result.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                    // Authenticate with Firebase
                    firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                } else {
                    Toast.makeText(this@LoginActivity, "Google Sign-In Failed", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    btnGoogleSignIn.isEnabled = true
                }
            } catch (e: GetCredentialException) {
                Toast.makeText(this@LoginActivity, "Sign-In Cancelled", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                btnGoogleSignIn.isEnabled = true
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                btnGoogleSignIn.isEnabled = true
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser

                    // Get the Firebase ID token to send to your PHP backend
                    user?.getIdToken(true)?.addOnCompleteListener { tokenResult ->
                        if (tokenResult.isSuccessful) {
                            val firebaseIdToken = tokenResult.result.token
                            val email = user.email ?: "No Email"
                            val name = user.displayName ?: "Student"

                            // TODO: Send `firebaseIdToken` to your PHP backend to verify and log them in.
                            // For now, we will simulate a successful login and navigate to the main screen.

                            Log.d("FIREBASE_SSO", "ID Token: $firebaseIdToken")
                            Toast.makeText(this, "Google Login Success: $name", Toast.LENGTH_LONG).show()

                            // Simulate saving token for now (Replace this when you build the PHP endpoint)
                            saveUserSessionAndNavigate(
                                token = "FIREBASE_TOKEN_PLACEHOLDER",
                                name = name,
                                role = "student",
                                id = 0
                            )
                        }
                    }
                } else {
                    Toast.makeText(this, "Firebase Authentication Failed", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    btnGoogleSignIn.isEnabled = true
                }
            }
    }

    // HELPER METHOD
    private fun saveUserSessionAndNavigate(token: String?, name: String?, role: String?, id: Int?) {
        val sharedPref = getSharedPreferences("CampusNestPrefs", Context.MODE_PRIVATE)
        sharedPref.edit().apply {
            putString("API_TOKEN", "Bearer $token")
            putString("USER_NAME", name)
            putString("USER_ROLE", role)
            putInt("USER_ID", id ?: 0)
            apply()
        }
        goToMainActivity()
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}