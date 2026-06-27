# Android 15 Puzzle — Design Spec

## Overview
Нативное Android-приложение «Пятнашки» (15 Puzzle) на Kotlin + Jetpack Compose. CI через GitHub Actions собирает APK.

## Requirements
- Поле 4×4 с фишками 1–15 и пустой клеткой
- Tap по фишке → сдвиг если смежна с пустой
- Кнопка Shuffle — генерирует решаемую конфигурацию
- Счётчик ходов
- Определение победы (1–15 по порядку, пустая внизу справа)
- APK для ручной установки (не в Google Play)

## Architecture
**Язык:** Kotlin
**UI:** Jetpack Compose + Material3
**Min SDK:** 26
**Target SDK:** 34

### Структура файлов

```
android/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/puzzle15/
│   │   │   ├── MainActivity.kt    — точка входа, setContent
│   │   │   ├── GameState.kt       — data class состояния
│   │   │   └── GameLogic.kt       — чистая логика (shuffle, isSolvable, etc.)
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
└── gradle/wrapper/gradle-wrapper.properties

.github/
└── workflows/
    └── build.yml                  — CI сборка APK
```

### Data Flow
- `GameState` — data class: `tiles: List<Int>`, `moves: Int`, `isWin: Boolean`
- `GameLogic` — object с чистыми функциями: `shuffle()`, `isAdjacent()`, `isSolvable()`, `checkWin()`
- `MainActivity` — хранит state через `remember { mutableStateOf(...) }`
- Tap → onTileClick → проверка adjacency → swap → render → checkWin

### Solvability
Та же формула, что в HTML-версии: Fisher-Yates shuffle + parity check (инверсии + ряд пустой клетки).

### CI (GitHub Actions)
- Trigger: push в `android/` или PR
- `ubuntu-latest`, `temurin 17`, Android SDK 34
- `./gradlew assembleDebug`
- Upload `app-debug.apk` как artifact

## Visual
- Material3, тёмная тема или светлая (системная)
- Плитки — Card с закруглениями, контрастный цвет
- Пустая плитка — transparent/invisible
- Кнопка Shuffle в TopAppBar
- Диалог победы

## Edge Cases
- Shuffle гарантирует решаемость (parity)
- Tap на пустую клетку → no-op
- Повторный shuffle сбрасывает счётчик
