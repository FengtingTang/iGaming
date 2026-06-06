package com.igaming.localarcade.games.tetris

data class TetrisPoint(val x: Int, val y: Int)

enum class TetrominoType(val colorIndex: Int) {
    I(1), O(2), T(3), S(4), Z(5), J(6), L(7)
}

data class ActivePiece(
    val type: TetrominoType,
    val rotation: Int = 0,
    val origin: TetrisPoint = TetrisPoint(3, -1)
) {
    val cells: List<TetrisPoint>
        get() = shapeFor(type, rotation).map { TetrisPoint(it.x + origin.x, it.y + origin.y) }
}

data class TetrisGame(
    val width: Int = 10,
    val height: Int = 20,
    val board: List<Int> = List(width * height) { 0 },
    val active: ActivePiece = ActivePiece(TetrominoType.I),
    val score: Int = 0,
    val lines: Int = 0,
    val pieces: Int = 0,
    val isPaused: Boolean = false,
    val isGameOver: Boolean = false
) {
    val level: Int = lines / 10 + 1
    val tickMs: Long = (620L - (level - 1) * 48L).coerceAtLeast(160L)

    fun tick(): TetrisGame = softDrop()

    fun move(dx: Int): TetrisGame = tryMove(active.copy(origin = active.origin.copy(x = active.origin.x + dx)))

    fun rotate(): TetrisGame = tryMove(active.copy(rotation = (active.rotation + 1).mod(4)))

    fun softDrop(): TetrisGame {
        if (isPaused || isGameOver) return this
        val dropped = active.copy(origin = active.origin.copy(y = active.origin.y + 1))
        return if (canPlace(dropped)) copy(active = dropped) else lockPiece()
    }

    fun hardDrop(): TetrisGame {
        if (isPaused || isGameOver) return this
        var piece = active
        while (canPlace(piece.copy(origin = piece.origin.copy(y = piece.origin.y + 1)))) {
            piece = piece.copy(origin = piece.origin.copy(y = piece.origin.y + 1))
        }
        return copy(active = piece).lockPiece()
    }

    fun pause() = copy(isPaused = true)
    fun resume() = copy(isPaused = false)

    private fun tryMove(piece: ActivePiece): TetrisGame =
        if (!isPaused && !isGameOver && canPlace(piece)) copy(active = piece) else this

    private fun canPlace(piece: ActivePiece): Boolean =
        piece.cells.all { cell ->
            cell.x in 0 until width &&
                cell.y < height &&
                (cell.y < 0 || board[cell.y * width + cell.x] == 0)
        }

    private fun lockPiece(): TetrisGame {
        val nextBoard = board.toMutableList()
        active.cells.forEach { cell ->
            if (cell.y < 0) return copy(isGameOver = true)
            nextBoard[cell.y * width + cell.x] = active.type.colorIndex
        }
        val clearResult = clearLines(nextBoard)
        val nextLines = lines + clearResult.cleared
        val nextScore = score + when (clearResult.cleared) {
            1 -> 100
            2 -> 300
            3 -> 500
            4 -> 800
            else -> 0
        } * level
        val nextPiece = nextPiece(pieces + 1)
        val nextGame = copy(
            board = clearResult.board,
            active = nextPiece,
            score = nextScore,
            lines = nextLines,
            pieces = pieces + 1
        )
        return if (nextGame.canPlace(nextPiece)) nextGame else nextGame.copy(isGameOver = true)
    }
}

data class ClearResult(val board: List<Int>, val cleared: Int)

fun newTetrisGame() = TetrisGame()

fun clearLines(board: List<Int>, width: Int = 10, height: Int = 20): ClearResult {
    val rows = (0 until height).map { row -> board.subList(row * width, row * width + width) }
    val kept = rows.filterNot { row -> row.all { it != 0 } }
    val cleared = height - kept.size
    val emptyRows = List(cleared) { List(width) { 0 } }
    return ClearResult((emptyRows + kept).flatten(), cleared)
}

private fun nextPiece(index: Int): ActivePiece {
    val types = TetrominoType.entries
    return ActivePiece(type = types[(index * 5 + 2).mod(types.size)])
}

private fun shapeFor(type: TetrominoType, rotation: Int): List<TetrisPoint> {
    var points = when (type) {
        TetrominoType.I -> listOf(TetrisPoint(0, 1), TetrisPoint(1, 1), TetrisPoint(2, 1), TetrisPoint(3, 1))
        TetrominoType.O -> listOf(TetrisPoint(1, 0), TetrisPoint(2, 0), TetrisPoint(1, 1), TetrisPoint(2, 1))
        TetrominoType.T -> listOf(TetrisPoint(1, 0), TetrisPoint(0, 1), TetrisPoint(1, 1), TetrisPoint(2, 1))
        TetrominoType.S -> listOf(TetrisPoint(1, 0), TetrisPoint(2, 0), TetrisPoint(0, 1), TetrisPoint(1, 1))
        TetrominoType.Z -> listOf(TetrisPoint(0, 0), TetrisPoint(1, 0), TetrisPoint(1, 1), TetrisPoint(2, 1))
        TetrominoType.J -> listOf(TetrisPoint(0, 0), TetrisPoint(0, 1), TetrisPoint(1, 1), TetrisPoint(2, 1))
        TetrominoType.L -> listOf(TetrisPoint(2, 0), TetrisPoint(0, 1), TetrisPoint(1, 1), TetrisPoint(2, 1))
    }
    repeat(if (type == TetrominoType.O) 0 else rotation.mod(4)) {
        points = points.map { TetrisPoint(3 - it.y, it.x) }
    }
    return points
}
