package com.pacmanjava.entidade;

import com.almasb.fxgl.entity.Entity;
import com.pacmanjava.map.Lvl;

import static com.pacmanjava.utils.Utils.*;

public abstract class EntidadeJogo {
    protected int posX;
    protected int posY;
    protected double speed;
    protected String bufferDirection = ""; //seguinte direcao guardada
    protected String currentDirection = ""; //direcao atual de movimento
    protected Entity entity;
    protected boolean dead;

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

    public byte getTile() {
        //um tile == 8 pixels
        int posTilesX = (posX - 4) / 8;
        int posTilesY = (posY - 4) / 8;
        int nearestTileX = Math.round(posTilesX);
        int nearestTileY = Math.round(posTilesY);

        return Lvl.level[nearestTileY][nearestTileX];
    }

    public byte getTileInFront(String direction) {
        //um tile == 8 pixels
        int posTilesX = (posX - 4) / 8;
        int posTilesY = (posY - 4) / 8;
        int nearestTileX = Math.round(posTilesX);
        int nearestTileY = Math.round(posTilesY);

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
    
    public abstract void handleMovement(double tpf);
}