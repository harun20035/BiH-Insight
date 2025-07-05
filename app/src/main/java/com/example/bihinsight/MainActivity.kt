package com.example.bihinsight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.bihinsight.ui.theme.BiHInsightTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.bihinsight.data.local.AppDatabase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.bihinsight.data.remote.IssuedDLCardApiService
import com.example.bihinsight.data.repository.IssuedDLCardRepository
import com.example.bihinsight.ui.screens.issueddlcards.IssuedDLCardScreen
import com.example.bihinsight.ui.screens.issueddlcards.IssuedDLCardViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.bihinsight.ui.screens.details.IssuedDLCardDetailsScreen
import com.example.bihinsight.ui.navigation.AppNavGraph
import androidx.lifecycle.SavedStateHandle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicijalizacija Room baze
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "bihinsight-db"
        ).fallbackToDestructiveMigration().build()

        // Inicijalizacija Retrofit servisa
        val retrofit = Retrofit.Builder()
            .baseUrl("https://odp.iddeea.gov.ba:8096/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(IssuedDLCardApiService::class.java)

        // Token za API (ako je potreban)
        val token: String? = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIyMjE3IiwibmJmIjoxNzUxNjQxOTExLCJleHAiOjE3NTE3MjgzMTEsImlhdCI6MTc1MTY0MTkxMX0.IFsPveqnb6X_E8qVyaVI-5bTK_9l-ZPTm24UUV31MB5jgRm2Uur3ZxLeor2jIqJh9xcyzk2e3BaZ_SIAKsoZ-Q"
        val languageId = 1

        // Inicijalizacija repository-ja
        val repository = IssuedDLCardRepository(apiService, db.issuedDLCardDao())

        setContent {
            BiHInsightTheme {
                Surface {
                    val navController = rememberNavController()
                    val viewModel: IssuedDLCardViewModel = viewModel(
                        factory = IssuedDLCardViewModelFactory(repository, token, languageId)
                    )
                    AppNavGraph(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

class IssuedDLCardViewModelFactory(
    private val repository: IssuedDLCardRepository,
    private val token: String?,
    private val languageId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IssuedDLCardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IssuedDLCardViewModel(
                savedStateHandle = SavedStateHandle(),
                repository = repository,
                token = token,
                languageId = languageId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}