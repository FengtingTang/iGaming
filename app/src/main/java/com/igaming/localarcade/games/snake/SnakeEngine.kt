package com.igaming.localarcade.games.snake

data class SnakeCell(val x: Int, val y: Int)

enum class SnakeDirection {
    Up, Down, Left, Right
}

data class SnakeGame(
    val width: Int = 18,
    val height: Int = 22,
    val snake: List<SnakeCell> = listOf(SnakeCell(8, 11), SnakeCell(7, 11), SnakeCell(6, 11)),
    val food: SnakeCell = SnakeCell(13, 11),
    val direction: SnakeDirection = SnakeDirection.Right,
    val pendingDirection: SnakeDirection = SnakeDirection.Right,
    val score: Int = 0,
    val isPaused: Boolean = false,
    val isGameOver: Boolean = false
) {
    val tickMs: Long = (210L - (score / 50) * 12L).coerceAtLeast(90L)

    fun turn(direction: SnakeDirection): SnakeGame {
        if (direction.isOppositeOf(this.direction)) return this
        return copy(pendingDirection = direction)
    }

    fun tick(): SnakeGame {
        if (isPaused || isGameOver) return this
        val nextHead = snake.first().move(pendingDirection)
        val eating = nextHead == food
        val bodyForCollision = if (eating) snake else snake.dropLast(1)
        if (nextHead.x !in 0 until width || nextHead.y !in 0 until height || nextHead in bodyForCollision) {
            return copy(isGameOver = true)
        }
        val grownSnake = if (eating) listOf(nextHead) + snake else listOf(nextHead) + snake.dropLast(1)
        val nextScore = if (eating) score + 10 else score
        return copy(
            snake = grownSnake,
            food = if (eating) nextFood(grownSnake, nextScore) else food,
            direction = pendingDirection,
            score = nextScore
        )
    }

    fun pause() = copy(isPaused = true)
    fun resume() = copy(isPaused = false)
}

fun newSnakeGame() = SnakeGame()

private fun SnakeCell.move(direction: SnakeDirection) = when (direction) {
    SnakeDirection.Up -> copy(y = y - 1)
    SnakeDirection.Down -> copy(y = y + 1)
    SnakeDirection.Left -> copy(x = x - 1)
    SnakeDirection.Right -> copy(x = x + 1)
}

private fun SnakeDirection.isOppositeOf(other: SnakeDirection) =
    (this == SnakeDirection.Up && other == SnakeDirection.Down) ||
        (this == SnakeDirection.Down && other == SnakeDirection.Up) ||
        (this == SnakeDirection.Left && other == SnakeDirection.Right) ||
        (this == SnakeDirection.Right && other == SnakeDirection.Left)

private fun SnakeGame.nextFood(currentSnake: List<SnakeCell>, nextScore: Int): SnakeCell {
    val occupied = currentSnake.toSet()
    val available = buildList {
        for (y in 0 until height) {
            for (x in 0 until width) {
                val cell = SnakeCell(x, y)
                if (cell !in occupied) add(cell)
            }
        }
    }
    if (available.isEmpty()) return food
    val head = currentSnake.first()
    val index = (nextScore * 31 + head.x * 7 + head.y * 13).mod(available.size)
    return available[index]
}
