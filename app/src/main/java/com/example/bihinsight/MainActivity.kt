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
import com.example.bihinsight.ui.screens.registrations.IssuedDLCardScreen
import com.example.bihinsight.ui.screens.registrations.IssuedDLCardViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicijalizacija Room baze
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "bihinsight-db"
        ).build()

        // Inicijalizacija Retrofit servisa
        val retrofit = Retrofit.Builder()
            .baseUrl("https://odp.iddeea.gov.ba:8096/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiService = retrofit.create(IssuedDLCardApiService::class.java)

        // Token za API (ako je potreban)
        val token: String? = "Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIyMjE3IiwibmJmIjoxNzUxNTc0Nzc0LCJleHAiOjE3NTE2NjExNzQsImlhdCI6MTc1MTU3NDc3NH0.bl97mbiYoIuhExztq9M-W0364mAWPrpIHaXxdi5RT5FbfkWtjmFspcuR7-MYaTIWbNO4ibMsG3WHK-q5UGbZOA"
        val languageId = 1

        // Inicijalizacija repository-ja
        val repository = IssuedDLCardRepository(apiService, db.issuedDLCardDao())

        setContent {
            BiHInsightTheme {
                Surface {
                    val viewModel: IssuedDLCardViewModel = viewModel(
                        factory = IssuedDLCardViewModelFactory(repository, token, languageId)
                    )
                    IssuedDLCardScreen(viewModel)
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
            return IssuedDLCardViewModel(repository, token, languageId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}