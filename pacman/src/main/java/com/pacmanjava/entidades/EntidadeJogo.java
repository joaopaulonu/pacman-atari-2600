package com.pacmanjava.entidades;

import com.almasb.fxgl.entity.Entity;
import com.pacmanjava.map.Lvl;
import javafx.util.Duration;

import static com.pacmanjava.utils.Utils.*;
import static com.almasb.fxgl.dsl.FXGL.*;

public abstract class EntidadeJogo {
    protected int posX;
    protected int posY;
    protected double speed;
    protected String bufferDirection = ""; //seguinte direcao guardada
    protected String currentDirection = ""; //direcao atual de movimento
    protected Entity entity;
    protected boolean dead = false;
    protected boolean paused = false;

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

        return Lvl.level[posTilesX][posTilesY];
    }

    //pega o tile na frente da direção
    public byte getTileInFront(String direction) {
        int posTilesX = (posX - 4) / 8;
        int posTilesY = (posY - 4) / 8;

            switch(direction) {
                case "RIGHT": 
                    return Lvl.level[posTilesY][Math.floorMod(posTilesX + 1, WIDTH / 8)];
                case "LEFT": 
                    return Lvl.level[posTilesY][Math.floorMod(posTilesX - 1, WIDTH / 8)];
                case "UP": 
                    return Lvl.level[Math.floorMod(posTilesY - 1, HEIGHT / 8)][posTilesX];
                case "DOWN": 
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

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    //congela uma entidade
    public void pause() {
        if (entity.getComponent(PacmanSprite.class) != null) {
            entity.getComponent(PacmanSprite.class).setPaused();
        }
        this.paused = true;
    }

    //descongela
    public void resume() {
        if (entity.getComponent(PacmanSprite.class) != null) {
            entity.getComponent(PacmanSprite.class).setResumed();
        }
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