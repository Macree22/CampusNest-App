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
    // Notice the @Header("Authorization") - this sends the Bearer token we saved during login
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
}