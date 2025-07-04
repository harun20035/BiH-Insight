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

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: IssuedDLCardViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "list",
        modifier = modifier
    ) {
        composable("list") {
            IssuedDLCardScreen(viewModel, onCardClick = { cardId ->
                navController.navigate("details/$cardId")
            })
        }
        composable(
            route = "details/{cardId}",
            arguments = listOf(navArgument("cardId") { type = NavType.IntType })
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getInt("cardId")
            val card = viewModel.uiState.value.let { state ->
                if (state is IssuedDLCardUiState.Success) {
                    state.cards.find { it.id == cardId }
                } else null
            }
            if (card != null) {
                IssuedDLCardDetailsScreen(card, onBack = { navController.popBackStack() })
            } else {
                Text("Podatak nije pronađen.")
            }
        }
    }
} 