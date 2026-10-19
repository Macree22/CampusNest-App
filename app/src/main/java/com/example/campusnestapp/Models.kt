package com.example.campusnestapp

// Login Models
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val status: String, val token: String?, val role: String?, val name: String?, val id: Int?, val message: String?)

// Listing Models
data class Listing(
    val listingID: Int,
    val title: String,
    val address: String,
    val city: String,
    val monthlyRent: Double,
    val roomType: String,
    val imagePath: String,
    val amenities: String?
)

data class ListingResponse(
    val status: String,
    val data: List<Listing>
)