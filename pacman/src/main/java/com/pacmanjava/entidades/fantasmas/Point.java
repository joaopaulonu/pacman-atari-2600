package com.pacmanjava.entidades.fantasmas;

import com.pacmanjava.utils.Utils.*;

//Coordenada de tile imutável. Eixo Y cresce para baixo; valores negativos são válidos (alvos fora do mapa).
public record Point(int x, int y) {

    public int x() {
        return x;
    }
    
    public int y() {
        return y;
    }

    public Point plus(Point other) {
        return new Point(x + other.x, y + other.y);
    }

    public Point minus(Point other) {
        return new Point(x - other.x, y - other.y);
    }

    public Point translate(int dx, int dy) {
        return new Point(x + dx, y + dy);
    }

    public Point ahead(Direction direction, int tiles) {
        return new Point(x + direction.x() * tiles, y + direction.y() * tiles);
    }

    public int distanceSquared(Point other) {
        int dx = (int) x - other.x;
        int dy = (int) y - other.y;
        return dx * dx + dy * dy;
    }

    public double distanceTo(Point other) {
        return Math.sqrt(distanceSquared(other));
    }
}