package com.github.kuya32.geocachingandroidcodingexercise.geocachingsplash

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay

class SplashViewModel : ViewModel() {

    // Used this timer functions since Handler.postDelayed is deprecated
    suspend fun startTimer(time: Long, onTimerEnd: () -> Unit) {
        delay(timeMillis = time)
        onTimerEnd()
    }
}