package com.example.subcountdown.core.models

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<WeatherDescription>,
    val wind: Wind,
    val sys: Sys? = null,
    val clouds: Clouds? = null,
    val visibility: Int? = null,
    val dt: Long? = null
)

data class ForecastResponse(
    val list: List<ForecastItem>,
    val city: City
)

data class ForecastItem(
    val dt: Long,
    val main: Main,
    val weather: List<WeatherDescription>,
    val wind: Wind,
    @SerializedName("dt_txt")
    val dtTxt: String,
    val pop: Float? = null // Probability of precipitation
)

data class City(
    val name: String,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

data class Sys(
    val sunrise: Long,
    val sunset: Long
)

data class Clouds(val all: Int)

data class Main(
    val temp: Float,
    @SerializedName("feels_like")
    val feelsLike: Float,
    val temp_min: Float? = null,
    val temp_max: Float? = null,
    val humidity: Int,
    val pressure: Int
)

data class WeatherDescription(
    val description: String,
    val icon: String
)

data class Wind(
    val speed: Float
)
