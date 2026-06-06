package com.igaming.localarcade.games.tetris

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.RotateRight
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.South
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.igaming.localarcade.data.ScoreRepository
import com.igaming.localarcade.data.gameCatalog
import com.igaming.localarcade.games.common.GameScaffold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

@Composable
fun TetrisScreen(onBack: () -> Unit) {
    val definition = gameCatalog.first { it.id == "tetris" }
    val context = LocalContext.current
    val repository = remember { ScoreRepository(context.applicationContext) }
    val bestScore by repository.bestScore(definition.bestScoreKey).collectAsState(initial = 0)
    val scope = rememberCoroutineScope()
    var game by remember { mutableStateOf(newTetrisGame()) }

    LaunchedEffect(game.isPaused, game.isGameOver, game.level) {
        while (!game.isPaused && !game.isGameOver) {
            delay(game.tickMs)
            game = game.tick()
        }
    }

    LaunchedEffect(game.isGameOver, game.score) {
        if (game.isGameOver) repository.submitScore(definition.bestScoreKey, game.score)
    }

    GameScaffold(
        title = "${definition.title}  Lv.${game.level}",
        score = game.score,
        bestScore = max(bestScore, game.score),
        isPaused = game.isPaused,
        isGameOver = game.isGameOver,
        onBack = onBack,
        onRestart = {
            scope.launch { repository.submitScore(definition.bestScoreKey, game.score) }
            game = newTetrisGame()
        },
        onPauseToggle = { game = if (game.isPaused) game.resume() else game.pause() }
    ) { modifier ->
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            TetrisBoard(game = game, modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilledTonalIconButton(onClick = { game = game.move(-1) }) {
                    Icon(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, contentDescription = "左移")
                }
                FilledTonalIconButton(onClick = { game = game.rotate() }) {
                    Icon(Icons.AutoMirrored.Rounded.RotateRight, contentDescription = "旋转")
                }
                FilledTonalIconButton(onClick = { game = game.move(1) }) {
                    Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = "右移")
                }
                FilledTonalIconButton(onClick = { game = game.softDrop() }) {
                    Icon(Icons.Rounded.ArrowDownward, contentDescription = "软降")
                }
                FilledTonalIconButton(onClick = { game = game.hardDrop() }) {
                    Icon(Icons.Rounded.South, contentDescription = "硬降")
                }
            }
            Text("已消除 ${game.lines} 行", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun TetrisBoard(game: TetrisGame, modifier: Modifier) {
    val boardColor = MaterialTheme.colorScheme.surfaceVariant
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(boardColor, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cell = min(size.width / game.width, size.height / game.height)
            val left = (size.width - cell * game.width) / 2f
            val top = (size.height - cell * game.height) / 2f
            for (y in 0 until game.height) {
                for (x in 0 until game.width) {
                    val colorIndex = game.board[y * game.width + x]
                    if (colorIndex != 0) {
                        drawBlock(colorIndex, left + x * cell, top + y * cell, cell)
                    }
                }
            }
            game.active.cells.filter { it.y >= 0 }.forEach { cellPoint ->
                drawBlock(
                    game.active.type.colorIndex,
                    left + cellPoint.x * cell,
                    top + cellPoint.y * cell,
                    cell
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBlock(index: Int, x: Float, y: Float, cell: Float) {
    drawRoundRect(
        color = tetrisColor(index),
        topLeft = Offset(x + 2f, y + 2f),
        size = Size(cell - 4f, cell - 4f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(7f, 7f)
    )
}

private fun tetrisColor(index: Int) = when (index) {
    1 -> Color(0xFF4DD0E1)
    2 -> Color(0xFFFFD54F)
    3 -> Color(0xFFBA68C8)
    4 -> Color(0xFF81C784)
    5 -> Color(0xFFE57373)
    6 -> Color(0xFF64B5F6)
    else -> Color(0xFFFFB74D)
}
