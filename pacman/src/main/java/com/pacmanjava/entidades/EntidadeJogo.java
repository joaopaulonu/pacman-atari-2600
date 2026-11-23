package com.pacmanjava.entidades;

import com.almasb.fxgl.entity.Entity;
import com.pacmanjava.map.Lvl;
import com.pacmanjava.utils.Utils.Direction;

import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.pacmanjava.utils.Utils.*;

public abstract class EntidadeJogo {
    protected int posX;
    protected int posY;
    protected double speed;
    protected Direction bufferDirection = Direction.NONE; //seguinte direcao guardada
    protected Direction currentDirection = Direction.NONE; //direcao atual de movimento
    protected Entity entity;
    protected boolean dead = false;
    protected boolean paused = false;

    public Direction getBufferDirection() {
        return bufferDirection;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isPaused() {
        return paused;
    }

    protected int getPositionTilesX() {
        return (posX - 4) / 8;
    }

    protected int getPositionTilesY() {
        return (posY - 4) / 8;
    }

    //verifica se a entidade tá dentro de um tile
    protected boolean onTile() {
        //um tile == 8 pixels
        float posTilesX = (float)(posX - 4) / 8;
        float posTilesY = (float)(posY - 4) / 8;
        float nearestTileX = (float)Math.round(posTilesX);
        float nearestTileY = (float)Math.round(posTilesY);

        if (posTilesX == nearestTileX && posTilesY == nearestTileY) {
            return true;
        }

        return false;
    } 

    //pega o byte na matriz 'level' que representa o tile
    public byte getTile() {
        int posTilesX = (posX - 4) / 8;
        int posTilesY = (posY - 4) / 8;

        return Lvl.level[posTilesY][posTilesX];
    }

    //pega o tile na frente da direção
    public byte getTileInFront(Direction direction) {
        int posTilesX = (posX - 4) / 8;
        int posTilesY = (posY - 4) / 8;

            switch(direction) {
                case RIGHT: 
                    return Lvl.level[posTilesY][Math.floorMod(posTilesX + 1, WIDTH / 8)];
                case LEFT: 
                    return Lvl.level[posTilesY][Math.floorMod(posTilesX - 1, WIDTH / 8)];
                case UP: 
                    return Lvl.level[Math.floorMod(posTilesY - 1, HEIGHT / 8)][posTilesX];
                case DOWN: 
                    return Lvl.level[Math.floorMod(posTilesY + 1, HEIGHT / 8)][posTilesX];
            }

        return Lvl.level[posTilesY][posTilesX];
    }

    protected void handleBoundaries() {
        //teleporta a entidade para o extremo oposto do mapa
        if (Math.floorMod(posX, WIDTH) != posX) { 
            entity.setX(Math.floorMod(posX, WIDTH));
        }
        if (Math.floorMod(posY, HEIGHT) != posY) {
            entity.setY(Math.floorMod(posY, HEIGHT));
        }
    }

    public void setDead() {
        this.dead = true;
    }

    //congela uma entidade
    public void pause() {
        this.paused = true;
    }

    //descongela
    public void resume() {
        this.paused = false;
    }

    public void pauseFor(Duration time) {
        pause();

        getGameTimer().runOnceAfter(() -> {
            resume();
        }, time);
    }
    
    public abstract void handleMovement(double tpf, int epf);
}