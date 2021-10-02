package com.example.geocachingandroidcodingexercise.geocachingSplash

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay

class SplashViewModel: ViewModel() {

    suspend fun startTimer(time: Long, onTimerEnd: () -> Unit) {
        delay(timeMillis = time)
        onTimerEnd()
    }
}