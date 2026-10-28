package com.pacmanjava.entidade;

import com.almasb.fxgl.entity.Entity;

import static com.pacmanjava.utils.Utils.*;

public abstract class EntidadeJogo {
    protected int posX;
    protected int posY;
    protected double speed = 60;
    protected String bufferDirection = ""; //seguinte direcao guardada
    protected String currentDirection = ""; //direcao atual de movimento
    protected Entity entity;

    //verifica se a a posição passada por argumento tá dentro de um tile
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

    protected void handleBoundaries() {
        //teleporta o jogador para o extremo oposto do mapa
        if (Math.floorMod(posX, WIDTH) != posX) { 
            entity.setX(Math.floorMod(posX, WIDTH));
        }
        if (Math.floorMod(posY, HEIGHT) != posY) {
            entity.setY(Math.floorMod(posY, HEIGHT));
        }
    }

    public abstract void handleMovement(double tpf);
}