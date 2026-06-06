# iGaming

[中文](#中文) | [English](#english)

## 中文

iGaming 是一个完全本地运行的 Android 小游戏盒子，使用 Kotlin、Jetpack Compose 和 Material 3 构建。项目首版包含贪吃蛇、2048、俄罗斯方块和合成大西瓜，适合学习 Compose 游戏 UI、Canvas 绘制、本地状态保存和 Android 真机调试流程。

### 功能特性

- Material 3 风格首页与游戏界面
- 全面屏 edge-to-edge 适配，状态栏和手势导航栏透明绘制
- 四款本地小游戏：贪吃蛇、2048、俄罗斯方块、合成大西瓜
- DataStore 本地保存最高分
- 无后端、无 WebView、无远程游戏资源
- 纯 Kotlin 游戏规则引擎，附带单元测试

### 技术栈

- Kotlin
- Jetpack Compose
- Material 3
- Android DataStore
- Gradle / Android Gradle Plugin

### 构建与运行

```bash
./gradlew test assembleDebug lint
```

连接 Android 真机后安装调试包：

```bash
adb devices -l
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.igaming.localarcade/.MainActivity
```

### 项目结构

- `app/src/main/java/com/igaming/localarcade/ui`：首页、导航和主题入口
- `app/src/main/java/com/igaming/localarcade/games`：各小游戏的规则引擎和 Compose 界面
- `app/src/main/java/com/igaming/localarcade/data`：游戏目录和本地分数仓库
- `app/src/test/java`：小游戏规则单元测试

### 许可证

本项目使用 GNU General Public License v2.0 开源。详见 [LICENSE](LICENSE)。

## English

iGaming is a fully local Android mini-game arcade built with Kotlin, Jetpack Compose, and Material 3. The first version includes Snake, 2048, Tetris, and a lightweight Suika-style merge game. It is designed as a practical example for Compose game UI, Canvas rendering, local score persistence, and physical-device Android debugging.

### Features

- Material 3 home screen and game screens
- Edge-to-edge layout with transparent status and gesture navigation bars
- Four local mini games: Snake, 2048, Tetris, and Watermelon Merge
- Local high-score persistence with DataStore
- No backend, no WebView, and no remote game assets
- Pure Kotlin game engines with unit tests

### Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Android DataStore
- Gradle / Android Gradle Plugin

### Build And Run

```bash
./gradlew test assembleDebug lint
```

Install and launch on a connected Android device:

```bash
adb devices -l
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.igaming.localarcade/.MainActivity
```

### Project Structure

- `app/src/main/java/com/igaming/localarcade/ui`: home screen, navigation, and theme entry points
- `app/src/main/java/com/igaming/localarcade/games`: game engines and Compose screens
- `app/src/main/java/com/igaming/localarcade/data`: game catalog and local score repository
- `app/src/test/java`: unit tests for game rules

### License

This project is open source under the GNU General Public License v2.0. See [LICENSE](LICENSE).
