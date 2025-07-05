package com.example.bihinsight.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.bihinsight.ui.screens.issueddlcards.IssuedDLCardScreen
import com.example.bihinsight.ui.screens.issueddlcards.IssuedDLCardUiState
import com.example.bihinsight.ui.screens.issueddlcards.IssuedDLCardViewModel
import com.example.bihinsight.ui.screens.details.IssuedDLCardDetailsScreen
import com.example.bihinsight.data.local.IssuedDLCardEntity
import com.example.bihinsight.ui.screens.favorites.FavoritesScreen
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import com.example.bihinsight.ui.screens.splash.SplashScreen
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import com.example.bihinsight.ui.screens.onboarding.OnboardingScreen
import android.content.Context
import android.content.SharedPreferences
import com.example.bihinsight.ui.screens.home.DatasetSelectionScreen
import com.example.bihinsight.ui.screens.chart.ChartScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import com.example.bihinsight.ui.screens.personsbyrecorddate.PersonsByRecordDateScreen
import com.example.bihinsight.ui.screens.personsbyrecorddate.PersonsByRecordDateViewModel
import com.example.bihinsight.ui.screens.details.PersonsByRecordDateDetailsScreen
import com.example.bihinsight.ui.screens.chart.PersonsByRecordDateChartScreen
import com.example.bihinsight.ui.screens.favorites.PersonsByRecordDateFavoritesScreen
import com.example.bihinsight.ui.screens.personsbyrecorddate.PersonsByRecordDateUiState
import com.example.bihinsight.ui.screens.newborns.NewbornByRequestDateScreen
import com.example.bihinsight.ui.screens.newborns.NewbornByRequestDateViewModel
import com.example.bihinsight.ui.screens.details.NewbornByRequestDateDetailsScreen
import com.example.bihinsight.ui.screens.chart.NewbornByRequestDateChartScreen
import com.example.bihinsight.ui.screens.favorites.NewbornByRequestDateFavoritesScreen
import com.example.bihinsight.ui.screens.settings.SettingsScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: IssuedDLCardViewModel,
    personsViewModel: PersonsByRecordDateViewModel,
    newbornsViewModel: NewbornByRequestDateViewModel,
    onThemeChanged: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier
    ) {
        composable("splash") {
            val context = LocalContext.current
            val prefs = context.getSharedPreferences("bihinsight_prefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("onboarding_done", false).apply()
            LaunchedEffect(Unit) {
                delay(1500)
                if (prefs.getBoolean("onboarding_done", false)) {
                    navController.navigate("list") {
                        popUpTo("splash") { inclusive = true }
                    }
                } else {
                    navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            }
            SplashScreen()
        }
        composable("onboarding") {
            val context = LocalContext.current
            val prefs = context.getSharedPreferences("bihinsight_prefs", Context.MODE_PRIVATE)
            OnboardingScreen(onStartClick = {
                prefs.edit().putBoolean("onboarding_done", true).apply()
                navController.navigate("list") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }
        composable("list") {
            val context = LocalContext.current
            val prefs = context.getSharedPreferences("bihinsight_prefs", Context.MODE_PRIVATE)
            val selectedDataset = prefs.getString("selected_dataset", "Izdate vozačke dozvole") ?: "Izdate vozačke dozvole"
            
            when (selectedDataset) {
                "Izdate vozačke dozvole" -> {
                    IssuedDLCardScreen(
                        viewModel,
                        onCardClick = { cardId -> navController.navigate("details/$cardId") },
                        onFavoritesClick = { navController.navigate("favorites") },
                        onDatasetClick = { navController.navigate("dataset_selection") },
                        onChartClick = { navController.navigate("chart") },
                        onSettingsClick = { navController.navigate("settings") }
                    )
                }
                "Registrovane osobe" -> {
                    PersonsByRecordDateScreen(
                        viewModel = personsViewModel,
                        onPersonClick = { personId -> navController.navigate("person_details/$personId") },
                        onFavoritesClick = { navController.navigate("person_favorites") },
                        onDatasetClick = { navController.navigate("dataset_selection") },
                        onChartClick = { navController.navigate("person_chart") },
                        onSettingsClick = { navController.navigate("settings") }
                    )
                }
                "Novorođene osobe" -> {
                    NewbornByRequestDateScreen(
                        viewModel = newbornsViewModel,
                        onNewbornClick = { newbornId -> navController.navigate("newborn_details/$newbornId") },
                        onFavoritesClick = { navController.navigate("newborn_favorites") },
                        onDatasetClick = { navController.navigate("dataset_selection") },
                        onChartClick = { navController.navigate("newborn_chart") },
                        onSettingsClick = { navController.navigate("settings") }
                    )
                }
                else -> {
                    IssuedDLCardScreen(
                        viewModel,
                        onCardClick = { cardId -> navController.navigate("details/$cardId") },
                        onFavoritesClick = { navController.navigate("favorites") },
                        onDatasetClick = { navController.navigate("dataset_selection") },
                        onChartClick = { navController.navigate("chart") },
                        onSettingsClick = { navController.navigate("settings") }
                    )
                }
            }
        }
        composable("dataset_selection") {
            val context = LocalContext.current
            val prefs = context.getSharedPreferences("bihinsight_prefs", Context.MODE_PRIVATE)
            val selectedDataset = prefs.getString("selected_dataset", "Izdate vozačke dozvole") ?: "Izdate vozačke dozvole"
            DatasetSelectionScreen(
                selectedDataset = selectedDataset,
                onDatasetSelected = { dataset ->
                    prefs.edit().putString("selected_dataset", dataset).apply()
                },
                onConfirm = { navController.popBackStack() }
            )
        }
        composable(
            route = "details/{cardId}",
            arguments = listOf(navArgument("cardId") { type = NavType.IntType })
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getInt("cardId")
            if (cardId != null) {
                val context = LocalContext.current
                val card by viewModel.observeDetailCard(cardId).collectAsState(initial = null)
                card?.let {
                    IssuedDLCardDetailsScreen(
                        it,
                        onBack = { navController.popBackStack() },
                        onToggleFavorite = { isFav ->
                            if (isFav) viewModel.addToFavorites(it.id) else viewModel.removeFromFavorites(it.id)
                        },
                        onShare = { shareText ->
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        }
                    )
                } ?: Text("Podatak nije pronađen.")
            } else {
                Text("Podatak nije pronađen.")
            }
        }
        composable("favorites") {
            val favorites by viewModel.observeFavorites().collectAsState(initial = emptyList())
            FavoritesScreen(
                favorites = favorites, 
                onCardClick = { cardId ->
                    navController.navigate("details/$cardId")
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("chart") {
            val cards by viewModel.uiState.collectAsState()
            when (cards) {
                is IssuedDLCardUiState.Success -> {
                    ChartScreen(
                        cards = (cards as IssuedDLCardUiState.Success).cards,
                        onBack = { navController.popBackStack() }
                    )
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
        
        // PersonsByRecordDate navigation
        composable(
            route = "person_details/{personId}",
            arguments = listOf(navArgument("personId") { type = NavType.IntType })
        ) { backStackEntry ->
            val personId = backStackEntry.arguments?.getInt("personId")
            if (personId != null) {
                val context = LocalContext.current
                val person by personsViewModel.observeDetailPerson(personId).collectAsState(initial = null)
                person?.let {
                    PersonsByRecordDateDetailsScreen(
                        person = it,
                        onBack = { navController.popBackStack() },
                        onToggleFavorite = { isFav ->
                            if (isFav) personsViewModel.addToFavorites(it.id) else personsViewModel.removeFromFavorites(it.id)
                        },
                        onShare = { shareText ->
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        }
                    )
                } ?: Text("Podatak nije pronađen.")
            } else {
                Text("Podatak nije pronađen.")
            }
        }
        
        composable("person_favorites") {
            val favorites by personsViewModel.observeFavorites().collectAsState(initial = emptyList())
            PersonsByRecordDateFavoritesScreen(
                favorites = favorites, 
                onPersonClick = { personId ->
                    navController.navigate("person_details/$personId")
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("person_chart") {
            val persons by personsViewModel.uiState.collectAsState()
            when (persons) {
                is PersonsByRecordDateUiState.Success -> {
                    PersonsByRecordDateChartScreen(
                        persons = (persons as PersonsByRecordDateUiState.Success).persons,
                        onBack = { navController.popBackStack() }
                    )
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
        
        // NewbornByRequestDate navigation
        composable(
            route = "newborn_details/{newbornId}",
            arguments = listOf(navArgument("newbornId") { type = NavType.IntType })
        ) { backStackEntry ->
            val newbornId = backStackEntry.arguments?.getInt("newbornId")
            if (newbornId != null) {
                val context = LocalContext.current
                val newborn by newbornsViewModel.observeDetailNewborn(newbornId).collectAsState(initial = null)
                newborn?.let {
                    NewbornByRequestDateDetailsScreen(
                        newborn = it,
                        onBack = { navController.popBackStack() },
                        onToggleFavorite = { isFav ->
                            if (isFav) newbornsViewModel.addToFavorites(it.id) else newbornsViewModel.removeFromFavorites(it.id)
                        },
                        onShare = { shareText ->
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, null)
                            context.startActivity(shareIntent)
                        }
                    )
                } ?: Text("Podatak nije pronađen.")
            } else {
                Text("Podatak nije pronađen.")
            }
        }
        composable("newborn_favorites") {
            val favorites by newbornsViewModel.observeFavorites().collectAsState(initial = emptyList())
            NewbornByRequestDateFavoritesScreen(
                favorites = favorites,
                onNewbornClick = { newbornId -> navController.navigate("newborn_details/$newbornId") },
                onBack = { navController.popBackStack() },
                onToggleFavorite = { id, isFav ->
                    if (isFav) newbornsViewModel.addToFavorites(id) else newbornsViewModel.removeFromFavorites(id)
                }
            )
        }
        composable("newborn_chart") {
            val newborns by newbornsViewModel.uiState.collectAsState()
            when (newborns) {
                is com.example.bihinsight.ui.screens.newborns.NewbornByRequestDateUiState.Success -> {
                    NewbornByRequestDateChartScreen(
                        newborns = (newborns as com.example.bihinsight.ui.screens.newborns.NewbornByRequestDateUiState.Success).newborns,
                        onBack = { navController.popBackStack() }
                    )
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
        composable("settings") {
            val context = LocalContext.current
            val prefs = context.getSharedPreferences("bihinsight_prefs", Context.MODE_PRIVATE)
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onThemeChanged = onThemeChanged
            )
        }
    }
} 