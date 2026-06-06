package com.igaming.localarcade.games.snake

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SnakeEngineTest {
    @Test
    fun eatingFoodGrowsSnakeAndScores() {
        val game = SnakeGame(
            snake = listOf(SnakeCell(1, 1), SnakeCell(0, 1)),
            food = SnakeCell(2, 1),
            direction = SnakeDirection.Right,
            pendingDirection = SnakeDirection.Right
        )

        val next = game.tick()

        assertEquals(10, next.score)
        assertEquals(3, next.snake.size)
        assertFalse(next.isGameOver)
    }

    @Test
    fun hittingWallEndsGame() {
        val game = SnakeGame(
            snake = listOf(SnakeCell(0, 0)),
            direction = SnakeDirection.Left,
            pendingDirection = SnakeDirection.Left
        )

        assertTrue(game.tick().isGameOver)
    }

    @Test
    fun cannotReverseDirectionImmediately() {
        val game = newSnakeGame().turn(SnakeDirection.Left)

        assertEquals(SnakeDirection.Right, game.pendingDirection)
    }
}
