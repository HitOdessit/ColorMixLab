package com.colormixlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.colormixlab.ui.GameScreen
import com.colormixlab.ui.WelcomeScreen
import com.colormixlab.ui.theme.ColorMixLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if first launch
        val prefs = getSharedPreferences("game_prefs", MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("is_first_launch", true)
        
        setContent {
            ColorMixLabTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showWelcome by remember { mutableStateOf(isFirstLaunch) }
                    
                    if (showWelcome) {
                        WelcomeScreen(
                            onStartGame = {
                                showWelcome = false
                                // Mark as not first launch
                                prefs.edit().putBoolean("is_first_launch", false).apply()
                            }
                        )
                    } else {
                        GameScreen()
                    }
                }
            }
        }
    }
}

