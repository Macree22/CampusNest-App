package com.example.campusnestapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ListingsFragment : Fragment() {

    private lateinit var recyclerListings: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: ListingAdapter
    private lateinit var sharedPref: android.content.SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_listings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = requireActivity().getSharedPreferences("CampusNestPrefs", android.content.Context.MODE_PRIVATE)

        recyclerListings = view.findViewById(R.id.recyclerListings)
        progressBar = view.findViewById(R.id.progressBarListings)

        adapter = ListingAdapter(emptyList()) { listing, button ->
            applyForProperty(listing, button)
        }

        recyclerListings.layoutManager = LinearLayoutManager(requireContext())
        recyclerListings.adapter = adapter

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
                    Toast.makeText(requireContext(), "Failed to load listings", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", e.message.toString(), e)
                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun applyForProperty(listing: Listing, button: Button) {
        val token = sharedPref.getString("API_TOKEN", "") ?: ""
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 30)
        val moveInDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        button.isEnabled = false
        button.text = "..."

        lifecycleScope.launch {
            try {
                val request = ApplyRequest(listing.listingID, moveInDate)
                val response = RetrofitClient.apiService.applyForListing(token, request)

                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(requireContext(), "Application Submitted!", Toast.LENGTH_LONG).show()
                    button.text = "Applied"
                    button.backgroundTintList = android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#10B981") // Green
                    )
                } else {
                    val errorMsg = response.body()?.message ?: "Failed to apply"
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
                    button.isEnabled = true
                    button.text = "Apply"
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show()
                button.isEnabled = true
                button.text = "Apply"
            }
        }
    }
}