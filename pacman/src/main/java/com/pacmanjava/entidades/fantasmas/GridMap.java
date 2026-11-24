// GridMap.java
package com.pacmanjava.entidades.fantasmas;

import com.pacmanjava.map.Lvl;

import static com.pacmanjava.utils.Utils.*;

/** Contrato mínimo do labirinto. isWall deve retornar true para y fora dos limites. */
public class GridMap {

    public boolean isWall(int x, int y) {
        if (y < 0 || y >= Lvl.level.length
                || x < 0 || x >= Lvl.level[y].length) {
            return true;
        }
        if (Lvl.level[y][x] == 1) {
            return true;
        }
        return false;
    }

    public boolean isGhostDoor(int x, int y) {
        if (Lvl.level[y][x] == 2) {
            return true;
        }
        return false;
    }

    public boolean isTunnel(int x, int y) {
        if (Lvl.level[y][x] == 13) {
            return true;
        }
        return false;
    }

    /** Zonas vermelhas: interseções onde fantasmas em CHASE/SCATTER não podem escolher CIMA. */
    public boolean forbidsUpwardTurn(int x, int y) {
        if (Lvl.level[y][x] == 14) {
            return true;
        }
        return false;
    }

    /** Vizinho com wrap horizontal (túnel). */
    public Point neighbor(Point from, Direction direction) {
        return new Point(Math.floorMod((int) from.x() + direction.x(), (WIDTH - 4) / 8), (int) from.y() + direction.y());
    }

    public boolean isValidMove(Point from, Direction direction, boolean canPassGhostDoor) {
        Point to = neighbor(from, direction);
        if (isWall(to.x(), to.y())) {
            return false;
        }
        return canPassGhostDoor || !isGhostDoor(to.x(), to.y());
    }
}