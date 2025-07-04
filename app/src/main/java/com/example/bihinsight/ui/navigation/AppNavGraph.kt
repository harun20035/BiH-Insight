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

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: IssuedDLCardViewModel,
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
            IssuedDLCardScreen(
                viewModel,
                onCardClick = { cardId -> navController.navigate("details/$cardId") },
                onFavoritesClick = { navController.navigate("favorites") }
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
            FavoritesScreen(favorites = favorites, onCardClick = { cardId ->
                navController.navigate("details/$cardId")
            })
        }
    }
} 