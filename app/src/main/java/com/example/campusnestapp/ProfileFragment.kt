package com.example.campusnestapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("CampusNestPrefs", Context.MODE_PRIVATE)

        val tvProfileName = view.findViewById<TextView>(R.id.tvProfileName)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        tvProfileName.text = "Hello, ${sharedPref.getString("USER_NAME", "User")}!"

        btnLogout.setOnClickListener {
            sharedPref.edit().clear().apply()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }
}