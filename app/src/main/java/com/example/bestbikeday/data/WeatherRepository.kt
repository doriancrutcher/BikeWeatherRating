package com.example.bestbikeday.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository {
    private val api = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)

    suspend fun getWeatherForecast(latitude: Double, longitude: Double): List<DayForecast> {
        val response = api.getWeatherForecast(latitude, longitude)
        return response.daily.let { daily ->
            daily.time.mapIndexed { index, date ->
                DayForecast(
                    date = date,
                    maxTemp = daily.temperature_2m_max[index],
                    minTemp = daily.temperature_2m_min[index],
                    precipitationProbability = daily.precipitation_probability_max[index],
                    windSpeed = daily.windspeed_10m_max[index],
                    bikeScore = calculateBikeScore(
                        daily.temperature_2m_max[index],
                        daily.temperature_2m_min[index],
                        daily.precipitation_probability_max[index],
                        daily.windspeed_10m_max[index]
                    )
                )
            }
        }
    }

    private fun calculateBikeScore(maxTemp: Double, minTemp: Double, precipProb: Int, windSpeed: Double): Int {
        var score = 100

        // Temperature penalties
        when {
            maxTemp > 35 -> score -= 30
            maxTemp > 30 -> score -= 20
            maxTemp > 25 -> score -= 10
            minTemp < 5 -> score -= 20
            minTemp < 10 -> score -= 10
        }

        // Precipitation probability penalties
        when {
            precipProb > 70 -> score -= 40
            precipProb > 50 -> score -= 30
            precipProb > 30 -> score -= 20
            precipProb > 10 -> score -= 10
        }

        // Wind speed penalties (m/s)
        when {
            windSpeed > 10 -> score -= 30
            windSpeed > 7 -> score -= 20
            windSpeed > 5 -> score -= 10
        }

        return score.coerceIn(0, 100)
    }
} 