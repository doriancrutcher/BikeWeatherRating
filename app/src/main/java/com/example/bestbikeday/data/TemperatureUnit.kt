package com.example.bestbikeday.data

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT;

    fun format(temp: Double): String {
        return when (this) {
            CELSIUS -> "${temp.toInt()}°C"
            FAHRENHEIT -> "${(temp * 9/5 + 32).toInt()}°F"
        }
    }
} 