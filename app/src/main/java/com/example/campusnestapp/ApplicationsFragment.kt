package com.example.campusnestapp

import android.content.Context
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
import kotlinx.coroutines.launch

class ApplicationsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_applications, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar = view.findViewById<ProgressBar>(R.id.progressBarApps)
        val tvNoApps = view.findViewById<TextView>(R.id.tvNoApps)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerApplications)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        val sharedPref = requireActivity().getSharedPreferences("CampusNestPrefs", Context.MODE_PRIVATE)
        val token = sharedPref.getString("API_TOKEN", "") ?: ""

        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getMyApplications(token)
                progressBar.visibility = View.GONE

                if (response.isSuccessful && response.body()?.status == "success") {
                    val apps = response.body()!!.data
                    if (apps.isEmpty()) {
                        tvNoApps.visibility = View.VISIBLE
                    } else {
                        val adapter = ApplicationAdapter(apps)
                        recycler.adapter = adapter
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load applications", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Log.e("API_ERROR", e.message.toString(), e) // Fixed the unused 'e' warning
                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}