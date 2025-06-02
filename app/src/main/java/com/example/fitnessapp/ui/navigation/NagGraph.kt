package com.example.fitnessapp.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fitnessapp.ui.screens.HomeScreen
import com.example.fitnessapp.ui.screens.QuestDetailScreen
import com.example.fitnessapp.viewmodel.HomeViewModel
import com.example.fitnessapp.viewmodel.QuestViewModel
import com.example.fitnessapp.network.GeminiApiClient
import com.example.fitnessapp.ui.screens.components.MilestonePopup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Routes {
    const val HOME = "home"
    const val DETAIL = "detail"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    questViewModel: QuestViewModel
) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            val quests by homeViewModel.quests.collectAsState()
            HomeScreen(
                quests = quests,
                onCreateClick = { name, km ->
                    homeViewModel.createQuest(name, km)
                    questViewModel.reset()
                    navController.navigate(Routes.DETAIL)
                },
                onQuestClick = {
                    questViewModel.setQuest(it)
                    navController.navigate(Routes.DETAIL)
                },
                onDeleteQuest = { homeViewModel.deleteQuest(it) }
            )
        }

        composable(Routes.DETAIL) {
            val steps by questViewModel.steps.collectAsState()
            val isPaused by questViewModel.isPaused.collectAsState()
            val km = remember(steps) { steps * 0.5f / 1000f }
            val calo = remember(steps) { steps * 0.04f }
            val point = remember(steps) { steps / 100 }
            val progress = (km / questViewModel.targetKm).coerceIn(0f, 1f)

            val quoteState = remember { mutableStateOf("Loading...") }
            LaunchedEffect(steps, calo) {
                CoroutineScope(Dispatchers.IO).launch {
                    val quote = GeminiApiClient.fetchMotivationalQuote(
                        steps = steps,
                        calories = calo,
                        points = point
                    )
                    quoteState.value = quote
                }
            }

            val shownMilestones = remember { mutableStateListOf<Int>() }
            val milestoneToShow = remember(steps) {
                listOf(500, 1000, 5000, 10000).firstOrNull {
                    it == steps && it !in shownMilestones
                }
            }

            var showPopup by remember { mutableStateOf(false) }
            if (milestoneToShow != null) {
                shownMilestones.add(milestoneToShow)
                showPopup = true
            }

            if (showPopup && milestoneToShow != null) {
                MilestonePopup(
                    milestone = milestoneToShow,
                    onDismiss = { showPopup = false }
                )
            }

            QuestDetailScreen(
                progressPercent = progress,
                steps = steps,
                km = km,
                calo = calo,
                point =point,
                quote = quoteState.value,
                isPaused = questViewModel.isPaused.collectAsState().value,
                onPauseToggle = { questViewModel.togglePause() },
                onStop = {
                    questViewModel.stopAndSave()
                    navController.navigateUp()
                },
                onDismissMilestone = { showPopup = false }
            )
        }
    }
}

