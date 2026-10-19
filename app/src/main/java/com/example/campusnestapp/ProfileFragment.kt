package com.example.campusnestapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var sharedPref: android.content.SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = requireActivity().getSharedPreferences("CampusNestPrefs", Context.MODE_PRIVATE)

        val tvProfileName = view.findViewById<TextView>(R.id.tvProfileName)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        val tvPersonalInfo = view.findViewById<TextView>(R.id.tvPersonalInfo)
        val tvPaymentMethods = view.findViewById<TextView>(R.id.tvPaymentMethods)
        val tvHelpSupport = view.findViewById<TextView>(R.id.tvHelpSupport)
        val switchDarkMode = view.findViewById<SwitchMaterial>(R.id.switchDarkMode)

        tvProfileName.text = "Hello, ${sharedPref.getString("USER_NAME", "User")}!"

        // --- DARK MODE LOGIC ---
        // Check if dark mode is currently enabled
        val isDarkMode = sharedPref.getBoolean("IS_DARK_MODE", false)
        switchDarkMode.isChecked = isDarkMode

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("IS_DARK_MODE", isChecked).apply()
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // --- CLICK LISTENERS ---
        tvPersonalInfo.setOnClickListener {
            showEditProfileDialog()
        }

        tvPaymentMethods.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Payment Methods")
                .setMessage("All rental payments are processed securely. You can pay via Credit Card or EFT directly from the 'Pending Applications' tab once your application is approved.")
                .setPositiveButton("Got it") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        tvHelpSupport.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Help & Support")
                .setMessage("Need assistance?\n\nEmail: support@campusnest.co.za\nPhone: +27 11 123 4567\n\nOur support team is available Mon-Fri, 8am - 5pm.")
                .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        btnLogout.setOnClickListener {
            sharedPref.edit().clear().apply()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    // --- EDIT PROFILE DIALOG ---
    private fun showEditProfileDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null)

        val etFirstName = dialogView.findViewById<TextInputEditText>(R.id.etEditFirstName)
        val etLastName = dialogView.findViewById<TextInputEditText>(R.id.etEditLastName)
        val etEmail = dialogView.findViewById<TextInputEditText>(R.id.etEditEmail)
        val etPassword = dialogView.findViewById<TextInputEditText>(R.id.etEditPassword)

        // Pre-fill with current data from SharedPrefs (we'll just put a placeholder here)
        val currentName = sharedPref.getString("USER_NAME", "") ?: ""
        etFirstName.setText(currentName)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val firstName = etFirstName.text.toString().trim()
                val lastName = etLastName.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val newPassword = etPassword.text.toString().trim()

                if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty()) {
                    updateProfileAPI(firstName, lastName, email, newPassword)
                } else {
                    Toast.makeText(requireContext(), "Name and Email are required", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun updateProfileAPI(firstName: String, lastName: String, email: String, newPassword: String) {
        val token = sharedPref.getString("API_TOKEN", "") ?: ""

        lifecycleScope.launch {
            try {
                val request = UpdateProfileRequest(firstName, lastName, email, if(newPassword.isEmpty()) null else newPassword)
                val response = RetrofitClient.apiService.updateProfile(token, request)

                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(requireContext(), "Profile Updated!", Toast.LENGTH_SHORT).show()
                    // Update the greeting name in SharedPreferences
                    sharedPref.edit().putString("USER_NAME", firstName).apply()
                    // Update UI
                    view?.findViewById<TextView>(R.id.tvProfileName)?.text = "Hello, $firstName!"
                } else {
                    Toast.makeText(requireContext(), response.body()?.message ?: "Update failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}