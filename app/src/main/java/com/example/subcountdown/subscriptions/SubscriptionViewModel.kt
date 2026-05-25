package com.example.subcountdown.subscriptions

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

data class Subscription(
    val name: String,
    val packageName: String,
    val price: Double,
    val billingCycle: String, // e.g., "Monthly", "Yearly"
    val daysLeft: Int,
    val color: Color,
    val iconUrl: String? = null
)

class SubViewModel : ViewModel() {
    val subscriptions = mutableStateListOf(
        Subscription("Netflix", "Premium", 19.99, "Monthly", 5, Color(0xFFE50914)),
        Subscription("Spotify", "Family", 16.99, "Monthly", 12, Color(0xFF1DB954)),
        Subscription("YouTube", "Premium", 13.99, "Monthly", 2, Color(0xFFFF0000)),
        Subscription("Amazon Prime", "Annual", 139.0, "Yearly", 45, Color(0xFF00A8E1))
    )

    fun addSubscription(sub: Subscription) {
        subscriptions.add(sub)
    }

    fun removeSubscription(sub: Subscription) {
        subscriptions.remove(sub)
    }
    
    fun getTotalMonthlyCost(): Double {
        return subscriptions.sumOf { 
            if (it.billingCycle == "Yearly") it.price / 12 else it.price
        }
    }
}
