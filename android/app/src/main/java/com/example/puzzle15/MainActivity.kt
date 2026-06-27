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
                TileItem(
                    value = value,
                    onClick = {
                        val newState = GameLogic.move(state, index)
                        if (newState != state) state = newState
                    }
                )
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
        targetValue = Color(0xFFE94560),
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
