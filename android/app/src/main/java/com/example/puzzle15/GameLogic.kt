package com.example.puzzle15

import kotlin.random.Random
import kotlin.math.abs

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
        return abs(ax - bx) + abs(ay - by) == 1
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
