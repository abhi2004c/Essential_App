package com.example.subcountdown.converter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class CurrencyViewModel : ViewModel() {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://open.er-api.com/v6/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(CurrencyApi::class.java)

    var rates by mutableStateOf<Map<String, Double>>(emptyMap())
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    init {
        fetchRates("USD")
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
