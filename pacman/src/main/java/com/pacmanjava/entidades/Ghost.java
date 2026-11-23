// Ghost.java
package com.pacmanjava.entidades;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

import com.pacmanjava.entidades.fantasmas.Point;
import com.pacmanjava.utils.Utils.Direction;
import com.pacmanjava.utils.Utils.GhostSprite;
import com.pacmanjava.utils.Utils.PacmanSprite;

import javafx.scene.paint.Color;
import javafx.util.Duration;

import com.pacmanjava.entidades.fantasmas.GameContext;
import com.pacmanjava.entidades.fantasmas.GridMap;
import com.pacmanjava.entidades.fantasmas.LevelSettings;
import com.pacmanjava.PacManApp;
import com.pacmanjava.map.Lvl;
import com.pacmanjava.consumables.Dot;

import static com.pacmanjava.utils.Utils.*;
import static com.almasb.fxgl.dsl.FXGL.*;

public abstract class Ghost extends EntidadeJogo {
    public static enum GhostState {
        CHASE,
        SCATTER,
        FRIGHTENED,
        EATEN,
        HOUSE_BOUNCE,
        HOUSE_TO_DOOR,
        HOUSE_ENTER,
        HOUSE_EXIT;
    
        /** Estados em que o fantasma segue alvos fixos (sujeito às zonas vermelhas). */
        public boolean isHunting() {
            return this == CHASE || this == SCATTER;
        }
    }
    
    /** Tile da casa usado pelos fantasmas ao serem comidos. */
    public static final Point CLASSIC_HOME_TILE = new Point(13, 17);
    /** Tile central da porta da casa dos fantasmas. */
    public static final Point CLASSIC_DOOR_TILE = new Point(14, 16);
    /** Tile imediatamente acima da casa, após cruzar a porta. */
    public static final Point CLASSIC_EXIT_TILE = new Point(13, 12);

    private static final double FULL_SPEED_TILES_PER_SECOND = 75.75 / 8.0;
    private static final double EATEN_SPEED = 2.0;
    private static final Direction[] POSSIBLE_DIRECTIONS = {
        Direction.RIGHT,
        Direction.LEFT,
        Direction.UP,
        Direction.DOWN
    };

    private final GridMap map;
    private final Point scatterCorner;
    private final Point homeTile;
    private final RandomGenerator random;

    private Point position;
    private Point lastDecisionTile;
    private GhostState state = GhostState.SCATTER;
    private GhostState globalMode = GhostState.SCATTER;
    private boolean reversePending;
    private Point houseBounceTile;
    private int houseBounceMinY = 17;
    private int houseBounceMaxY = 19;
    private double houseExitX = 108.0;
    private java.util.List<Point> houseRoute;
    private int houseRouteIndex;

    protected Color color;

    protected Ghost(GridMap map, Point startPos, Direction startDir, Point scatterCorner, Point homeTile, RandomGenerator random, Color color) {
        this.map = map;
        this.position = startPos;
        this.lastDecisionTile = startPos;
        this.posX = (startPos.x() * 8) + 4;
        this.posY = (startPos.y() * 8) + 4;
        this.currentDirection = startDir;
        this.bufferDirection = startDir;
        this.scatterCorner = scatterCorner;
        this.homeTile = homeTile;
        this.random = random;
        this.color = color;

        this.entity = entityBuilder()
            .at((startPos.x() * 8) + 4, (startPos.y() * 8) + 4) 
            .with(new GhostSprite(color))
            .with(new FixedUpdateComponent(this))
            .zIndex(99)
            .buildAndAttach();
    }

    public void setDead() {
        super.setDead();
        this.entity.removeFromWorld();
    }

    public void pause() {
        if (entity.getComponent(GhostSprite.class) != null) {
            entity.getComponent(GhostSprite.class).setPaused();
        }
        super.pause();
    }

    public void resume() {
        if (entity.getComponent(GhostSprite.class) != null) {
            entity.getComponent(GhostSprite.class).setResumed();
        }
        super.resume();
    }

    protected abstract Point chaseTarget(GameContext ctx); //target in chase mode

    //target in scatter mode
    protected Point scatterTarget(GameContext ctx) {
        return scatterCorner;
    }
    
    protected final Point scatterCorner() {
        return scatterCorner;
    }

    protected static Point tilesAheadOfPacman(GameContext ctx, int tiles) {
        return ctx.pacmanTile().ahead(ctx.pacmanDirection(), tiles);
    }

