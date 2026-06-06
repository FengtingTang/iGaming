package com.igaming.localarcade.games.watermelon

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.VerticalAlignBottom
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
fun WatermelonScreen(onBack: () -> Unit) {
    val definition = gameCatalog.first { it.id == "watermelon" }
    val context = LocalContext.current
    val repository = remember { ScoreRepository(context.applicationContext) }
    val bestScore by repository.bestScore(definition.bestScoreKey).collectAsState(initial = 0)
    val scope = rememberCoroutineScope()
    var game by remember { mutableStateOf(newWatermelonGame()) }

    LaunchedEffect(game.isPaused, game.isGameOver) {
        while (!game.isPaused && !game.isGameOver) {
            delay(24L)
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
            game = newWatermelonGame()
        },
        onPauseToggle = { game = if (game.isPaused) game.resume() else game.pause() }
    ) { modifier ->
        androidx.compose.foundation.layout.Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            WatermelonBoard(
                game = game,
                modifier = Modifier.weight(1f),
                onAimAt = { game = game.aimAt(it) },
                onDrop = { game = game.drop() }
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FilledTonalIconButton(onClick = { game = game.aim(-7f) }) {
                    Icon(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, contentDescription = "左移")
                }
                FilledTonalIconButton(onClick = { game = game.drop() }) {
                    Icon(Icons.Rounded.VerticalAlignBottom, contentDescription = "投放")
                }
                FilledTonalIconButton(onClick = { game = game.aim(7f) }) {
                    Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = "右移")
                }
            }
        }
    }
}

@Composable
private fun WatermelonBoard(
    game: WatermelonGame,
    modifier: Modifier,
    onAimAt: (Float) -> Unit,
    onDrop: () -> Unit
) {
    val boardColor = MaterialTheme.colorScheme.surfaceVariant
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(boardColor, RoundedCornerShape(8.dp))
            .padding(8.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onAimAt(offset.x / size.width * game.width)
                    onDrop()
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val scale = min(size.width / game.width, size.height / game.height)
            val left = (size.width - scale * game.width) / 2f
            val top = (size.height - scale * game.height) / 2f
            drawLine(
                color = Color(0xFFE95F78).copy(alpha = 0.62f),
                start = Offset(left, top + game.failLine * scale),
                end = Offset(left + game.width * scale, top + game.failLine * scale),
                strokeWidth = 3f
            )
            drawLine(
                color = Color(0xFF006D43),
                start = Offset(left + game.aimX * scale, top + 2f),
                end = Offset(left + game.aimX * scale, top + 18f),
                strokeWidth = 5f
            )
            val preview = fruitSpecs[game.nextLevel]
            drawCircle(
                color = fruitColor(game.nextLevel).copy(alpha = 0.45f),
                radius = preview.radius * scale,
                center = Offset(left + game.aimX * scale, top + 8f * scale)
            )
            game.fruits.forEach { fruit ->
                drawCircle(
                    color = fruitColor(fruit.level),
                    radius = fruit.spec.radius * scale,
                    center = Offset(left + fruit.x * scale, top + fruit.y * scale)
                )
            }
        }
    }
}

private fun fruitColor(level: Int) = when (level) {
    0 -> Color(0xFFE95F78)
    1 -> Color(0xFFFF8A65)
    2 -> Color(0xFFFFD54F)
    3 -> Color(0xFFAED581)
    4 -> Color(0xFF4DB6AC)
    5 -> Color(0xFF64B5F6)
    6 -> Color(0xFF9575CD)
    else -> Color(0xFF43A047)
}
