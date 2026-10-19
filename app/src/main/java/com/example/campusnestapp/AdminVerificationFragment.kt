package com.example.campusnestapp

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class AdminVerificationFragment : Fragment() {

    private var pendingListings = mutableListOf<PendingListing>()
    private lateinit var adapter: VerificationAdapter
    private lateinit var token: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_admin_verification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar = view.findViewById<ProgressBar>(R.id.progressBarVerify)
        val tvNoPending = view.findViewById<TextView>(R.id.tvNoPending)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerVerify)

        val sharedPref = requireActivity().getSharedPreferences("CampusNestPrefs", Context.MODE_PRIVATE)
        token = sharedPref.getString("API_TOKEN", "") ?: ""

        adapter = VerificationAdapter(pendingListings) { listingID ->
            verifyListing(listingID, view)
        }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getPendingListings(token)
                progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body()?.status == "success") {
                    pendingListings = response.body()!!.data.toMutableList()
                    if (pendingListings.isEmpty()) {
                        tvNoPending.visibility = View.VISIBLE
                    } else {
                        adapter.updateData(pendingListings)
                    }
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Verify a listing
    private fun verifyListing(listingID: Int, rootView: View) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.verifyListing(token, VerifyRequest(listingID))
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(requireContext(), "Listing Verified!", Toast.LENGTH_SHORT).show()
                    // Remove it from the list and refresh UI
                    pendingListings.removeAll { it.listingID == listingID }
                    adapter.updateData(pendingListings)
                    if (pendingListings.isEmpty()) {
                        rootView.findViewById<TextView>(R.id.tvNoPending).visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error verifying", Toast.LENGTH_SHORT).show()
            }
        }
    }
}