    //retorna a velocidade do fantasma
    protected double speedFactor(GameContext ctx) {
        LevelSettings level = ctx.level();
        
        if (state == GhostState.EATEN || state == GhostState.HOUSE_ENTER) {
            return EATEN_SPEED;
        }
        if (state == GhostState.HOUSE_BOUNCE
            || state == GhostState.HOUSE_TO_DOOR
            || state == GhostState.HOUSE_EXIT) {
            return level.tunnelSpeed();
        }
        if (isInTunnel()) {
            return level.tunnelSpeed();
        }

        return state == GhostState.FRIGHTENED ? level.frightenedSpeed() : level.ghostSpeed();
    }

    protected final boolean isInTunnel() {
        return map.isTunnel(position.x(), position.y());
    }

    // Decide onde ir somente no centro de um tile.
    private Direction decideAt(Point tile, Direction arrival, GameContext ctx) {
        List<Direction> legal = legalDirections(tile, arrival);
        if (legal.isEmpty()) {
            return arrival == Direction.NONE ? Direction.NONE : arrival.opposite();
        }
        if (state == GhostState.FRIGHTENED) {
            return randomDirection(legal);
        }
        return DirectionClosestToTarget(legal, tile, resolveTarget(ctx));
    }

    //retorna as direções que o fantasma pode ir
    private List<Direction> legalDirections(Point tile, Direction arrival) {
        boolean upRestricted = state.isHunting() && map.forbidsUpwardTurn(tile.x(), tile.y());
        List<Direction> legal = new ArrayList<>(4);


        for (Direction candidate : POSSIBLE_DIRECTIONS) {
            if (candidate == arrival.opposite()) { continue; }
            if (candidate == Direction.UP && upRestricted) { continue; }

            if (map.isValidMove(tile, candidate, canPassGhostDoor())) {
                legal.add(candidate);
            }
        }
        return legal;
    }

    private boolean canPassGhostDoor() {
        return state == GhostState.EATEN
            || state == GhostState.HOUSE_ENTER
            || state == GhostState.HOUSE_TO_DOOR
            || state == GhostState.HOUSE_EXIT;
    }

