package com.example.subcountdown.subscriptions

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.AndroidViewModel
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

data class Subscription(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val packageName: String,
    val price: Double,
    val billingCycle: String,
    val daysLeft: Int,
    val color: Color,
    val iconUrl: String? = null
)

class SubViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("subs_prefs", Context.MODE_PRIVATE)
    private val gson = GsonBuilder()
        .registerTypeAdapter(Color::class.java, ColorAdapter())
        .create()
    val subscriptions = mutableStateListOf<Subscription>()

    init {
        loadSubscriptions()
    }

    private fun loadSubscriptions() {
        val json = prefs.getString("subs_list", null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<Subscription>>() {}.type
                val savedSubs: List<Subscription> = gson.fromJson(json, type)
                subscriptions.clear()
                subscriptions.addAll(savedSubs)
            } catch (e: Exception) {
                loadDefaultData()
            }
        } else {
            loadDefaultData()
        }
    }

    private fun loadDefaultData() {
        subscriptions.clear()
        subscriptions.addAll(listOf(
            Subscription(name = "Netflix", packageName = "Premium", price = 19.99, billingCycle = "Monthly", daysLeft = 5, color = Color(0xFFE50914)),
            Subscription(name = "Spotify", packageName = "Family", price = 16.99, billingCycle = "Monthly", daysLeft = 12, color = Color(0xFF1DB954)),
            Subscription(name = "YouTube", packageName = "Premium", price = 13.99, billingCycle = "Monthly", daysLeft = 2, color = Color(0xFFFF0000))
        ))
        saveSubscriptions()
    }

    private fun saveSubscriptions() {
        val json = gson.toJson(subscriptions.toList())
        prefs.edit().putString("subs_list", json).apply()
    }

    fun addSubscription(sub: Subscription) {
        subscriptions.add(sub)
        saveSubscriptions()
    }

    fun updateSubscription(oldSubId: Long, newSub: Subscription) {
        val index = subscriptions.indexOfFirst { it.id == oldSubId }
        if (index != -1) {
            subscriptions[index] = newSub
            saveSubscriptions()
        }
    }

    fun removeSubscription(subId: Long) {
        subscriptions.removeIf { it.id == subId }
        saveSubscriptions()
    }
    
    fun getTotalMonthlyCost(): Double {
        return subscriptions.sumOf { 
            if (it.billingCycle == "Yearly") it.price / 12 else it.price
        }
    }

    private class ColorAdapter : JsonSerializer<Color>, JsonDeserializer<Color> {
        override fun serialize(src: Color, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(src.toArgb())
        }
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Color {
            return Color(json.asInt)
        }
    }
}
