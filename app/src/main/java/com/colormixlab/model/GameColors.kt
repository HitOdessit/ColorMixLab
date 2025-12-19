package com.colormixlab.model

import androidx.compose.ui.graphics.Color

sealed class GameColor(val name: String, val rgb: Color, val unlockLevel: Int) {
    object Red : GameColor("Red", Color(0xFFE74C3C), 1)
    object Blue : GameColor("Blue", Color(0xFF3498DB), 1)
    object Green : GameColor("Green", Color(0xFF2ECC71), 1)
    object Yellow : GameColor("Yellow", Color(0xFFF1C40F), 4)
    object Orange : GameColor("Orange", Color(0xFFE67E22), 7)
    object Purple : GameColor("Purple", Color(0xFF9B59B6), 10)
    object Pink : GameColor("Pink", Color(0xFFE91E63), 13)
    object White : GameColor("White", Color(0xFFFFFFFF), 16)
    object Black : GameColor("Black", Color(0xFF000000), 19)
    
    companion object {
        fun getAllColors() = listOf(Red, Blue, Green, Yellow, Orange, Purple, Pink, White, Black)
        
        fun getAvailableColors(level: Int): List<GameColor> {
            return getAllColors().filter { it.unlockLevel <= level }
        }
    }
}

