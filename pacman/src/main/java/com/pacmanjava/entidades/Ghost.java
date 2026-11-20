// Ghost.java
package com.pacmanjava.entidades;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

import com.pacmanjava.entidades.fantasmas.Point;
import com.pacmanjava.utils.Utils.GhostSprite;

import javafx.scene.paint.Color;

import com.pacmanjava.entidades.fantasmas.GameContext;
import com.pacmanjava.entidades.fantasmas.GridMap;
import com.pacmanjava.entidades.fantasmas.LevelSettings;

import static com.pacmanjava.utils.Utils.*;
import static com.almasb.fxgl.dsl.FXGL.*;

public abstract class Ghost extends EntidadeJogo {
    public static enum GhostState {
        CHASE,
        SCATTER,
        FRIGHTENED,
        EATEN;
    
        /** Estados em que o fantasma segue alvos fixos (sujeito às zonas vermelhas). */
        public boolean isHunting() {
            return this == CHASE || this == SCATTER;
        }
    }
    
    /** Tile imediatamente acima da porta da casa dos fantasmas. */
    public static final Point CLASSIC_HOME_TILE = new Point(13, 11);

    private static final double FULL_SPEED_TILES_PER_SECOND = 75.75 / 8.0;
    private static final double EATEN_SPEED = 2.0;
    private static final Direction[] POSSIBLE_DIRECTIONS = Direction.values();

    private final GridMap map;
    private final Point scatterCorner;
    private final Point homeTile;
    private final RandomGenerator random;

    private Point position;
    private Direction direction; //possibly currentDirection
    private Direction nextDirection; //possibly bufferDirection
    private GhostState state = GhostState.SCATTER;
    private GhostState globalMode = GhostState.SCATTER;
    private boolean reversePending; //idk
    private double progress; //idk

    protected Color color;

    protected Ghost(GridMap map, Point startPos, Direction startDir, Point scatterCorner, Point homeTile, RandomGenerator random, Color color) {
        this.map = map;
        this.position = startPos;
        this.direction = startDir;
        this.scatterCorner = scatterCorner;
        this.homeTile = homeTile;
        this.random = random;
        this.color = color;

        this.entity = entityBuilder()
            .at(startPos.x(), startPos.y())
            .with(new GhostSprite(color))
            .with(new FixedUpdateComponent(this))
            .buildAndAttach();
    }

    public void setDead(boolean dead) {
        setDead(dead);
        //onGhostEaten();
    }

    protected abstract Point chaseTarget(GameContext ctx); //target in chase mode

    //target in scatter mode
    protected Point scatterTarget(GameContext ctx) {
        return scatterCorner;
    }
    
    protected final Point scatterCorner() {
        return scatterCorner;
    }

    // Alvo "à frente" do Pac-Man reproduzindo o bug de overflow do Z80.
    protected static Point tilesAheadOfPacman(GameContext ctx, int tiles) {
        return ctx.pacmanTile().ahead(ctx.pacmanDirection(), tiles);
    }

    //retorna a velocidade do fantasma
    protected double speedFactor(GameContext ctx) {
        LevelSettings level = ctx.level();
        
        if (state == GhostState.EATEN) {
            return EATEN_SPEED;
        }
        if (isInTunnel()) {
            return level.tunnelSpeed();
        }

        return state == GhostState.FRIGHTENED ? level.frightenedSpeed() : level.ghostSpeed();
    }

    protected final boolean isInTunnel() {
        return map.isTunnel(position.x(), position.y());
    }

    //atualiza a lógica dos fantasmas
    public final void update(double deltaSeconds, GameContext ctx) {
        if (nextDirection == null) {
            nextDirection = decideAt(map.neighbor(position, direction), direction, ctx);
        }
        progress += deltaSeconds * FULL_SPEED_TILES_PER_SECOND * speedFactor(ctx);
        while (progress >= 1.0) {
            progress -= 1.0;
            enterNextTile(ctx);
        }
    }