    private Direction DirectionClosestToTarget(List<Direction> legal, Point tile, Point target) {
        Direction best = legal.get(0);
        int bestDistance = Integer.MAX_VALUE;
        
        for (Direction candidate : legal) {
            int distance = map.neighbor(tile, candidate).distanceSquared(target);

            if (distance < bestDistance
                || (distance == bestDistance && candidate == direction())) {
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
            case HOUSE_ENTER -> CLASSIC_DOOR_TILE;
            case HOUSE_TO_DOOR -> CLASSIC_DOOR_TILE;
            case FRIGHTENED -> throw new IllegalStateException("FRIGHTENED não usa alvo");
            case HOUSE_BOUNCE, HOUSE_EXIT -> throw new IllegalStateException("estado da casa não usa target normal");
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
            state = GhostState.HOUSE_ENTER;
            reversePending = true;
            houseRoute = null;
        }
    }

    public final void beginHouseBounce(Point homePosition) {
        this.houseBounceTile = homePosition;
        this.houseBounceMinY = 17;
        this.houseBounceMaxY = 19;
        this.state = GhostState.HOUSE_BOUNCE;
        this.houseRoute = null;
        this.currentDirection = homePosition.y() >= houseBounceMaxY
            ? Direction.UP
            : Direction.DOWN;
        this.bufferDirection = this.currentDirection;
    }

    public final void requestHouseExit() {
        if (state == GhostState.HOUSE_BOUNCE) {
            double spawnX = entity.getPosition().getX();
            houseExitX = spawnX < 120.0 ? 108.0 : 132.0;
            state = GhostState.HOUSE_TO_DOOR;
            houseRoute = null;
            reversePending = false;
        }
    }

    public final void spawnAt(double spawnX, double spawnY) {
        posX = (int) spawnX;
        posY = (int) spawnY;
        entity.setX(spawnX);
        entity.setY(spawnY);
    }

    public final Point position() {
        return position;
    }

    public final Direction direction() {
        return currentDirection;
    }

    public final GhostState state() {
        return state;
    }

    public void handleMovement(double tpf, int epf) {
        if (dead) { return; }

        if (!PacManApp.getPacMan().hasMovedAfterSpawn()
            || PacManApp.getPacMan().isPaused()
            || PacManApp.getPacMan().isDead()
            || PacManApp.getPacMan().isDying()
            || Lvl.getWin()) {
            pause();
            return;
        }

        if (paused) {
            resume();
            return;
        }

        posX = (int) entity.getPosition().getX();
        posY = (int) entity.getPosition().getY();

        GameContext ctx = new GameContext(
            PacManApp.getPacMan().getPosition(),
            PacManApp.getPacMan().getCurrentDirection(),
            Dot.TOTAL - Dot.getConsumed(),
            LevelSettings.forLevel(Lvl.getStage())
        );

        boolean scriptedHouseMovement = handleHousePixels();

        if (!scriptedHouseMovement && onTile()) {
            position = new Point((posX - 4) / 8, (posY - 4) / 8);
            boolean enteredNewTile = !position.equals(lastDecisionTile);

            if (enteredNewTile) {
                lastDecisionTile = position;

                if (!handleHouseMovement(position, ctx)) {
                    if (reversePending) {
                        bufferDirection = currentDirection == Direction.NONE
                            ? Direction.NONE
                            : currentDirection.opposite();
                        reversePending = false;
                    } else {
                        bufferDirection = decideAt(position, currentDirection, ctx);
                    }

                    Direction previousDirection = currentDirection;
                    if (bufferDirection != Direction.NONE
                        && map.isValidMove(position, bufferDirection, canPassGhostDoor())) {
                        currentDirection = bufferDirection;
                    } else if (currentDirection == Direction.NONE
                        || !map.isValidMove(position, currentDirection, canPassGhostDoor())) {
                        currentDirection = Direction.NONE;
                    }

                    if (previousDirection != currentDirection) {
                        // Alinha somente ao trocar de direção, como Pac-Man.
                        entity.setX(posX);
                        entity.setY(posY);
                    }
                }
            }
        }
        
        if (getPositionTilesX() == PacManApp.getPacMan().getPositionTilesX() && getPositionTilesY() == PacManApp.getPacMan().getPositionTilesY()) {
            if (state == GhostState.FRIGHTENED) {
                eat();
            } else {
                if (state.isHunting()) {
                    PacManApp.getPacMan().setDead(); 
                }
            }
        }

        updateSprite();

        if (currentDirection != Direction.NONE) {
            double pixelsPerSecond = FULL_SPEED_TILES_PER_SECOND * 8.0 * speedFactor(ctx);

            switch (currentDirection) {
                case RIGHT -> entity.translateX(pixelsPerSecond * tpf);
                case LEFT -> entity.translateX(-pixelsPerSecond * tpf);
                case UP -> entity.translateY(-pixelsPerSecond * tpf);
                case DOWN -> entity.translateY(pixelsPerSecond * tpf);
            }
        }

        handleBoundaries();
    }

    private boolean handleHousePixels() {
        switch (state) {
            case HOUSE_BOUNCE -> {
                double y = entity.getPosition().getY();
                if (y <= 144.0) {
                    entity.setY(144.0);
                    posY = 144;
                    currentDirection = Direction.DOWN;
                } else if (y >= 152.0) {
                    entity.setY(152.0);
                    posY = 152;
                    currentDirection = Direction.UP;
                } else if (currentDirection == Direction.NONE) {
                    currentDirection = Direction.DOWN;
                }
                bufferDirection = currentDirection;
                return true;
            }
            case HOUSE_ENTER -> {
                double x = entity.getPosition().getX();
                double y = entity.getPosition().getY();
                if (Math.abs(x - 120.0) <= 0.5 && y >= 128.0) {
                    entity.setX(120.0);
                    posX = 120;
                    if (y < 144.0) {
                        currentDirection = Direction.DOWN;
                    } else {
                        entity.setY(144.0);
                        posY = 144;
                        state = GhostState.HOUSE_EXIT;
                        currentDirection = Direction.UP;
                        bufferDirection = currentDirection;
                        return true;
                    }
                    bufferDirection = currentDirection;
                    return true;
                }
                return false;
            }
            case HOUSE_TO_DOOR -> {
                double x = entity.getPosition().getX();
                double y = entity.getPosition().getY();
                if (Math.abs(x - 120.0) > 0.5) {
                    currentDirection = x < 120.0 ? Direction.RIGHT : Direction.LEFT;
                } else {
                    entity.setX(120.0);
                    posX = 120;
                    if (y > 132.0) {
                        currentDirection = Direction.UP;
                    } else {
                        entity.setY(132.0);
                        posY = 132;
                        state = GhostState.HOUSE_EXIT;
                        currentDirection = Direction.UP;
                    }
                }
                bufferDirection = currentDirection;
                return true;
            }
            case HOUSE_EXIT -> {
                double x = entity.getPosition().getX();
                double y = entity.getPosition().getY();
                if (y > 124.0) {
                    currentDirection = Direction.UP;
                } else if (Math.abs(x - houseExitX) <= 0.5) {
                    entity.setX(houseExitX);
                    entity.setY(124.0);
                    posX = (int) houseExitX;
                    posY = 124;
                    state = globalMode;
                    reversePending = false;
                    lastDecisionTile = null;
                } else if (x < houseExitX) {
                    entity.setY(124.0);
                    posY = 124;
                    currentDirection = Direction.RIGHT;
                } else if (x > houseExitX) {
                    entity.setY(124.0);
                    posY = 124;
                    currentDirection = Direction.LEFT;
                }
                bufferDirection = currentDirection;
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    private boolean handleHouseMovement(Point tile, GameContext ctx) {
        switch (state) {
            case HOUSE_BOUNCE -> {
                if (tile.y() <= 18) {
                    currentDirection = Direction.DOWN;
                } else if (tile.y() >= 19) {
                    currentDirection = Direction.UP;
                } else if (currentDirection == Direction.NONE) {
                    currentDirection = Direction.DOWN;
                }
                bufferDirection = currentDirection;
                return true;
            }
            case HOUSE_TO_DOOR -> {
                if ((tile.x() == 14 || tile.x() == 15)
                    && tile.y() == 16) {
                    entity.setX(120.0);
                    posX = 120;
                    state = GhostState.HOUSE_EXIT;
                    currentDirection = Direction.UP;
                    bufferDirection = currentDirection;
                } else {
                    bufferDirection = decideAt(tile, currentDirection, ctx);
                    currentDirection = bufferDirection;
                }
                return true;
            }
            case HOUSE_ENTER -> {
                if ((tile.x() == 14 || tile.x() == 15)
                    && tile.y() == 16) {
                    entity.setX(120.0);
                    posX = 120;
                    currentDirection = Direction.DOWN;
                    bufferDirection = currentDirection;
                } else {
                    bufferDirection = decideAt(tile, currentDirection, ctx);
                    currentDirection = bufferDirection;
                }
                return true;
            }
            case HOUSE_EXIT -> {
                if (posY > 124) {
                    currentDirection = Direction.UP;
                } else if (posX > 108) {
                    currentDirection = Direction.LEFT;
                } else if (posY > 100) {
                    currentDirection = Direction.UP;
                } else {
                    state = globalMode;
                    reversePending = false;
                    currentDirection = Direction.UP;
                }
                bufferDirection = currentDirection;
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    private void followHouseRoute(Point tile) {
        if (houseRoute == null) {
            return;
        }

        if (houseRouteIndex < houseRoute.size()
            && tile.equals(houseRoute.get(houseRouteIndex))) {
            houseRouteIndex++;
        }

        if (houseRouteIndex >= houseRoute.size()) {
            if (state == GhostState.HOUSE_ENTER) {
                state = GhostState.HOUSE_BOUNCE;
                currentDirection = tile.y() >= houseBounceMaxY
                    ? Direction.UP
                    : Direction.DOWN;
                bufferDirection = currentDirection;
            } else if (state == GhostState.HOUSE_EXIT) {
                state = globalMode;
                reversePending = false;
                houseRoute = null;
            }
            return;
        }

        Point next = houseRoute.get(houseRouteIndex);
        if (next.x() > tile.x()) {
            currentDirection = Direction.RIGHT;
        } else if (next.x() < tile.x()) {
            currentDirection = Direction.LEFT;
        } else if (next.y() > tile.y()) {
            currentDirection = Direction.DOWN;
        } else if (next.y() < tile.y()) {
            currentDirection = Direction.UP;
        }
        bufferDirection = currentDirection;
    }

    private void updateSprite() {
        GhostSprite sprite = entity.getComponent(GhostSprite.class);
        if (sprite == null) {
            return;
        }

        if (state == GhostState.FRIGHTENED) {
            double frightenedRemaining = PacManApp.getOrchestrator().getFrightenedRemaining();
            if (frightenedRemaining > 0.0 && frightenedRemaining <= 2.0) {
                sprite.scared2();
            } else {
                sprite.scared1();
            }
        } else if (state == GhostState.EATEN || state == GhostState.HOUSE_ENTER) {
            sprite.dead();
            switch (currentDirection) {
                case RIGHT -> sprite.moveRight();
                case LEFT -> sprite.moveLeft();
                case UP -> sprite.moveUp();
                case DOWN -> sprite.moveDown();
            }
        } else {
            sprite.alive();
            if (state != GhostState.SCATTER) {
                switch (currentDirection) {
                    case RIGHT -> sprite.moveRight();
                    case LEFT -> sprite.moveLeft();
                    case UP -> sprite.moveUp();
                    case DOWN -> sprite.moveDown();
                }
            } else {
                sprite.scatter();
            }
        }
    }
}