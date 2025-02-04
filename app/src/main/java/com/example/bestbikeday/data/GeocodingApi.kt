package com.example.bestbikeday.data

import retrofit2.http.GET
import retrofit2.http.Query

data class GeocodingResult(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val admin1: String? = null
) {
    val displayName: String
        get() = if (admin1 != null) "$name, $admin1, $country" else "$name, $country"
}

interface GeocodingApi {
    @GET("v1/search")
    suspend fun searchLocations(
        @Query("name") query: String,
        @Query("count") count: Int = 5,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json"
    ): List<GeocodingResult>
} 