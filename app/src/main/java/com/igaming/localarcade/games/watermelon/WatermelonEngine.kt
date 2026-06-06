package com.igaming.localarcade.games.watermelon

import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max

data class FruitSpec(val name: String, val radius: Float, val score: Int)

val fruitSpecs = listOf(
    FruitSpec("樱桃", 4.5f, 10),
    FruitSpec("草莓", 6f, 20),
    FruitSpec("葡萄", 7.5f, 35),
    FruitSpec("橙子", 9f, 55),
    FruitSpec("柠檬", 10.5f, 80),
    FruitSpec("猕猴桃", 12f, 120),
    FruitSpec("蜜瓜", 14f, 180),
    FruitSpec("西瓜", 16.5f, 260)
)

data class WatermelonFruit(
    val id: Int,
    val level: Int,
    val x: Float,
    val y: Float,
    val vx: Float = 0f,
    val vy: Float = 0f
) {
    val spec: FruitSpec get() = fruitSpecs[level]
}

data class WatermelonGame(
    val width: Float = 100f,
    val height: Float = 140f,
    val failLine: Float = 24f,
    val fruits: List<WatermelonFruit> = emptyList(),
    val aimX: Float = 50f,
    val nextLevel: Int = 0,
    val nextId: Int = 1,
    val score: Int = 0,
    val isPaused: Boolean = false,
    val isGameOver: Boolean = false
) {
    fun aim(delta: Float): WatermelonGame =
        copy(aimX = (aimX + delta).coerceIn(8f, width - 8f))

    fun aimAt(x: Float): WatermelonGame =
        copy(aimX = x.coerceIn(8f, width - 8f))

    fun drop(): WatermelonGame {
        if (isPaused || isGameOver) return this
        val fruit = WatermelonFruit(
            id = nextId,
            level = nextLevel,
            x = aimX,
            y = 8f,
            vy = 0.7f
        )
        return copy(
            fruits = fruits + fruit,
            nextLevel = (score / 70 + nextId).mod(3),
            nextId = nextId + 1
        )
    }

    fun tick(): WatermelonGame {
        if (isPaused || isGameOver) return this
        var current = fruits.map { fruit ->
            val radius = fruit.spec.radius
            val nextVy = (fruit.vy + 0.34f).coerceAtMost(4.2f)
            var nextX = fruit.x + fruit.vx
            var nextY = fruit.y + nextVy
            var vx = fruit.vx * 0.985f
            var vy = nextVy
            if (nextX - radius < 0f) {
                nextX = radius
                vx = abs(vx) * 0.35f
            }
            if (nextX + radius > width) {
                nextX = width - radius
                vx = -abs(vx) * 0.35f
            }
            if (nextY + radius > height) {
                nextY = height - radius
                vy = 0f
                vx *= 0.82f
            }
            fruit.copy(x = nextX, y = nextY, vx = vx, vy = vy)
        }.toMutableList()

        var gained = 0
        repeat(3) {
            val removed = mutableSetOf<Int>()
            val additions = mutableListOf<WatermelonFruit>()
            for (i in current.indices) {
                for (j in i + 1 until current.size) {
                    val a = current[i]
                    val b = current[j]
                    if (a.id in removed || b.id in removed) continue
                    val distance = max(0.01f, hypot(a.x - b.x, a.y - b.y))
                    val minDistance = a.spec.radius + b.spec.radius
                    if (distance < minDistance) {
                        if (a.level == b.level && a.level < fruitSpecs.lastIndex) {
                            val nextLevel = a.level + 1
                            gained += fruitSpecs[nextLevel].score
                            removed += a.id
                            removed += b.id
                            additions += WatermelonFruit(
                                id = nextId + additions.size,
                                level = nextLevel,
                                x = (a.x + b.x) / 2f,
                                y = (a.y + b.y) / 2f,
                                vy = -0.9f
                            )
                        } else {
                            val overlap = (minDistance - distance) / 2f
                            val nx = (a.x - b.x) / distance
                            val ny = (a.y - b.y) / distance
                            current[i] = a.copy(
                                x = (a.x + nx * overlap).coerceIn(a.spec.radius, width - a.spec.radius),
                                y = (a.y + ny * overlap).coerceIn(a.spec.radius, height - a.spec.radius),
                                vx = a.vx * 0.72f
                            )
                            current[j] = b.copy(
                                x = (b.x - nx * overlap).coerceIn(b.spec.radius, width - b.spec.radius),
                                y = (b.y - ny * overlap).coerceIn(b.spec.radius, height - b.spec.radius),
                                vx = b.vx * 0.72f
                            )
                        }
                    }
                }
            }
            if (removed.isNotEmpty()) {
                current = (current.filterNot { it.id in removed } + additions).toMutableList()
            }
        }

        val stableHighFruit = current.any { fruit ->
            fruit.y - fruit.spec.radius < failLine && abs(fruit.vy) < 0.1f && current.size > 6
        }
        return copy(
            fruits = current,
            nextId = nextId + current.count { it.id >= nextId },
            score = score + gained,
            isGameOver = stableHighFruit
        )
    }

    fun pause() = copy(isPaused = true)
    fun resume() = copy(isPaused = false)
}

fun newWatermelonGame() = WatermelonGame()
