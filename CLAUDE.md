# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

A 2D falling rhythm runner game built with the Processing library (`core.jar`). The player navigates 5 lanes, switching tracks with number keys, while obstacles fall from above. This is a NetBeans Ant project targeting Java 21.

## Build & run commands

```bash
ant compile    # Compile only
ant jar        # Package into dist/ICS4U1_final_project.jar
ant run        # Build and launch
ant clean      # Remove build artifacts
```

Compilation depends on `core.jar` at the project root in the classpath.

## Architecture

```
GameMain (PApplet)
├── Pages (UI layer)
│   ├── Page              ← base: background image + button list
│   ├── Button            ← clickable rectangle with hover detect
│   ├── TitlePage         ← "START" → START state
│   └── StartPage         ← "CONTINUE" → EXIT state
└── Battle (gameplay layer — WIP)
    ├── Battle            ← empty shell
    ├── Scene             ← empty shell
    ├── Character         ← HP + shield, wraps Property
    ├── Property          ← HP/SH with damage calc
    ├── Obstacle          ← falling rect (x, y, w, h, speed)
    └── Road              ← ArrayDeque<Obstacle>, 5 lanes
```

**Game state machine**: `GameMain.Gamestate` enum — TITLE → START → TUTORIAL → BATTLE → END → EXIT. `GameMain.draw()` switches on `currentState` and delegates to the active page's `run()`. The BATTLE state will delegate to the `Battle` class once implemented.

**Rendering**: `GameMain` extends `PApplet`. `fullScreen()` + `size(1920, 1080)` at 60 FPS. Every visual class receives a `PApplet` reference and draws through it.

**Input**: Mouse events are delegated through the state machine. Keyboard input (number keys for lane switching) is not yet wired.

**Key processing conventions**:
- `PApplet p` is passed to constructors, never stored statically
- Drawing happens in `draw()`/`display()` methods called each frame, not in constructors
- Game logic updates (movement, collision) happen in `update()`/`run()` before drawing
