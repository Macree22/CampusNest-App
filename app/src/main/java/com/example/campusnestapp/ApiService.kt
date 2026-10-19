package com.example.campusnestapp

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("login.php")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("listings.php")
    suspend fun getListings(): Response<ListingResponse>

    @POST("apply.php")
    suspend fun applyForListing(
        @Header("Authorization") token: String,
        @Body request: ApplyRequest
    ): Response<BasicResponse>
}