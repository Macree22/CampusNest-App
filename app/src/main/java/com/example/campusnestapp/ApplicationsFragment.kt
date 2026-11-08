package com.example.campusnestapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class ApplicationsFragment : Fragment() {

    private lateinit var progressBar: ProgressBar
    private lateinit var tvNoApps: TextView
    private lateinit var recycler: RecyclerView
    private var token: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_applications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.progressBarApps)
        tvNoApps = view.findViewById(R.id.tvNoApps)
        recycler = view.findViewById(R.id.recyclerApplications)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        val sharedPref = requireActivity().getSharedPreferences("CampusNestPrefs", Context.MODE_PRIVATE)
        token = sharedPref.getString("API_TOKEN", "") ?: ""

        loadApplications()
    }

    private fun loadApplications() {
        progressBar.visibility = View.VISIBLE
        tvNoApps.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getMyApplications(token)
                progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body()?.status == "success") {
                    val apps = response.body()!!.data
                    if (apps.isEmpty()) {
                        tvNoApps.visibility = View.VISIBLE
                        recycler.adapter = null
                    } else {
                        tvNoApps.visibility = View.GONE
                        val adapter = ApplicationAdapter(
                            apps = apps,
                            onAcceptLease = { bookingID ->
                                showLeaseDialog(bookingID)
                            },
                            onViewLease = { bookingID ->
                                openUrl("http://18.133.240.99/lease_agreement.php?bookingID=$bookingID")
                            },
                            onPrintReceipt = { paymentID ->
                                openUrl("http://18.133.240.99/receipt.php?paymentID=$paymentID")
                            }
                        )
                        recycler.adapter = adapter
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load applications", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Log.e("API_ERROR", e.message.toString(), e)
                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("OPEN_URL_ERROR", e.message.toString(), e)
            Toast.makeText(requireContext(), "Unable to open link", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLeaseDialog(bookingID: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Lease Agreement")
            .setMessage("Do you accept the lease agreement terms?")
            .setPositiveButton("Accept") { dialog, _ ->
                dialog.dismiss()
                acceptLeaseAgreement(bookingID)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun acceptLeaseAgreement(bookingID: Int) {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                // Try /api/accept_lease.php endpoint first
                var response = RetrofitClient.apiService.acceptLease(token, AcceptLeaseRequest(bookingID))

                // If server returns 404 Not Found, fallback to root endpoint /accept_lease.php
                if (response.code() == 404) {
                    response = RetrofitClient.apiService.acceptLeaseRoot(token, AcceptLeaseRequest(bookingID))
                }

                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(requireContext(), response.body()?.message ?: "Lease accepted successfully!", Toast.LENGTH_SHORT).show()
                    loadApplications()
                } else {
                    val errorMsg = response.body()?.message ?: "Failed to accept lease (${response.code()})"
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Log.e("API_ERROR", e.message.toString(), e)
                Toast.makeText(requireContext(), "Network Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
