package com.example.subcountdown.converter

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class CurrencyResponse(
    val base: String,
    val rates: Map<String, Double>
)

interface CurrencyApi {
    @GET("latest/{base}")
    suspend fun getLatestRates(@Path("base") base: String): CurrencyResponse
}

class CurrencyViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("currency_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://open.er-api.com/v6/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(CurrencyApi::class.java)

    var rates by mutableStateOf<Map<String, Double>>(emptyMap())
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)
    
    val favoriteCurrencies = mutableStateListOf<String>()

    init {
        loadFavorites()
        fetchRates("USD")
    }

    private fun loadFavorites() {
        val json = prefs.getString("favorites", null)
        if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            val saved: List<String> = gson.fromJson(json, type)
            favoriteCurrencies.clear()
            favoriteCurrencies.addAll(saved)
        } else {
            favoriteCurrencies.addAll(listOf("USD", "EUR", "GBP", "JPY", "INR"))
            saveFavorites()
        }
    }

    private fun saveFavorites() {
        val json = gson.toJson(favoriteCurrencies.toList())
        prefs.edit().putString("favorites", json).apply()
    }

    fun toggleFavorite(currency: String) {
        if (favoriteCurrencies.contains(currency)) {
            favoriteCurrencies.remove(currency)
        } else {
            favoriteCurrencies.add(currency)
        }
        saveFavorites()
    }

    fun fetchRates(base: String) {
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val response = api.getLatestRates(base)
                rates = response.rates
            } catch (e: Exception) {
                error = "Failed to load exchange rates"
            } finally {
                isLoading = false
            }
        }
    }

    fun convert(amount: Double, from: String, to: String): Double {
        val fromRate = rates[from] ?: 1.0
        val toRate = rates[to] ?: 1.0
        return (amount / fromRate) * toRate
    }
}
