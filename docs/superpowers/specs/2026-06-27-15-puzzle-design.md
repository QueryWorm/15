# 15 Puzzle Game — Design Spec

## Overview
Single-file HTML/JS/CSS implementation of the classic 15 Puzzle (4×4 grid).

## Requirements
- Field 4×4 with tiles 1–15 and one empty cell
- Click on tile → move it if adjacent to empty cell (with smooth animation)
- Shuffle button — generates a solvable configuration
- Win detection — tiles 1–15 in order, empty cell bottom-right
- Move counter
- Mouse-only control

## Architecture
One self-contained `index.html`:
- Inline CSS with dark tiles, light digits, smooth transitions
- Inline JS: game state (array 16), render, move logic, shuffle (Fisher-Yates + parity check), win check

## Data Flow
- State: `tiles[0..15]` where `0` = empty cell
- Click → find clicked tile index → check adjacency with empty cell → swap → re-render
- Shuffle → generate random permutation → verify solvability → if not, swap last two non-zero tiles → render

## Visual
- Grid: CSS Grid, 4×4, gap, rounded corners
- Tiles: numbered with `data-index`, transform/transition for smooth movement
- Empty cell: invisible/hidden
- Win: overlay or alert

## Edge Cases
- Shuffle must guarantee solvability (parity rule)
- Click on empty cell → no-op
- Multiple rapid clicks → queue animation via CSS transitions
