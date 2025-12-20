package com.colormixlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.colormixlab.game.Difficulty
import com.colormixlab.game.GameViewModel
import com.colormixlab.ui.GameScreen
import com.colormixlab.ui.IntroScreen
import com.colormixlab.ui.MathChallengeScreen
import com.colormixlab.ui.theme.ColorMixLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            ColorMixLabTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf("intro") }
                    var selectedDifficulty by remember { mutableStateOf(Difficulty.MEDIUM) }
                    val viewModel: GameViewModel = viewModel()
                    
                    when (currentScreen) {
                        "intro" -> IntroScreen(
                            onStartGame = { difficulty ->
                                selectedDifficulty = difficulty
                                currentScreen = "mathChallenge"
                            }
                        )
                        "mathChallenge" -> MathChallengeScreen(
                            difficulty = selectedDifficulty,
                            onChallengeComplete = {
                                viewModel.setDifficulty(selectedDifficulty)
                                viewModel.resetGame()
                                currentScreen = "game"
                            }
                        )
                        "game" -> GameScreen(
                            viewModel = viewModel,
                            onNavigateToIntro = {
                                currentScreen = "intro"
                            }
                        )
                    }
                }
            }
        }
    }
}
