package com.guidovezzoni.sfta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.guidovezzoni.sfta.ui.intent.DashboardUiIntent
import com.guidovezzoni.sfta.ui.screens.DashboardScreen
import com.guidovezzoni.sfta.ui.theme.StarkFutureTechnicalAssessmentTheme
import com.guidovezzoni.sfta.ui.viewmodel.DashboardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StarkFutureTechnicalAssessmentTheme {
                val viewModel: DashboardViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    viewModel.onIntent(DashboardUiIntent.LoadDashboard)
                }

                DashboardScreen(
                    uiState = uiState,
                    onIntent = viewModel::onIntent,
                )
            }
        }
    }
}
