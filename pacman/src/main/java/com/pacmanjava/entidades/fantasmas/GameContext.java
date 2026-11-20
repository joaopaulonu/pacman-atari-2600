// GameContext.java
package com.pacmanjava.entidades.fantasmas;

import com.pacmanjava.consumables.Dot;

import static com.pacmanjava.utils.Utils.*;

/** Snapshot imutável do mundo consumido pelos fantasmas em um tick. */
public record GameContext(Point pacmanTile, Direction pacmanDirection, int dotsRemaining, LevelSettings level) {

    public GameContext {
    }
}