// Pinky.java
package com.pacmanjava.entidades.fantasmas;

import java.util.random.RandomGenerator;
import javafx.scene.paint.Color;

import com.pacmanjava.entidades.Ghost;

import static com.pacmanjava.utils.Utils.*;

public final class Pinky extends Ghost {

    private static final Point SCATTER_CORNER = new Point(4, 1);
    private static final int LOOKAHEAD_TILES = 4;

    public Pinky(GridMap map, Point startTile, Direction startDirection, RandomGenerator random) {
        super(map, startTile, startDirection, SCATTER_CORNER, CLASSIC_HOME_TILE, random, Color.PINK);
    }

    @Override
    protected Point chaseTarget(GameContext ctx) {
        return tilesAheadOfPacman(ctx, LOOKAHEAD_TILES);
    }
}