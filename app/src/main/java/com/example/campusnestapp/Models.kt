package com.example.campusnestapp

// --- Authentication Models ---
data class LoginRequest(val email: String, val password: String)
data class LoginResponse(
    val status: String,
    val token: String?,
    val role: String?,
    val name: String?,
    val id: Int?,
    val message: String?
)

// --- Listing Models ---
data class Listing(
    val listingID: Int,
    val title: String,
    val address: String,
    val city: String,
    val monthlyRent: Double,
    val roomType: String,
    val imagePath: String,
    val amenities: String?
) : java.io.Serializable
data class ListingResponse(val status: String, val data: List<Listing>)

// --- Application Models ---
data class ApplyRequest(val listingID: Int, val moveInDate: String)
data class BasicResponse(
    val status: String,
    val message: String,
    val reference: String? = null
)

data class UserApplication(
    val applicationID: Int,
    val applicationStatus: String,
    val preferredMoveInDate: String,
    val title: String,
    val monthlyRent: Double,
    val address: String
)

data class ApplicationResponse(
    val status: String,
    val data: List<UserApplication>
)
// --- Profile Models ---
data class ProfileData(
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String,
    val studentNumber: String? = null
)
// --- Profile Models ---
data class ProfileResponse(
    val status: String,
    val data: ProfileData
)

// --- Edit Profile Models ---
data class UpdateProfileRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val newPassword: String?
)
// --- Register Models ---
data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val studentNumber: String
)

// --- Pending Listings Models ---
data class PendingListing(
    val listingID: Int,
    val title: String,
    val address: String,
    val city: String,
    val monthlyRent: Double,
    val roomType: String,
    val firstName: String,
    val lastName: String
)
data class PendingListingsResponse(
    val status: String,
    val data: List<PendingListing>
)
data class VerifyRequest(val listingID: Int)
