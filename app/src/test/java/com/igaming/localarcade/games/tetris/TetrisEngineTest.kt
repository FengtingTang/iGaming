package com.igaming.localarcade.games.tetris

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TetrisEngineTest {
    @Test
    fun clearLinesRemovesFullRows() {
        val board = MutableList(200) { 0 }
        for (x in 0 until 10) board[19 * 10 + x] = 1

        val result = clearLines(board)

        assertEquals(1, result.cleared)
        assertEquals(200, result.board.size)
        assertTrue(result.board.take(10).all { it == 0 })
    }

    @Test
    fun pieceCanMoveHorizontally() {
        val game = newTetrisGame()
        val moved = game.move(1)

        assertEquals(game.active.origin.x + 1, moved.active.origin.x)
    }

    @Test
    fun hardDropLocksPieceAndSpawnsNext() {
        val game = newTetrisGame()
        val next = game.hardDrop()

        assertFalse(next.isGameOver)
        assertEquals(1, next.pieces)
        assertTrue(next.board.any { it != 0 })
    }
}
