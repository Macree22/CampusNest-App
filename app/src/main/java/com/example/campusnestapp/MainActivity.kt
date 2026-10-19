package com.example.campusnestapp

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply Dark Mode setting BEFORE the UI loads
        val sharedPref = getSharedPreferences("CampusNestPrefs", Context.MODE_PRIVATE)
        if (sharedPref.getBoolean("IS_DARK_MODE", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val userRole = sharedPref.getString("USER_ROLE", "student")

        // Check if Admin or Student
        if (userRole == "admin") {
            setupAdminNavigation(bottomNav)
        } else {
            setupStudentNavigation(bottomNav)
        }
    }

    private fun setupStudentNavigation(bottomNav: BottomNavigationView) {

        if (supportFragmentManager.findFragmentById(R.id.fragmentContainer) == null) {
            loadFragment(HomeFragment())
            bottomNav.selectedItemId = R.id.nav_home
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { loadFragment(HomeFragment()); true }
                R.id.nav_explore -> { loadFragment(ListingsFragment()); true }
                R.id.nav_pending -> { loadFragment(ApplicationsFragment()); true }
                R.id.nav_profile -> { loadFragment(ProfileFragment()); true }
                else -> false
            }
        }
    }

    private fun setupAdminNavigation(bottomNav: BottomNavigationView) {
        // Clear the default student menu that was loaded from activity_main.xml
        bottomNav.menu.clear()

        // Add Admin items manually
        bottomNav.menu.add(0, 100, 0, "Admin Home").setIcon(android.R.drawable.ic_menu_manage)
        bottomNav.menu.add(0, 101, 1, "Verify").setIcon(android.R.drawable.ic_menu_edit)
        bottomNav.menu.add(0, 102, 2, "Profile").setIcon(android.R.drawable.ic_menu_myplaces)

        if (supportFragmentManager.findFragmentById(R.id.fragmentContainer) == null) {
            loadFragment(AdminDashboardFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                100 -> { loadFragment(AdminDashboardFragment()); true }
                101 -> { loadFragment(AdminVerificationFragment()); true }
                102 -> { loadFragment(ProfileFragment()); true } // Reuse the profile fragment
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}