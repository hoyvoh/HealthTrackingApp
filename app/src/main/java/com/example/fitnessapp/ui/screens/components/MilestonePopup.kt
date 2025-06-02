package com.example.fitnessapp.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun MilestonePopup(
    milestone: Int,
    onDismiss: () -> Unit
) {
    val visible = remember { mutableStateOf(false) }
    val milestones = listOf(500, 1000, 5000, 10000)

    LaunchedEffect(milestone) {
        if (milestone in milestones) {
            visible.value = true
            delay(3000)
            visible.value = false
            onDismiss()
        }
    }

    if (visible.value) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xAA000000))
                .clickable {
                    visible.value = false
                    onDismiss()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🎉 Congrats! $milestone steps reached!",
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.Black)
            )
        }
    }
}
