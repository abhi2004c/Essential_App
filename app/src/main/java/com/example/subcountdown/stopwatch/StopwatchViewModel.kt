package com.example.subcountdown.stopwatch

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StopwatchViewModel : ViewModel() {
    var timeMillis by mutableStateOf(0L)
    var isRunning by mutableStateOf(false)
    val laps = mutableStateListOf<Long>()
    
    private var stopwatchJob: Job? = null

    fun start() {
        if (isRunning) return
        isRunning = true
        stopwatchJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis() - timeMillis
            while (isRunning) {
                timeMillis = System.currentTimeMillis() - startTime
                delay(10)
            }
        }
    }

    fun pause() {
        isRunning = false
        stopwatchJob?.cancel()
    }

    fun reset() {
        pause()
        timeMillis = 0L
        laps.clear()
    }

    fun recordLap() {
        if (timeMillis > 0) {
            laps.add(0, timeMillis)
        }
    }
}
