package com.igaming.localarcade.games.common

interface GameStateContract {
    val score: Int
    val bestScore: Int
    val isPaused: Boolean
    val isGameOver: Boolean
    fun restart()
    fun pause()
    fun resume()
}
