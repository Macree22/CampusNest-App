package com.example.campusnestapp

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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerListings: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: ListingAdapter
    private lateinit var sharedPref: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref = getSharedPreferences("CampusNestPrefs", Context.MODE_PRIVATE)

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        recyclerListings = findViewById(R.id.recyclerListings)
        progressBar = findViewById(R.id.progressBarListings)

        val userName = sharedPref.getString("USER_NAME", "User")
        tvWelcome.text = "Hello, $userName!"

        // Pass the apply click callback to the adapter
        adapter = ListingAdapter(emptyList()) { listing, button ->
            applyForProperty(listing, button)
        }

        recyclerListings.layoutManager = LinearLayoutManager(this)
        recyclerListings.adapter = adapter

        btnLogout.setOnClickListener {
            sharedPref.edit().clear().apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        fetchListings()
    }

    private fun fetchListings() {
        progressBar.visibility = View.VISIBLE
        recyclerListings.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getListings()
                if (response.isSuccessful && response.body()?.status == "success") {
                    val listings = response.body()!!.data
                    adapter.updateData(listings)
                    progressBar.visibility = View.GONE
                    recyclerListings.visibility = View.VISIBLE
                } else {
                    Toast.makeText(this@MainActivity, "Failed to load listings", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", e.message.toString(), e)
                Toast.makeText(this@MainActivity, "Network Error", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun applyForProperty(listing: Listing, button: Button) {
        // Get the token we saved during login
        val token = sharedPref.getString("API_TOKEN", "") ?: ""

        // Calculate a move-in date (e.g., 30 days from today)
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 30)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val moveInDate = dateFormat.format(calendar.time)

        // Change button UI to show loading
        button.isEnabled = false
        button.text = "..."

        lifecycleScope.launch {
            try {
                val request = ApplyRequest(listing.listingID, moveInDate)
                val response = RetrofitClient.apiService.applyForListing(token, request)

                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(this@MainActivity, "Application Submitted!", Toast.LENGTH_LONG).show()
                    button.text = "Applied"
                    button.backgroundTintList = android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#10B981") // Green success color
                    )
                } else {
                    // Show error from server (e.g., "Already applied")
                    val errorMsg = response.body()?.message ?: "Failed to apply"
                    Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_LONG).show()
                    button.isEnabled = true
                    button.text = "Apply"
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", e.message.toString(), e)
                Toast.makeText(this@MainActivity, "Network Error", Toast.LENGTH_SHORT).show()
                button.isEnabled = true
                button.text = "Apply"
            }
        }
    }
}