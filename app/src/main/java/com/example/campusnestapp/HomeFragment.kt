package com.example.campusnestapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.card.MaterialCardView

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Set the greeting name
        val sharedPref = requireActivity().getSharedPreferences("CampusNestPrefs", Context.MODE_PRIVATE)
        val name = sharedPref.getString("USER_NAME", "Student")
        view.findViewById<TextView>(R.id.tvHomeGreeting).text = "Hello, $name 👋"

        // 2. Handle the "Browse Accommodations" click
        val cardBrowse = view.findViewById<MaterialCardView>(R.id.cardBrowseAccommodations)
        cardBrowse.setOnClickListener {
            // Find the BottomNavigationView in the parent Activity and trigger a click on the "Explore" tab
            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
            bottomNav.selectedItemId = R.id.nav_explore
        }
    }
}