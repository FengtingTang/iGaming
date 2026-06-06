package com.igaming.localarcade.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.Filter9Plus
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class GameRoute(val route: String) {
    Home("home"),
    Snake("snake"),
    Twenty("twenty"),
    Tetris("tetris"),
    Watermelon("watermelon")
}

data class GameDefinition(
    val id: String,
    val title: String,
    val category: String,
    val icon: ImageVector,
    val accent: Color,
    val route: GameRoute,
    val description: String,
    val bestScoreKey: String
)

val gameCatalog = listOf(
    GameDefinition(
        id = "snake",
        title = "贪吃蛇",
        category = "反应",
        icon = Icons.Rounded.SportsEsports,
        accent = Color(0xFF21A67A),
        route = GameRoute.Snake,
        description = "控制蛇身吃掉能量块，越长越刺激。",
        bestScoreKey = "best_snake"
    ),
    GameDefinition(
        id = "twenty",
        title = "2048",
        category = "数字",
        icon = Icons.Rounded.Filter9Plus,
        accent = Color(0xFFE2A33A),
        route = GameRoute.Twenty,
        description = "滑动合并数字，冲到 2048。",
        bestScoreKey = "best_twenty"
    ),
    GameDefinition(
        id = "tetris",
        title = "俄罗斯方块",
        category = "消除",
        icon = Icons.Rounded.GridView,
        accent = Color(0xFF5777E6),
        route = GameRoute.Tetris,
        description = "旋转、下落、消行，节奏会越来越快。",
        bestScoreKey = "best_tetris"
    ),
    GameDefinition(
        id = "watermelon",
        title = "合成大西瓜",
        category = "合成",
        icon = Icons.Rounded.Casino,
        accent = Color(0xFFE95F78),
        route = GameRoute.Watermelon,
        description = "投放水果，相同水果碰撞后合成更大水果。",
        bestScoreKey = "best_watermelon"
    )
)
