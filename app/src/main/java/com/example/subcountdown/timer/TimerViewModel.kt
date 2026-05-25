package com.example.subcountdown.timer

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewModel(application: Application) : AndroidViewModel(application) {
    private val masterKey = MasterKey.Builder(application)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        application,
        "timer_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    var timeSeconds by mutableLongStateOf(prefs.getLong("last_seconds", 60L))
    var isRunning by mutableStateOf(false)
    private var timerJob: Job? = null

    fun startTimer() {
        if (timeSeconds <= 0L) return
        isRunning = true
        timerJob = viewModelScope.launch {
            while (isRunning && timeSeconds > 0L) {
                delay(1000)
                timeSeconds--
                saveLastTime()
            }
            if (timeSeconds == 0L) stopTimer()
        }
    }

    fun stopTimer() {
        isRunning = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        stopTimer()
        timeSeconds = prefs.getLong("last_set_val", 60L)
    }

    fun addMinutes(mins: Int) {
        val added = timeSeconds + mins.toLong() * 60L
        timeSeconds = added.coerceAtMost(Long.MAX_VALUE)
        prefs.edit().putLong("last_set_val", timeSeconds).apply()
        saveLastTime()
    }

    private fun saveLastTime() {
        prefs.edit().putLong("last_seconds", timeSeconds).apply()
    }
}
