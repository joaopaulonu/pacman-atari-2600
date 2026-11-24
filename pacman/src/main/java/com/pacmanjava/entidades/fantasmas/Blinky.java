package com.pacmanjava.entidades.fantasmas;

import java.util.random.RandomGenerator;
import javafx.scene.paint.Color;

import com.pacmanjava.entidades.Ghost;
import com.pacmanjava.utils.Utils.Direction;

import static com.pacmanjava.utils.Utils.*;

public final class Blinky extends Ghost {

    private static final Point SCATTER_CORNER = new Point(27, 1);

    private boolean elroySuspended;

    public Blinky(GridMap map, Point startTile, Direction startDirection, RandomGenerator random) {
        super(map, startTile, startDirection, SCATTER_CORNER, CLASSIC_HOME_TILE, random, Color.RED);
    }

    @Override
    protected Point chaseTarget(GameContext ctx) {
        return ctx.pacmanTile();
    }

    @Override
    protected Point scatterTarget(GameContext ctx) {
        if (elroyPhase(ctx) > 0) { setState(GhostState.CHASE); }

        // Elroy ignora o canto de dispersão e continua perseguindo
        return elroyPhase(ctx) > 0 ? ctx.pacmanTile() : scatterCorner();
    }

    @Override
    protected double speedFactor(GameContext ctx) {
        int phase = elroyPhase(ctx);

        if (phase > 0 && state().isHunting() && !isInTunnel()) {
            return phase == 2 ? ctx.level().elroy2Speed() : ctx.level().elroy1Speed();
        }
        return super.speedFactor(ctx);
    }

    //0 = normal, 1 = Elroy 1, 2 = Elroy 2 (limiares por fase em LevelSettings).
    public int elroyPhase(GameContext ctx) {
        if (elroySuspended) {
            return 0;
        }
        int remaining = ctx.dotsRemaining();
        if (remaining <= ctx.level().elroy2Dots()) {
            return 2;
        }
        return remaining <= ctx.level().elroy1Dots() ? 1 : 0;
    }

    public void setElroySuspended(boolean suspended) {
        this.elroySuspended = suspended;
    }
}