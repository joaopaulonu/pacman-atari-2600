// GridMap.java
package com.pacmanjava.entidades.fantasmas;

import static com.pacmanjava.utils.Utils.*;

/** Contrato mínimo do labirinto. isWall deve retornar true para y fora dos limites. */
public interface GridMap {

    int width();

    boolean isWall(int x, int y);

    default boolean isGhostDoor(int x, int y) {
        return false;
    }

    default boolean isTunnel(int x, int y) {
        return false;
    }

    /** Zonas vermelhas: interseções onde fantasmas em CHASE/SCATTER não podem escolher CIMA. */
    default boolean forbidsUpwardTurn(int x, int y) {
        return false;
    }

    /** Vizinho com wrap horizontal (túnel). */
    default Point neighbor(Point from, Direction direction) {
        return new Point(Math.floorMod((int) from.x() + direction.x(), width()), (int) from.y() + direction.y());
    }

    default boolean isValidMove(Point from, Direction direction, boolean canPassGhostDoor) {
        Point to = neighbor(from, direction);
        if (isWall(to.x(), to.y())) {
            return false;
        }
        return canPassGhostDoor || !isGhostDoor(to.x(), to.y());
    }
}