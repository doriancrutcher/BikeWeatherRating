package com.example.bestbikeday.data

data class WeatherResponse(
    val daily: DailyForecast
)

data class DailyForecast(
    val time: List<String>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val precipitation_probability_max: List<Int>,
    val windspeed_10m_max: List<Double>
)

data class DayForecast(
    val date: String,
    val maxTemp: Double,
    val minTemp: Double,
    val precipitationProbability: Int,
    val windSpeed: Double,
    val bikeScore: Int
) 