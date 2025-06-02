package com.example.fitnessapp.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fitnessapp.data.local.QuestEntity

@Composable
fun QuestCard(
    quest: QuestEntity,
    onClick: (QuestEntity) -> Unit,
    onDelete: (QuestEntity) -> Unit
) {
    var showOverlay by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth()
        .background(Color.LightGray)
        .clickable { showOverlay = true }) {

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = quest.name, style = MaterialTheme.typography.h6)
            Text(text = "Target: ${quest.targetKm} km")
        }

        if (showOverlay) {
            Box(
                Modifier.fillMaxSize().background(Color(0xAA000000)),
                contentAlignment = Alignment.Center
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { onClick(quest); showOverlay = false }) {
                        Text("Resume")
                    }
                    OutlinedButton(onClick = { onDelete(quest); showOverlay = false }) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}
