package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NoInternetScreen
import com.example.ui.theme.SuperAceTheme
import com.example.ui.viewmodel.AppScreenState
import com.example.ui.viewmodel.GameViewModel
import com.example.util.NetworkMonitor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SuperAceTheme {
                val isOnline by remember { NetworkMonitor.observeNetwork(this) }
                    .collectAsState(initial = NetworkMonitor.isOnline(this))
                var manualRetryTrigger by remember { mutableStateOf(0) }

                if (!isOnline) {
                    NoInternetScreen(
                        onRetry = {
                            manualRetryTrigger++
                        }
                    )
                } else {
                    val gameViewModel: GameViewModel = viewModel()
                    val uiState by gameViewModel.uiState.collectAsState()

                    Crossfade(
                        targetState = uiState.currentScreen,
                        animationSpec = tween(300),
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            AppScreenState.HOME_LOBBY -> {
                                HomeScreen(viewModel = gameViewModel)
                            }
                            AppScreenState.GAME_PLAY -> {
                                GameScreen(viewModel = gameViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
