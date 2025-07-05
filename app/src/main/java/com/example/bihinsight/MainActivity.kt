package com.example.bihinsight

import android.content.res.Configuration
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
import com.example.bihinsight.data.remote.PersonsByRecordDateApiService
import com.example.bihinsight.data.remote.NewbornByRequestDateApiService
import com.example.bihinsight.data.repository.IssuedDLCardRepository
import com.example.bihinsight.data.repository.PersonsByRecordDateRepository
import com.example.bihinsight.data.repository.NewbornByRequestDateRepository
import com.example.bihinsight.ui.screens.issueddlcards.IssuedDLCardScreen
import com.example.bihinsight.ui.screens.issueddlcards.IssuedDLCardUiState
import com.example.bihinsight.ui.screens.issueddlcards.IssuedDLCardViewModel
import com.example.bihinsight.ui.screens.personsbyrecorddate.PersonsByRecordDateViewModel
import com.example.bihinsight.ui.screens.newborns.NewbornByRequestDateViewModel
import com.example.bihinsight.ui.screens.details.IssuedDLCardDetailsScreen
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.bihinsight.ui.navigation.AppNavGraph
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

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
        val issuedDLCardApiService = retrofit.create(IssuedDLCardApiService::class.java)
        val personsByRecordDateApiService = retrofit.create(PersonsByRecordDateApiService::class.java)
        val newbornByRequestDateApiService = retrofit.create(NewbornByRequestDateApiService::class.java)

        // Token za API - čita se iz BuildConfig
        val token: String? = if (BuildConfig.API_TOKEN.isNotEmpty()) BuildConfig.API_TOKEN else null
        val languageId = 1

        // Inicijalizacija repository-ja
        val issuedDLCardRepository = IssuedDLCardRepository(issuedDLCardApiService, db.issuedDLCardDao())
        val personsByRecordDateRepository = PersonsByRecordDateRepository(personsByRecordDateApiService, db.personsByRecordDateDao())
        val newbornByRequestDateRepository = NewbornByRequestDateRepository(newbornByRequestDateApiService, db.newbornByRequestDateDao())

        setContent {
            val context = LocalContext.current
            val prefs = context.getSharedPreferences("bihinsight_prefs", Context.MODE_PRIVATE)
            var isDarkMode by remember { mutableStateOf(prefs.getBoolean("dark_mode", false)) }
            
            BiHInsightTheme(darkTheme = isDarkMode) {
                Surface {
                    val navController = rememberNavController()
                    val issuedDLCardViewModel: IssuedDLCardViewModel = viewModel(
                        factory = IssuedDLCardViewModelFactory(issuedDLCardRepository, token, languageId)
                    )
                    val personsByRecordDateViewModel: PersonsByRecordDateViewModel = viewModel(
                        factory = PersonsByRecordDateViewModelFactory(personsByRecordDateRepository, token, languageId)
                    )
                    val newbornByRequestDateViewModel: NewbornByRequestDateViewModel = viewModel(
                        factory = NewbornByRequestDateViewModelFactory(newbornByRequestDateRepository, token, languageId)
                    )
                    AppNavGraph(
                        navController = navController,
                        viewModel = issuedDLCardViewModel,
                        personsViewModel = personsByRecordDateViewModel,
                        newbornsViewModel = newbornByRequestDateViewModel,
                        onThemeChanged = { newTheme ->
                            isDarkMode = newTheme
                        }
                    )
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        
        // Log konfiguracijske promjene
        val orientation = when (newConfig.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> "Landscape"
            Configuration.ORIENTATION_PORTRAIT -> "Portrait"
            else -> "Unknown"
        }
        
        val screenSize = when (newConfig.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) {
            Configuration.SCREENLAYOUT_SIZE_SMALL -> "Small"
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> "Normal"
            Configuration.SCREENLAYOUT_SIZE_LARGE -> "Large"
            Configuration.SCREENLAYOUT_SIZE_XLARGE -> "XLarge"
            else -> "Unknown"
        }
        
        val uiMode = when (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> "Light Theme"
            Configuration.UI_MODE_NIGHT_YES -> "Dark Theme"
            else -> "Unknown"
        }
        
        android.util.Log.d("ConfigChange", "Orientation: $orientation, Screen: $screenSize, Theme: $uiMode")
        
        // Ovdje možete dodati dodatnu logiku za obradu promjena
        // Na primjer, ažuriranje UI-a, ponovno učitavanje podataka, itd.
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

class PersonsByRecordDateViewModelFactory(
    private val repository: PersonsByRecordDateRepository,
    private val token: String?,
    private val languageId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PersonsByRecordDateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PersonsByRecordDateViewModel(
                savedStateHandle = SavedStateHandle(),
                repository = repository,
                token = token,
                languageId = languageId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class NewbornByRequestDateViewModelFactory(
    private val repository: NewbornByRequestDateRepository,
    private val token: String?,
    private val languageId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewbornByRequestDateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewbornByRequestDateViewModel(
                savedStateHandle = SavedStateHandle(),
                repository = repository,
                token = token,
                languageId = languageId
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}