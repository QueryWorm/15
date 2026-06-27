package com.example.puzzle15

data class GameState(
    val tiles: List<Int> = (0..15).toList(),
    val moves: Int = 0,
    val isWin: Boolean = false
)
