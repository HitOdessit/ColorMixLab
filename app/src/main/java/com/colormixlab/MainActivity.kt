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

/**
 * Top-level routes in the app. Using a sealed hierarchy gives compile-time
 * exhaustiveness checks on the navigation `when` block — a typo or new screen
 * cannot silently break routing.
 */
sealed interface Screen {
    data object Intro : Screen
    data object MathChallenge : Screen
    data object Game : Screen
}

/** Single Activity hosting Compose-based navigation between [Screen] destinations. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ColorMixLabTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf<Screen>(Screen.Intro) }
                    var selectedDifficulty by remember { mutableStateOf(Difficulty.MEDIUM) }
                    val viewModel: GameViewModel = viewModel()

                    when (currentScreen) {
                        Screen.Intro -> IntroScreen(
                            onStartGame = { difficulty ->
                                selectedDifficulty = difficulty
                                currentScreen = Screen.MathChallenge
                            }
                        )
                        Screen.MathChallenge -> MathChallengeScreen(
                            difficulty = selectedDifficulty,
                            onChallengeComplete = {
                                viewModel.setDifficulty(selectedDifficulty)
                                viewModel.resetGame()
                                currentScreen = Screen.Game
                            },
                            onBack = {
                                currentScreen = Screen.Intro
                            }
                        )
                        Screen.Game -> GameScreen(
                            viewModel = viewModel,
                            onNavigateToIntro = {
                                currentScreen = Screen.Intro
                            }
                        )
                    }
                }
            }
        }
    }
}
