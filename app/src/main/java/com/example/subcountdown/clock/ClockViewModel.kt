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

    val allAvailableTimeZones = TimeZone.getAvailableIDs()
        .filter { it.contains("/") } // Focus on Region/City format
        .sortedBy { it.split("/").last() }

    fun addClock(tzId: String) {
        val cityName = tzId.split("/").last().replace("_", " ")
        clocks.add(WorldClock(cityName, tzId))
    }
    
    fun removeCity(clock: WorldClock) {
        clocks.remove(clock)
    }
}
