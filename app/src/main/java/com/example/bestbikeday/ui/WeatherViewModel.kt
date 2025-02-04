package com.example.bestbikeday.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bestbikeday.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val weatherRepository = WeatherRepository()
    
    // Updated list with more US cities
    private val cities = listOf(
        // United States Cities
        City("New York", "New York", "USA", 40.7128, -74.0060),
        City("Los Angeles", "California", "USA", 34.0522, -118.2437),
        City("San Francisco", "California", "USA", 37.7749, -122.4194),
        City("Chicago", "Illinois", "USA", 41.8781, -87.6298),
        City("Miami", "Florida", "USA", 25.7617, -80.1918),
        City("Seattle", "Washington", "USA", 47.6062, -122.3321),
        City("Denver", "Colorado", "USA", 39.7392, -104.9903),
        City("Boston", "Massachusetts", "USA", 42.3601, -71.0589),
        City("Portland", "Oregon", "USA", 45.5155, -122.6789),
        City("Austin", "Texas", "USA", 30.2672, -97.7431),
        
        // International Cities
        City("London", "England", "United Kingdom", 51.5074, -0.1278),
        City("Tokyo", "Tokyo", "Japan", 35.6762, 139.6503),
        City("Paris", "Île-de-France", "France", 48.8566, 2.3522),
        City("Sydney", "New South Wales", "Australia", -33.8688, 151.2093),
        City("Berlin", "Berlin", "Germany", 52.5200, 13.4050),
        City("Vancouver", "British Columbia", "Canada", 49.2827, -123.1207),
        City("Barcelona", "Catalonia", "Spain", 41.3851, 2.1734),
        City("Amsterdam", "North Holland", "Netherlands", 52.3676, 4.9041),
        City("Singapore", "Singapore", "Singapore", 1.3521, 103.8198),
        City("Dubai", "Dubai", "UAE", 25.2048, 55.2708)
    )
    
    private val _weatherState = MutableStateFlow<List<DayForecast>>(emptyList())
    val weatherState = _weatherState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _filteredCities = MutableStateFlow(cities)
    val filteredCities = _filteredCities.asStateFlow()

    private val _temperatureUnit = MutableStateFlow(TemperatureUnit.CELSIUS)
    val temperatureUnit = _temperatureUnit.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _filteredCities.value = if (query.isBlank()) {
            cities
        } else {
            cities.filter { city ->
                city.displayName.contains(query, ignoreCase = true)
            }
        }
    }

    fun selectCity(city: City) {
        _searchQuery.value = city.displayName
        _filteredCities.value = emptyList()
        loadWeatherForecast(city.latitude, city.longitude)
    }

    fun loadWeatherForecast(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                _weatherState.value = weatherRepository.getWeatherForecast(latitude, longitude)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setTemperatureUnit(unit: TemperatureUnit) {
        _temperatureUnit.value = unit
    }
} 