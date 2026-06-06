package com.igaming.localarcade.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.igaming.localarcade.data.GameRoute
import com.igaming.localarcade.games.snake.SnakeScreen
import com.igaming.localarcade.games.tetris.TetrisScreen
import com.igaming.localarcade.games.twenty.TwentyScreen
import com.igaming.localarcade.games.watermelon.WatermelonScreen

@Composable
fun LocalArcadeApp() {
    var route by rememberSaveable { mutableStateOf(GameRoute.Home.route) }
    val goHome = { route = GameRoute.Home.route }

    when (route) {
        GameRoute.Snake.route -> SnakeScreen(onBack = goHome)
        GameRoute.Twenty.route -> TwentyScreen(onBack = goHome)
        GameRoute.Tetris.route -> TetrisScreen(onBack = goHome)
        GameRoute.Watermelon.route -> WatermelonScreen(onBack = goHome)
        else -> HomeScreen(onOpenGame = { route = it.route.route })
    }
}
