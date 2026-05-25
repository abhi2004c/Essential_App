package com.example.subcountdown.clock

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.util.*

data class WorldClock(val cityName: String, val timezoneId: String)

class ClockViewModel : ViewModel() {
    val clocks = mutableStateListOf(
        WorldClock("New York", "America/New_York"),
        WorldClock("London", "Europe/London"),
        WorldClock("Tokyo", "Asia/Tokyo")
    )

    fun addCityByName(name: String) {
        val availableIds = TimeZone.getAvailableIDs()
        val cleanName = name.trim().replace(" ", "_")
        
        // Find best match: e.g. if user types "Paris", find "Europe/Paris" or "Paris"
        val matchedTz = availableIds.find { it.split("/").last().equals(cleanName, ignoreCase = true) } 
                      ?: availableIds.find { it.contains(cleanName, ignoreCase = true) }
                      ?: "UTC"

        clocks.add(WorldClock(name.trim(), matchedTz))
    }
    
    fun removeCity(clock: WorldClock) {
        clocks.remove(clock)
    }
}
