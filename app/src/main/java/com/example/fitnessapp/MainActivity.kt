package com.example.fitnessapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.MaterialTheme
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Surface
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.fitnessapp.data.local.AppDatabase
import com.example.fitnessapp.data.repository.QuestRepository
import com.example.fitnessapp.ui.navigation.AppNavGraph
import com.example.fitnessapp.ui.theme.FitnessAppTheme
import com.example.fitnessapp.viewmodel.HomeViewModel
import com.example.fitnessapp.viewmodel.QuestViewModel
import com.example.fitnessapp.viewmodel.QuestViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FitnessAppTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()

                    // Remember DB and ViewModels
                    val db = remember { AppDatabase.getInstance(applicationContext) }
                    val repository = remember { QuestRepository(db.questDao()) }
                    val homeViewModel = remember { HomeViewModel(repository) }
                    val questViewModel: QuestViewModel = viewModel(
                        factory = QuestViewModelFactory(application, repository)
                    )

                    AppNavGraph(
                        navController = navController,
                        homeViewModel = homeViewModel,
                        questViewModel = questViewModel
                    )
                }
            }
        }
    }
}
