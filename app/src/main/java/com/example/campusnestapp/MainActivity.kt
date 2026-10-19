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

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerListings: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: ListingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Setup UI
        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        recyclerListings = findViewById(R.id.recyclerListings)
        progressBar = findViewById(R.id.progressBarListings)

        val sharedPref = getSharedPreferences("CampusNestPrefs", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("USER_NAME", "User")
        tvWelcome.text = "Hello, $userName!"

        // 2. Setup RecyclerView
        recyclerListings.layoutManager = LinearLayoutManager(this)
        adapter = ListingAdapter(emptyList())
        recyclerListings.adapter = adapter

        // 3. Logout Logic
        btnLogout.setOnClickListener {
            sharedPref.edit().clear().apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // 4. Fetch Listings
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
}