package com.igaming.localarcade.games.twenty

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.igaming.localarcade.data.ScoreRepository
import com.igaming.localarcade.data.gameCatalog
import com.igaming.localarcade.games.common.GameScaffold
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max

@Composable
fun TwentyScreen(onBack: () -> Unit) {
    val definition = gameCatalog.first { it.id == "twenty" }
    val context = LocalContext.current
    val repository = remember { ScoreRepository(context.applicationContext) }
    val bestScore by repository.bestScore(definition.bestScoreKey).collectAsState(initial = 0)
    val scope = rememberCoroutineScope()
    var game by remember { mutableStateOf(newTwentyGame()) }

    LaunchedEffect(game.score, game.isGameOver) {
        if (game.isGameOver || game.hasWon) repository.submitScore(definition.bestScoreKey, game.score)
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
            game = newTwentyGame()
        },
        onPauseToggle = { game = if (game.isPaused) game.resume() else game.pause() }
    ) { modifier ->
        TwentyBoard(
            game = game,
            modifier = modifier,
            onMove = { game = game.move(it) }
        )
    }
}

@Composable
private fun TwentyBoard(game: TwentyGame, modifier: Modifier, onMove: (TwentyMove) -> Unit) {
    val boardColor = MaterialTheme.colorScheme.surfaceVariant
    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val move = if (abs(dragAmount.x) > abs(dragAmount.y)) {
                        if (dragAmount.x > 0) TwentyMove.Right else TwentyMove.Left
                    } else {
                        if (dragAmount.y > 0) TwentyMove.Down else TwentyMove.Up
                    }
                    onMove(move)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(boardColor)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (row in 0 until 4) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (col in 0 until 4) {
                        TwentyTile(
                            value = game.board[row * 4 + col],
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TwentyTile(value: Int, modifier: Modifier) {
    val color = when (value) {
        0 -> MaterialTheme.colorScheme.surface.copy(alpha = 0.52f)
        2 -> Color(0xFFEAE4DA)
        4 -> Color(0xFFE3D8C3)
        8 -> Color(0xFFF2B179)
        16 -> Color(0xFFF59563)
        32 -> Color(0xFFF67C5F)
        64 -> Color(0xFFF65E3B)
        128 -> Color(0xFFEDCF72)
        256 -> Color(0xFFEDCC61)
        512 -> Color(0xFFEDC850)
        1024 -> Color(0xFFEDC53F)
        else -> Color(0xFF3D6374)
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(8.dp))
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        if (value > 0) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = if (value <= 4) Color(0xFF5E5347) else Color.White
            )
        }
    }
}
