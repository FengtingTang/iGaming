package com.igaming.localarcade.games.twenty

enum class TwentyMove {
    Up, Down, Left, Right
}

data class TwentyGame(
    val board: List<Int> = List(16) { 0 }.placeTile(0, 2).placeTile(5, 2),
    val score: Int = 0,
    val isPaused: Boolean = false,
    val isGameOver: Boolean = false,
    val hasWon: Boolean = false,
    val moves: Int = 0
) {
    fun move(direction: TwentyMove): TwentyGame {
        if (isPaused || isGameOver) return this
        val rows = when (direction) {
            TwentyMove.Left -> List(4) { row -> rowCells(row) }
            TwentyMove.Right -> List(4) { row -> rowCells(row).reversed() }
            TwentyMove.Up -> List(4) { col -> columnCells(col) }
            TwentyMove.Down -> List(4) { col -> columnCells(col).reversed() }
        }
        var gained = 0
        val collapsed = rows.map { line ->
            val result = mergeLine(line.map { board[it] })
            gained += result.score
            result.values
        }
        val nextBoard = MutableList(16) { 0 }
        rows.forEachIndexed { index, cells ->
            val values = collapsed[index]
            cells.forEachIndexed { cellIndex, boardIndex ->
                nextBoard[boardIndex] = values[cellIndex]
            }
        }
        if (nextBoard == board) return this
        val nextScore = score + gained
        val withTile = nextBoard.placeDeterministicTile(nextScore + moves)
        return copy(
            board = withTile,
            score = nextScore,
            hasWon = withTile.any { it >= 2048 },
            isGameOver = !withTile.canMove(),
            moves = moves + 1
        )
    }

    fun pause() = copy(isPaused = true)
    fun resume() = copy(isPaused = false)
}

data class MergeResult(val values: List<Int>, val score: Int)

fun newTwentyGame() = TwentyGame()

fun mergeLine(values: List<Int>): MergeResult {
    val compact = values.filter { it != 0 }
    val merged = mutableListOf<Int>()
    var score = 0
    var index = 0
    while (index < compact.size) {
        val value = compact[index]
        if (index + 1 < compact.size && compact[index + 1] == value) {
            val doubled = value * 2
            merged += doubled
            score += doubled
            index += 2
        } else {
            merged += value
            index += 1
        }
    }
    while (merged.size < 4) merged += 0
    return MergeResult(merged, score)
}

private fun rowCells(row: Int) = List(4) { col -> row * 4 + col }
private fun columnCells(col: Int) = List(4) { row -> row * 4 + col }

private fun List<Int>.placeTile(index: Int, value: Int): List<Int> =
    toMutableList().also { it[index] = value }

private fun List<Int>.placeDeterministicTile(seed: Int): List<Int> {
    val empty = indices.filter { this[it] == 0 }
    if (empty.isEmpty()) return this
    val index = empty[(seed * 37 + 11).mod(empty.size)]
    val value = if (seed % 7 == 0) 4 else 2
    return placeTile(index, value)
}

private fun List<Int>.canMove(): Boolean {
    if (any { it == 0 }) return true
    for (row in 0 until 4) {
        for (col in 0 until 4) {
            val value = this[row * 4 + col]
            if (col < 3 && this[row * 4 + col + 1] == value) return true
            if (row < 3 && this[(row + 1) * 4 + col] == value) return true
        }
    }
    return false
}
