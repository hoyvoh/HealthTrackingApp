package com.example.fitnessapp.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.concurrent.fixedRateTimer

class StepBoundService : Service() {

    private val binder = StepBinder()

    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps.asStateFlow()

    private val _calories = MutableStateFlow(0f)
    val calories: StateFlow<Float> = _calories.asStateFlow()

    private var timer = fixedRateTimer("stepTimer", initialDelay = 1000, period = 3000) {
        _steps.value += 100
        _calories.value = _steps.value * 0.04f
    }

    inner class StepBinder : Binder() {
        fun getService(): StepBoundService = this@StepBoundService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }
}