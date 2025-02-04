package com.example.bestbikeday.data

data class City(
    val name: String,
    val region: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
) {
    val displayName: String
        get() = "$name, $region, $country"
} 