package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AdminAppScreen
import com.example.ui.screens.NoInternetScreen
import com.example.ui.theme.SuperAceTheme
import com.example.ui.viewmodel.AdminViewModel
import com.example.util.NetworkMonitor

class AdminActivity : ComponentActivity() {
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
                    val adminViewModel: AdminViewModel = viewModel()
                    AdminAppScreen(viewModel = adminViewModel)
                }
            }
        }
    }
}
