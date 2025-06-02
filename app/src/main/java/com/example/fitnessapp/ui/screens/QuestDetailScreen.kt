package com.example.fitnessapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessapp.ui.screens.components.CircularProgressBar
import com.example.fitnessapp.ui.screens.components.MilestonePopup

@SuppressLint("DefaultLocale")
@Composable
fun QuestDetailScreen(
    progressPercent: Float,
    steps: Int,
    km: Float,
    calo: Float,
    quote: String,
    point: Int,
    isPaused: Boolean,
    onPauseToggle: () -> Unit,
    onStop: () -> Unit,
    onDismissMilestone: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = quote,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            CircularProgressBar(percentage = progressPercent)

            Spacer(modifier = Modifier.height(16.dp))
            Text("$steps steps")
            Text("${String.format("%.2f", km)} km")
            Text("${String.format("%.1f", calo)} cal")
            Text("$point pts")

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onPauseToggle,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (isPaused) MaterialTheme.colors.secondary else MaterialTheme.colors.primary
                    )
                ) {
                    Text(if (isPaused) "Resume" else "Pause")
                }

                Button(onClick = onStop) {
                    Text("Stop & Save Results")
                }
            }

            MilestonePopup(milestone = steps, onDismiss = onDismissMilestone)
        }
    }
}
