package com.example.fitnessapp.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.fitnessapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.concurrent.fixedRateTimer

class StepForegroundService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var steps = 0
    private var quote: String = "Loading..."

    override fun onCreate() {
        super.onCreate()

        serviceScope.launch {
            quote = try {
                com.example.fitnessapp.network.GeminiApiClient.fetchMotivationalQuote(
                    steps, steps * 0.04f, steps / 100
                )
            } catch (e: Exception) {
                "You're doing great! Keep going!"
            }

            startForeground(
                NotificationHelper.NOTIFICATION_ID,
                createNotification(steps, quote)
            )
        }

        fixedRateTimer("stepTracker", initialDelay = 5000, period = 5000) {
            steps += 100
            val updatedNotification = createNotification(steps, quote)
            NotificationHelper.updateNotification(this@StepForegroundService, updatedNotification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun createNotification(steps: Int, quote: String): Notification {
        return NotificationCompat.Builder(this, NotificationHelper.CHANNEL_ID)
            .setContentTitle("Steps: $steps")
            .setContentText(quote)
            .setSmallIcon(R.drawable.ic_walk)
            .setOngoing(true)
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}