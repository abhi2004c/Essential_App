package com.example.subcountdown.subscriptions

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

data class Subscription(val name: String, val daysLeft: Int, val color: Color)

class SubViewModel : ViewModel() {
    val subscriptions = mutableStateListOf(
        Subscription("Netflix", 5, Color(0xFFE50914)),
        Subscription("Spotify", 12, Color(0xFF1DB954)),
        Subscription("YouTube", 2, Color(0xFFFF0000))
    )
}
