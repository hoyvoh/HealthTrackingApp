package com.example.fitnessapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessapp.data.local.QuestEntity
import com.example.fitnessapp.ui.screens.components.QuestCard

@Composable
fun HomeScreen(
    quests: List<QuestEntity>,
    onCreateClick: (String, Float) -> Unit,
    onQuestClick: (QuestEntity) -> Unit,
    onDeleteQuest: (QuestEntity) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fitness Quests") },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Create Quest")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (quests.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Button(onClick = { showDialog = true }) {
                        Text("Create Quest")
                    }
                }
            } else {
                val ongoing = quests.count { it.status.name == "ONGOING" }
                val completed = quests.count { it.status.name == "COMPLETED" }
                Text("You have ${quests.size} quests: $ongoing ongoing, $completed completed.",
                    modifier = Modifier.padding(16.dp))

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(quests) { quest ->
                        QuestCard(quest, onQuestClick, onDeleteQuest)
                    }
                }
            }
        }
    }

    if (showDialog) {
        CreateQuestDialog(
            onDismiss = { showDialog = false },
            onCreate = { name, km ->
                onCreateClick(name, km)
                showDialog = false
            }
        )
    }
}
