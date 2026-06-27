# Android 15 Puzzle Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Android APK игры Пятнашки на Kotlin + Jetpack Compose с CI сборкой.

**Architecture:** Gradle-проект в `android/`. Одна Activity с Compose UI. Чистая логика без ViewModel. GitHub Actions собирает debug APK.

**Tech Stack:** Kotlin, Jetpack Compose, Material3, Gradle KTS, GitHub Actions

---

### Task 1: Gradle-проект и AndroidManifest

**Files:**
- Create: `android/settings.gradle.kts`
- Create: `android/build.gradle.kts`
- Create: `android/gradle.properties`
- Create: `android/gradle/wrapper/gradle-wrapper.properties`
- Create: `android/gradlew` / `android/gradlew.bat`
- Create: `android/app/build.gradle.kts`
- Create: `android/app/src/main/AndroidManifest.xml`

- [ ] **Step 1: Create root build.gradle.kts**

```kotlin
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}
```

- [ ] **Step 2: Create settings.gradle.kts**

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolution {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Puzzle15"
include(":app")
```

- [ ] **Step 3: Create gradle.properties**

```properties
org.gradle.jvmargs=-Xmx1024m
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
```

- [ ] **Step 4: Create gradle-wrapper.properties**

```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

- [ ] **Step 5: Create app/build.gradle.kts**

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.puzzle15"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.puzzle15"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.activity:activity-compose:1.8.2")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
```

- [ ] **Step 6: Create AndroidManifest.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application
        android:allowBackup="true"
        android:label="Пятнашки"
        android:supportsRtl="true"
        android:theme="@android:style/Theme.Material.Light.NoActionBar">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

- [ ] **Step 7: Create gradlew wrapper files**

Run: `mkdir -p android/gradle/wrapper`

Download gradle wrapper scripts. Use `gradle wrapper` if gradle is available, otherwise download manually.

- [ ] **Step 8: Setup gradlew**

Run: `chmod +x android/gradlew`

---

### Task 2: Код игры (Kotlin + Compose)

**Files:**
- Create: `android/app/src/main/java/com/example/puzzle15/GameState.kt`
- Create: `android/app/src/main/java/com/example/puzzle15/GameLogic.kt`
- Create: `android/app/src/main/java/com/example/puzzle15/MainActivity.kt`

- [ ] **Step 1: Create GameState.kt**

```kotlin
package com.example.puzzle15

data class GameState(
    val tiles: List<Int> = (0..15).toList(),
    val moves: Int = 0,
    val isWin: Boolean = false
)
```

- [ ] **Step 2: Create GameLogic.kt**

```kotlin
package com.example.puzzle15

import kotlin.random.Random

object GameLogic {
    private const val SIZE = 4
    private const val TOTAL = SIZE * SIZE

    fun shuffled(): List<Int> {
        val tiles = (0 until TOTAL).toMutableList()
        do {
            tiles.shuffle(Random)
        } while (!isSolvable(tiles))
        return tiles
    }

    fun isAdjacent(a: Int, b: Int): Boolean {
        val ax = a % SIZE; val ay = a / SIZE
        val bx = b % SIZE; val by = b / SIZE
        return kotlin.math.abs(ax - bx) + kotlin.math.abs(ay - by) == 1
    }

    fun move(state: GameState, idx: Int): GameState {
        if (state.isWin) return state
        val emptyIdx = state.tiles.indexOf(0)
        if (!isAdjacent(idx, emptyIdx)) return state
        val newTiles = state.tiles.toMutableList()
        newTiles[idx] = 0
        newTiles[emptyIdx] = state.tiles[idx]
        val newMoves = state.moves + 1
        val won = newTiles.withIndex().all { (i, v) -> v == (i + 1) % TOTAL }
        return GameState(newTiles, newMoves, won)
    }

    private fun isSolvable(tiles: List<Int>): Boolean {
        val inv = countInversions(tiles)
        val emptyRowFromBottom = SIZE - (tiles.indexOf(0) / SIZE)
        return if (SIZE % 2 == 1) inv % 2 == 0
        else (inv + emptyRowFromBottom) % 2 == 0
    }

    private fun countInversions(tiles: List<Int>): Int {
        var inv = 0
        val filtered = tiles.filter { it != 0 }
        for (i in filtered.indices)
            for (j in i + 1 until filtered.size)
                if (filtered[i] > filtered[j]) inv++
        return inv
    }
}
```

- [ ] **Step 3: Create MainActivity.kt**

```kotlin
package com.example.puzzle15

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                GameScreen()
            }
        }
    }
}

@Composable
fun GameScreen() {
    var state by remember { mutableStateOf(GameState(tiles = GameLogic.shuffled())) }
    var showWinDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.isWin) {
        if (state.isWin) showWinDialog = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Пятнашки") },
                actions = {
                    Text("Ходов: ${state.moves}", modifier = Modifier.padding(end = 12.dp))
                    TextButton(onClick = {
                        state = GameState(tiles = GameLogic.shuffled())
                        showWinDialog = false
                    }) {
                        Text("Перемешать", color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            itemsIndexed(state.tiles) { index, value ->
                TileItem(value = value, onClick = {
                    val newState = GameLogic.move(state, index)
                    if (newState != state) state = newState
                })
            }
        }
    }

    if (showWinDialog) {
        AlertDialog(
            onDismissRequest = { showWinDialog = false },
            title = { Text("Вы выиграли!") },
            text = { Text("Потрачено ходов: ${state.moves}") },
            confirmButton = {
                TextButton(onClick = {
                    state = GameState(tiles = GameLogic.shuffled())
                    showWinDialog = false
                }) {
                    Text("Новая игра")
                }
            }
        )
    }
}

@Composable
fun TileItem(value: Int, onClick: () -> Unit) {
    if (value == 0) {
        Box(modifier = Modifier.aspectRatio(1f))
        return
    }
    val containerColor by animateColorAsState(
        targetValue = if (value == 0) Color.Transparent else Color(0xFFE94560),
        label = "tileColor"
    )
    Card(
        modifier = Modifier
            .aspectRatio(1f),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = value.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
```

- [ ] **Step 4: Verify files exist**

Run: `find android -type f | sort`

---

### Task 3: GitHub Actions CI

**Files:**
- Create: `.github/workflows/build.yml`

- [ ] **Step 1: Create build.yml**

```yaml
name: Build APK

on:
  push:
    paths:
      - 'android/**'
    branches:
      - '**'
  pull_request:
    paths:
      - 'android/**'

jobs:
  build:
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: ./android

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Setup Android SDK
        uses: android-actions/setup-android@v3

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew

      - name: Build APK
        run: ./gradlew assembleDebug

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: puzzle15-debug
          path: android/app/build/outputs/apk/debug/app-debug.apk
```

- [ ] **Step 2: Verify workflow**

Check `.github/workflows/build.yml` exists and syntax is valid.

---

### Task 4: Commit and push

- [ ] **Step 1: Check git status**

Run: `git status`

- [ ] **Step 2: Commit**

```bash
git add -A && git commit -m "feat: android 15 puzzle (Kotlin + Compose + CI)"
```

- [ ] **Step 3: Push**

```bash
git push
```
