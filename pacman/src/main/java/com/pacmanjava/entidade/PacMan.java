package com.pacmanjava.entidade;

import com.almasb.fxgl.entity.Entity;

import static com.pacmanjava.utils.Utils.*;

public class PacMan extends EntidadeJogo {

    //coloca o player criado no PacManApp na variável player
    public PacMan(Entity entity) {
        this.entity = entity;
    }

    //maneja o movimento do jogador (movimentação e teleportagem nos extremos)
    public void handleMovement(double tpf) {
        posX = (int) entity.getPosition().getX();
        posY = (int) entity.getPosition().getY();

        bufferDirection = inputBufferDirection;

        //atualiza a direção do jogador
        if (onTile() && currentDirection != bufferDirection) {
            currentDirection = bufferDirection;
            
            //teleporta para exatamente o centro do tile
            entity.setX(posX);
            entity.setY(posY);
        }

        //movimentar jogador
        switch(currentDirection) {
            case "RIGHT":  
                entity.translateX(speed * tpf); 
                entity.getComponent(PacmanSprite.class).moveRight(); 
                break;   
            case "LEFT": 
                entity.translateX(-speed * tpf); 
                entity.getComponent(PacmanSprite.class).moveLeft(); 
                break;
            case "UP": 
                entity.translateY(-speed * tpf); 
                entity.getComponent(PacmanSprite.class).moveUp(); 
                break;
            case "DOWN": 
                entity.translateY(speed * tpf); 
                entity.getComponent(PacmanSprite.class).moveDown(); 
                break;
        }

        handleBoundaries();
    }
}