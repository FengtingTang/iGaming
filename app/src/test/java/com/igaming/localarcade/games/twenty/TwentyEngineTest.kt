package com.igaming.localarcade.games.twenty

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TwentyEngineTest {
    @Test
    fun mergeLineCombinesPairsOnce() {
        val result = mergeLine(listOf(2, 2, 2, 2))

        assertEquals(listOf(4, 4, 0, 0), result.values)
        assertEquals(8, result.score)
    }

    @Test
    fun leftMoveChangesBoardAndAddsScore() {
        val game = TwentyGame(
            board = listOf(
                2, 2, 0, 0,
                4, 0, 4, 0,
                0, 0, 0, 0,
                0, 0, 0, 0
            )
        )

        val next = game.move(TwentyMove.Left)

        assertEquals(12, next.score)
        assertTrue(next.board[0] == 4)
        assertTrue(next.board[4] == 8)
    }

    @Test
    fun fullBoardWithoutMatchesIsGameOverAfterMove() {
        val game = TwentyGame(
            board = listOf(
                0, 2, 4, 8,
                16, 32, 64, 128,
                2, 4, 8, 16,
                32, 64, 128, 256
            )
        )

        val next = game.move(TwentyMove.Left)

        assertTrue(next.isGameOver)
    }
}
