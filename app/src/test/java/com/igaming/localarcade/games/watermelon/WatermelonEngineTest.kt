package com.igaming.localarcade.games.watermelon

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WatermelonEngineTest {
    @Test
    fun droppingAddsFruit() {
        val game = newWatermelonGame().drop()

        assertEquals(1, game.fruits.size)
    }

    @Test
    fun matchingFruitsMergeAndScore() {
        val game = WatermelonGame(
            fruits = listOf(
                WatermelonFruit(id = 1, level = 0, x = 40f, y = 80f),
                WatermelonFruit(id = 2, level = 0, x = 43f, y = 80f)
            ),
            nextId = 3
        )

        val next = game.tick()

        assertEquals(1, next.fruits.size)
        assertEquals(1, next.fruits.first().level)
        assertTrue(next.score > 0)
    }

    @Test
    fun aimStaysInsideBoard() {
        val game = newWatermelonGame().aim(-500f)

        assertEquals(8f, game.aimX)
    }
}
