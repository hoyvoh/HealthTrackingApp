package com.example.fitnessapp.wearos

import android.content.Context
import android.util.Log
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import java.util.UUID

object SyncHelper {

    private const val SYNC_PATH = "/fitness_sync"

    fun sendQuestData(
        context: Context,
        questName: String,
        steps: Int,
        km: Float,
        calo: Float,
        onStatus: (String) -> Unit = {}  // optional lambda for status messages
    ) {
        val dataClient: DataClient = Wearable.getDataClient(context)

        val dataMap = DataMap().apply {
            putString("questName", questName)
            putInt("steps", steps)
            putFloat("km", km)
            putFloat("calo", calo)
            putLong("timestamp", System.currentTimeMillis())
            putString("nonce", UUID.randomUUID().toString())
        }

        val request = PutDataMapRequest.create(SYNC_PATH).apply {
            dataMap.putAll(this.dataMap)
        }.asPutDataRequest().setUrgent()

        onStatus("Sending data to WearOS...")
        Log.d("SyncHelper", "WearOS sync in progress: $request")
        dataClient.putDataItem(request)
            .addOnSuccessListener {
                Log.d("SyncHelper", "WearOS sync success: $request")
                onStatus("✅ WearOS sync success")
            }
            .addOnFailureListener {
                Log.e("SyncHelper", "WearOS sync failed", it)
                onStatus("❌ WearOS sync failed: ${it.message}")
            }
    }

}