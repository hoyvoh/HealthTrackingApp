package com.example.fitnesswatch.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
//noinspection WearMaterialTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.wearable.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivityWatch : ComponentActivity(), DataClient.OnDataChangedListener {

    private var steps by mutableIntStateOf(0)
    private var km by mutableFloatStateOf(0f)
    private var calo by mutableFloatStateOf(0f)
    private var questName by mutableStateOf("Unknown Quest")
    private var lastSyncTime by mutableStateOf("Never")

    @SuppressLint("SimpleDateFormat")
    private fun formatTimestamp(timestamp: Long): String {
        return try {
            val sdf = SimpleDateFormat("HH:mm:ss")
            sdf.format(Date(timestamp))
        } catch (e: Exception) {
            "Invalid"
        }
    }

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainWearableActivity", "onCreate started")

        Wearable.getDataClient(this).addListener(this)

        setContent {
            MaterialTheme {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = questName)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "$steps steps")
                        Text(text = "${String.format("%.2f", km)} km")
                        Text(text = "${String.format("%.1f", calo)} cal")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Last sync: $lastSyncTime")
                    }
                }
            }
        }
    }

    @SuppressLint("VisibleForTests")
    override fun onDataChanged(events: DataEventBuffer) {
        for (event in events) {
            if (event.type == DataEvent.TYPE_CHANGED && event.dataItem.uri.path == "/fitness_sync") {
                val dataMap = DataMapItem.fromDataItem(event.dataItem).dataMap

                questName = dataMap.getString("questName") ?: questName
                steps = dataMap.getInt("steps")
                km = dataMap.getFloat("km")
                calo = dataMap.getFloat("calo")
                val timestamp = dataMap.getLong("timestamp", -1L)

                if (timestamp != -1L) {
                    lastSyncTime = formatTimestamp(timestamp)
                    val now = System.currentTimeMillis()
                    val delta = now - timestamp
                    if (delta > 60000) { // more than 1 minute ago
                        Log.w("MainWearableActivity", "Timestamp is old: ${delta}ms ago")
                    }
                } else {
                    lastSyncTime = "No timestamp"
                    Log.w("MainWearableActivity", "Missing timestamp in dataMap")
                }

                Log.d("MainWearableActivity", "Data received → steps: $steps, km: $km, calo: $calo, time: $lastSyncTime")
            }
        }
    }
    override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(this)
        Log.d("MainWearableActivity", "Listener registered")
    }
    override fun onPause() {
        Wearable.getDataClient(this).removeListener(this)
        Log.d("MainWearableActivity", "Listener unregistered")
        super.onPause()
    }

    override fun onDestroy() {
        Wearable.getDataClient(this).removeListener(this)
        super.onDestroy()
    }
}
