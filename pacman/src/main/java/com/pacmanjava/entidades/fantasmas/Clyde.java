// Clyde.java
package com.pacmanjava.entidades.fantasmas;

import java.util.random.RandomGenerator;
import javafx.scene.paint.Color;

import com.pacmanjava.entidades.Ghost;

import static com.pacmanjava.utils.Utils.*;

public final class Clyde extends Ghost {

    private static final Point SCATTER_CORNER = new Point(2, 35);
    private static final int SHYNESS_RADIUS_TILES = 8;
    private static final long SHYNESS_RADIUS_SQUARED = (long) SHYNESS_RADIUS_TILES * SHYNESS_RADIUS_TILES;

    public Clyde(GridMap map, Point startTile, Direction startDirection, RandomGenerator random) {
        super(map, startTile, startDirection, SCATTER_CORNER, CLASSIC_HOME_TILE, random, Color.ORANGE);
    }

    @Override
    protected Point chaseTarget(GameContext ctx) {
        // dist² >= 64 equivale a dist >= 8 tiles, sem sqrt
        boolean farEnough = position().distanceSquared(ctx.pacmanTile()) >= SHYNESS_RADIUS_SQUARED;
        return farEnough ? ctx.pacmanTile() : scatterCorner();
    }
}