package com.example.subcountdown.weather

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subcountdown.core.models.ForecastResponse
import com.example.subcountdown.core.models.WeatherResponse
import com.example.subcountdown.BuildConfig
import com.example.subcountdown.core.network.RetrofitInstance
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    var weatherData by mutableStateOf<WeatherResponse?>(null)
    var forecastData by mutableStateOf<ForecastResponse?>(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private val API_KEY = BuildConfig.WEATHER_API_KEY

    fun fetchWeather(city: String) {
        if (city.isBlank()) return
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                weatherData = RetrofitInstance.api.getWeatherByCity(city, API_KEY)
                forecastData = RetrofitInstance.api.getForecastByCity(city, API_KEY)
            } catch (e: Exception) {
                errorMessage = "City not found"
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchByLoc(lat: Double, lon: Double) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                weatherData = RetrofitInstance.api.getWeatherByCoords(lat, lon, API_KEY)
                forecastData = RetrofitInstance.api.getForecastByCoords(lat, lon, API_KEY)
            } catch (e: Exception) {
                errorMessage = "GPS search failed"
            } finally {
                isLoading = false
            }
        }
    }
}
