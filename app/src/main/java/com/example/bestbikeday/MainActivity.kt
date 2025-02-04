package com.example.bestbikeday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bestbikeday.data.DayForecast
import com.example.bestbikeday.ui.WeatherViewModel
import com.example.bestbikeday.ui.theme.BestBikeDayTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import com.example.bestbikeday.data.TemperatureUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BestBikeDayTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
    val weatherState by viewModel.weatherState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredCities by viewModel.filteredCities.collectAsState()
    val temperatureUnit by viewModel.temperatureUnit.collectAsState()

    LaunchedEffect(Unit) {
        // Start with San Francisco weather
        viewModel.loadWeatherForecast(37.7749, -122.4194)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Best Bike Day",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Add temperature unit selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Bar (wrapped in Box to take most of the width)
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search city...") },
                    leadingIcon = { 
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    singleLine = true
                )
            }

            // Temperature Unit Selector
            Box(
                modifier = Modifier.padding(start = 8.dp)
            ) {
                var expanded by remember { mutableStateOf(false) }
                
                IconButton(onClick = { expanded = true }) {
                    Text(
                        text = when (temperatureUnit) {
                            TemperatureUnit.CELSIUS -> "°C"
                            TemperatureUnit.FAHRENHEIT -> "°F"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Celsius (°C)") },
                        onClick = {
                            viewModel.setTemperatureUnit(TemperatureUnit.CELSIUS)
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Fahrenheit (°F)") },
                        onClick = {
                            viewModel.setTemperatureUnit(TemperatureUnit.FAHRENHEIT)
                            expanded = false
                        }
                    )
                }
            }
        }

        // Search Results Dropdown
        AnimatedVisibility(visible = filteredCities.isNotEmpty() && searchQuery.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            ) {
                LazyColumn {
                    items(filteredCities) { city ->
                        Text(
                            text = city.displayName,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectCity(city) }
                                .padding(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            error != null -> {
                Text(
                    text = "Error: ${error}",
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(weatherState) { forecast ->
                        ForecastCard(
                            forecast = forecast,
                            temperatureUnit = temperatureUnit
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ForecastCard(forecast: DayForecast, temperatureUnit: TemperatureUnit) {
    val bikeScore = forecast.bikeScore
    val backgroundColor = when {
        bikeScore >= 90 -> Color(0xFFE8F5E9)  // Light green
        bikeScore >= 75 -> Color(0xFFC8E6C9)  // Slightly darker green
        bikeScore >= 60 -> Color(0xFFFFF3E0)  // Light orange
        bikeScore >= 45 -> Color(0xFFFFE0B2)  // Darker orange
        bikeScore >= 30 -> Color(0xFFFFCDD2)  // Light red
        else -> Color(0xFFEFBBBB)             // Darker red
    }

    val scoreColor = when {
        bikeScore >= 90 -> Color(0xFF2E7D32)  // Dark green
        bikeScore >= 75 -> Color(0xFF388E3C)
        bikeScore >= 60 -> Color(0xFFE65100)  // Dark orange
        bikeScore >= 45 -> Color(0xFFEF6C00)
        bikeScore >= 30 -> Color(0xFFC62828)  // Dark red
        else -> Color(0xFFB71C1C)
    }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val date = LocalDate.parse(forecast.date, dateFormatter)
    val formattedDate = date.format(DateTimeFormatter.ofPattern("EEEE, MMM d"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${forecast.bikeScore}",
                    style = MaterialTheme.typography.titleLarge,
                    color = scoreColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherInfoColumn(
                    icon = "🌡️",
                    label = "Temperature",
                    value = "${temperatureUnit.format(forecast.minTemp)} - ${temperatureUnit.format(forecast.maxTemp)}"
                )
                WeatherInfoColumn(
                    icon = "🌧️",
                    label = "Rain Chance",
                    value = "${forecast.precipitationProbability}%"
                )
                WeatherInfoColumn(
                    icon = "💨",
                    label = "Wind Speed",
                    value = "${forecast.windSpeed} m/s"
                )
            }
        }
    }
}

@Composable
private fun WeatherInfoColumn(
    icon: String,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}