    //possible (onTile)
    private void enterNextTile(GameContext ctx) {
        Direction heading = direction;
        position = map.neighbor(position, heading);

        if (state == GhostState.EATEN && position.equals(homeTile)) {
            state = globalMode;
            direction = decideAt(position, heading, ctx);
        } else {
            // Única exceção à regra de 180°: reversão forçada por troca de modo
            direction = reversePending ? heading.opposite() : nextDirection;
        }
        reversePending = false;

        // Lookahead de 1 tile: a decisão da próxima interseção é tomada ao entrar neste tile
        nextDirection = decideAt(map.neighbor(position, direction), direction, ctx);
    }

    //decide where to go
    private Direction decideAt(Point tile, Direction arrival, GameContext ctx) {
        List<Direction> legal = legalDirections(tile, arrival);
        if (legal.isEmpty()) {
            return arrival.opposite();
        }
        if (state == GhostState.FRIGHTENED) {
            return randomDirection(legal);
        }
        return DirectionClosestToTarget(legal, tile, resolveTarget(ctx));
    }

    //retorna as direções que o fantasma pode ir
    private List<Direction> legalDirections(Point tile, Direction arrival) {
        boolean upRestricted = state.isHunting() && map.forbidsUpwardTurn(tile.x(), tile.y());
        boolean mayUseDoor = state == GhostState.EATEN;
        List<Direction> legal = new ArrayList<>(4);


        for (Direction candidate : POSSIBLE_DIRECTIONS) {
            if (candidate == arrival.opposite()) { continue; }
            if (candidate == Direction.UP && upRestricted) { continue; }

            if (map.isValidMove(tile, candidate, mayUseDoor)) {
                legal.add(candidate);
            }
        }
        return legal;
    }

    private Direction DirectionClosestToTarget(List<Direction> legal, Point tile, Point target) {
        Direction best = legal.get(0);
        int bestDistance = Integer.MAX_VALUE;
        
        for (Direction candidate : legal) {
            int distance = map.neighbor(tile, candidate).distanceSquared(target);

            if (distance < bestDistance) {
                bestDistance = distance;
                best = candidate;
            }
        }

        return best;
    }

    private Direction randomDirection(List<Direction> legal) {
        int start = random.nextInt(POSSIBLE_DIRECTIONS.length);

        for (int i = 0; i < POSSIBLE_DIRECTIONS.length; i++) {
            Direction candidate = POSSIBLE_DIRECTIONS[(start + i) % POSSIBLE_DIRECTIONS.length]; // rotação anti-horária até ser válida
            if (legal.contains(candidate)) {
                return candidate;
            }
        }
        
        return legal.get(0);
    }

    private Point resolveTarget(GameContext ctx) {
        return switch (state) {
            case CHASE -> chaseTarget(ctx);
            case SCATTER -> scatterTarget(ctx);
            case EATEN -> homeTile;
            case FRIGHTENED -> throw new IllegalStateException("FRIGHTENED não usa alvo");
        };
    }

    public final void applyGlobalMode(GhostState mode) {
        if (!mode.isHunting()) {
            throw new IllegalArgumentException("modo global deve ser CHASE ou SCATTER");
        }
        globalMode = mode;
        if (state.isHunting() && state != mode) {
            state = mode;
            reversePending = true;
        }
    }

    public final void frighten() {
        if (state.isHunting() || state == GhostState.FRIGHTENED) {
            state = GhostState.FRIGHTENED;
            reversePending = true;
        }
    }

    //Fases com tempo de susto zero: fantasmas apenas invertem a direção.
    public final void reverseOnly() {
        if (state.isHunting()) {
            reversePending = true;
        }
    }

    //Fim do susto: retorna ao modo global.
    public final void endFright() {
        if (state == GhostState.FRIGHTENED) {
            state = globalMode;
        }
    }

    public final void eat() {
        if (state == GhostState.FRIGHTENED) {
            state = GhostState.EATEN;
        }
    }

    public final Point position() {
        return position;
    }

    public final Direction direction() {
        return direction;
    }

    public final GhostState state() {
        return state;
    }

    public void handleMovement(double tpf, int epf) {
        handleBoundaries();
    }
}