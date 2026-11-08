package com.example.campusnestapp

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    // 1. Authentication
    @POST("login.php")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // 2. Fetch all available and verified listings
    @GET("listings.php")
    suspend fun getListings(): Response<ListingResponse>

    // 3. Submit an application for a property
    @POST("apply.php")
    suspend fun applyForListing(
        @Header("Authorization") token: String,
        @Body request: ApplyRequest
    ): Response<BasicResponse>

    // 4. Fetch the logged-in student's application history
    @GET("my_applications.php")
    suspend fun getMyApplications(
        @Header("Authorization") token: String
    ): Response<ApplicationResponse>

    // 5. Accept lease agreement (API path)
    @POST("accept_lease.php")
    suspend fun acceptLease(
        @Header("Authorization") token: String,
        @Body request: AcceptLeaseRequest
    ): Response<BasicResponse>

    // 5b. Accept lease agreement (Root fallback path)
    @POST("/accept_lease.php")
    suspend fun acceptLeaseRoot(
        @Header("Authorization") token: String,
        @Body request: AcceptLeaseRequest
    ): Response<BasicResponse>

    // 6. Fetch the logged-in student's profile
    @GET("profile.php")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ProfileResponse>

    // 7. Update the logged-in student's profile
    @POST("update_profile.php")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<BasicResponse>

    // 8. Register a new user
    @POST("register.php")
    suspend fun register(@Body request: RegisterRequest): Response<BasicResponse>

    // 9. Fetch all pending listings for admin
    @GET("admin_pending_listings.php")
    suspend fun getPendingListings(@Header("Authorization") token: String): Response<PendingListingsResponse>

    // 10. Verify a pending listing
    @POST("admin_verify.php")
    suspend fun verifyListing(
        @Header("Authorization") token: String,
        @Body request: VerifyRequest
    ): Response<BasicResponse>
}
