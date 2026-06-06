package com.igaming.localarcade.games.snake

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.igaming.localarcade.data.ScoreRepository
import com.igaming.localarcade.data.gameCatalog
import com.igaming.localarcade.games.common.GameScaffold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Composable
fun SnakeScreen(onBack: () -> Unit) {
    val definition = gameCatalog.first { it.id == "snake" }
    val context = LocalContext.current
    val repository = remember { ScoreRepository(context.applicationContext) }
    val bestScore by repository.bestScore(definition.bestScoreKey).collectAsState(initial = 0)
    val scope = rememberCoroutineScope()
    var game by remember { mutableStateOf(newSnakeGame()) }

    LaunchedEffect(game.isPaused, game.isGameOver, game.score) {
        while (!game.isPaused && !game.isGameOver) {
            delay(game.tickMs)
            game = game.tick()
        }
    }

    LaunchedEffect(game.isGameOver, game.score) {
        if (game.isGameOver) repository.submitScore(definition.bestScoreKey, game.score)
    }

    GameScaffold(
        title = definition.title,
        score = game.score,
        bestScore = max(bestScore, game.score),
        isPaused = game.isPaused,
        isGameOver = game.isGameOver,
        onBack = onBack,
        onRestart = {
            scope.launch { repository.submitScore(definition.bestScoreKey, game.score) }
            game = newSnakeGame()
        },
        onPauseToggle = { game = if (game.isPaused) game.resume() else game.pause() }
    ) { modifier ->
        SnakeBoard(
            game = game,
            modifier = modifier,
            onTurn = { game = game.turn(it) }
        )
    }
}

@Composable
private fun SnakeBoard(game: SnakeGame, modifier: Modifier, onTurn: (SnakeDirection) -> Unit) {
    val boardColor = MaterialTheme.colorScheme.surfaceVariant
    val snakeColor = Color(0xFF21A67A)
    val foodColor = Color(0xFFE95F78)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(boardColor, RoundedCornerShape(8.dp))
            .padding(8.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val direction = if (abs(dragAmount.x) > abs(dragAmount.y)) {
                        if (dragAmount.x > 0) SnakeDirection.Right else SnakeDirection.Left
                    } else {
                        if (dragAmount.y > 0) SnakeDirection.Down else SnakeDirection.Up
                    }
                    onTurn(direction)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cell = min(size.width / game.width, size.height / game.height)
            val left = (size.width - cell * game.width) / 2f
            val top = (size.height - cell * game.height) / 2f
            game.snake.forEachIndexed { index, snakeCell ->
                drawRoundRect(
                    color = if (index == 0) Color(0xFF006D43) else snakeColor,
                    topLeft = Offset(left + snakeCell.x * cell + 2f, top + snakeCell.y * cell + 2f),
                    size = Size(cell - 4f, cell - 4f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(7f, 7f)
                )
            }
            drawCircle(
                color = foodColor,
                radius = cell * 0.38f,
                center = Offset(left + game.food.x * cell + cell / 2f, top + game.food.y * cell + cell / 2f)
            )
        }
    }
}
