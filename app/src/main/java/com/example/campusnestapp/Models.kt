package com.example.campusnestapp

import com.google.gson.annotations.SerializedName

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

data class AcceptLeaseRequest(
    @SerializedName("bookingID", alternate = ["booking_id", "bookingId"])
    val bookingID: Int
)

data class UserApplication(
    @SerializedName("applicationID", alternate = ["application_id", "applicationId"])
    val applicationID: Int,

    @SerializedName("applicationStatus", alternate = ["application_status", "status"])
    val applicationStatus: String,

    @SerializedName("preferredMoveInDate", alternate = ["preferred_move_in_date", "move_in_date"])
    val preferredMoveInDate: String,

    val title: String,
    val monthlyRent: Double,
    val address: String,

    @SerializedName("bookingID", alternate = ["booking_id", "bookingId"])
    val bookingID: Int? = null,

    @SerializedName("studentAccepted", alternate = ["student_accepted", "accepted"])
    val studentAccepted: Int? = null,

    @SerializedName("durationMonths", alternate = ["duration_months"])
    val durationMonths: Int? = null,

    @SerializedName("totalContractValue", alternate = ["total_contract_value"])
    val totalContractValue: Double? = null,

    @SerializedName("paymentID", alternate = ["payment_id", "paymentId"])
    val paymentID: Int? = null
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
