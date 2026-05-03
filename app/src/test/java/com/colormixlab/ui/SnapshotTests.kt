package com.colormixlab.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.colormixlab.model.GameColor
import com.colormixlab.model.PlatformColor
import com.colormixlab.ui.components.ColorButton
import com.colormixlab.ui.components.MathAnswerButton
import com.colormixlab.ui.components.MixingBowl
import com.colormixlab.ui.components.TargetColor
import com.colormixlab.ui.dialogs.ResultDialogContent
import com.colormixlab.ui.theme.ColorMixLabTheme
import org.junit.Rule
import org.junit.Test

/**
 * Paparazzi snapshot tests for the visual components that drive the gameplay
 * surface. These render each composable in a fixed device configuration and
 * compare against a recorded golden image, catching unintended visual
 * regressions in animation refactors and theming changes.
 *
 * Recording goldens locally:  ./gradlew :app:recordPaparazziDebug
 * Verifying current goldens:  ./gradlew :app:verifyPaparazziDebug
 *
 * Goldens live at app/src/test/snapshots/. Commit them alongside test changes.
 */
class SnapshotTests {

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_5)

    @Test
    fun mixingBowl_empty() {
        paparazzi.snapshot {
            Themed {
                Box(modifier = Modifier.padding(24.dp)) {
                    MixingBowl(
                        drops = emptyMap(),
                        mixedColor = PlatformColor.White
                    )
                }
            }
        }
    }

    @Test
    fun mixingBowl_threeColors() {
        paparazzi.snapshot {
            Themed {
                Box(modifier = Modifier.padding(24.dp)) {
                    MixingBowl(
                        drops = mapOf(
                            GameColor.Red to 2,
                            GameColor.Blue to 1,
                            GameColor.Yellow to 1
                        ),
                        mixedColor = PlatformColor(150, 90, 60)
                    )
                }
            }
        }
    }

    @Test
    fun colorButton_noDrops() {
        paparazzi.snapshot {
            Themed {
                Box(modifier = Modifier.padding(16.dp)) {
                    ColorButton(color = GameColor.Red, dropCount = 0, onClick = {})
                }
            }
        }
    }

    @Test
    fun colorButton_withBadge() {
        paparazzi.snapshot {
            Themed {
                Box(modifier = Modifier.padding(16.dp)) {
                    ColorButton(color = GameColor.Blue, dropCount = 3, onClick = {})
                }
            }
        }
    }

    @Test
    fun targetColor_orange() {
        paparazzi.snapshot {
            Themed {
                Box(modifier = Modifier.padding(16.dp)) {
                    TargetColor(targetColor = PlatformColor(230, 126, 34))
                }
            }
        }
    }

    @Test
    fun mathAnswer_idle() {
        paparazzi.snapshot {
            Themed {
                Box(modifier = Modifier.padding(16.dp).size(120.dp, 80.dp)) {
                    MathAnswerButton(
                        answer = 42,
                        isCorrect = false,
                        isSelected = false,
                        showingAnswer = false,
                        fontSize = 32.sp,
                        modifier = Modifier,
                        onClick = {}
                    )
                }
            }
        }
    }

    @Test
    fun mathAnswer_correctSelected() {
        paparazzi.snapshot {
            Themed {
                Box(modifier = Modifier.padding(16.dp).size(120.dp, 80.dp)) {
                    MathAnswerButton(
                        answer = 42,
                        isCorrect = true,
                        isSelected = true,
                        showingAnswer = true,
                        fontSize = 32.sp,
                        modifier = Modifier,
                        onClick = {}
                    )
                }
            }
        }
    }

    @Test
    fun mathAnswer_wrongSelected() {
        paparazzi.snapshot {
            Themed {
                Box(modifier = Modifier.padding(16.dp).size(120.dp, 80.dp)) {
                    MathAnswerButton(
                        answer = 41,
                        isCorrect = false,
                        isSelected = true,
                        showingAnswer = true,
                        fontSize = 32.sp,
                        modifier = Modifier,
                        onClick = {}
                    )
                }
            }
        }
    }

    @Test
    fun resultDialog_success() {
        paparazzi.snapshot {
            Themed {
                ResultDialogContent(
                    similarity = 0.92f,
                    isSuccess = true,
                    level = 2,
                    basePoints = 120,
                    timeBonus = 25,
                    unlockedColors = listOf(GameColor.Red, GameColor.Blue, GameColor.Green),
                    getMessage = { "Great Match!" },
                    getEmoji = { "🎉" },
                    onAction = {}
                )
            }
        }
    }

    @Test
    fun resultDialog_failure() {
        paparazzi.snapshot {
            Themed {
                ResultDialogContent(
                    similarity = 0.62f,
                    isSuccess = false,
                    level = 5,
                    basePoints = -25,
                    timeBonus = 0,
                    unlockedColors = listOf(GameColor.Red, GameColor.Blue, GameColor.Green, GameColor.Yellow),
                    getMessage = { "Not quite — try again" },
                    getEmoji = { "😅" },
                    onAction = {}
                )
            }
        }
    }
}

@Composable
private fun Themed(content: @Composable () -> Unit) {
    ColorMixLabTheme(darkTheme = false, content = content)
}
