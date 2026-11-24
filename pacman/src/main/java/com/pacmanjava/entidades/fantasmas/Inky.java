// Inky.java
package com.pacmanjava.entidades.fantasmas;

import java.util.random.RandomGenerator;
import javafx.scene.paint.Color;

import com.pacmanjava.entidades.Ghost;

import static com.pacmanjava.utils.Utils.*;

public final class Inky extends Ghost {

    private static final Point SCATTER_CORNER = new Point(29, 35);
    private static final int PIVOT_TILES = 2;

    private final Blinky blinky;

    public Inky(GridMap map, Blinky blinky, Point startTile, Direction startDirection, RandomGenerator random) {
        super(map, startTile, startDirection, SCATTER_CORNER, CLASSIC_HOME_TILE, random, Color.CYAN);
        this.blinky = blinky;
    }

    @Override
    protected Point chaseTarget(GameContext ctx) {
        Point pivot = tilesAheadOfPacman(ctx, PIVOT_TILES);
        Point blinkyToPivot = pivot.minus(blinky.position());
        return pivot.plus(blinkyToPivot); // vetor Blinky→pivô dobrado
    }
}