package com.pacmanjava.entidade;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.audio.Sound;
import javafx.util.Duration;
import com.almasb.fxgl.time.TimerAction;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.pacmanjava.utils.Utils.*;

public class PacMan extends EntidadeJogo {

    private boolean dying;
    private float deathAnimationTime = 0;
    private Sound death, siren;
    private TimerAction timerAction;

    //coloca o player criado no PacManApp na variável entity
    public PacMan() {
        this.entity = entityBuilder()
            .at((WIDTH / 2), (HEIGHT / 2) + 68)
            .with(new PacmanSprite())
            .buildAndAttach();
        speed = 60;

        death = getAssetLoader().loadSound("death.wav");
        siren = getAssetLoader().loadSound("siren0.wav");
    }

    public void setDead(boolean dead) {
        dying = dead;
    }

    private boolean played = false;
    private boolean playedSiren = false;

    //maneja o movimento do jogador (movimentação e teleportagem nos extremos)
    public void handleMovement(double tpf) {

        if (dead) { return; }

        if (dying) { 
            siren.getAudio().stop();
            timerAction.expire();

            bufferDirection = "";
            currentDirection = "";

            deathAnimationTime += tpf;

            if (deathAnimationTime >= 2) {
                if (!played) {
                    death.getAudio().play();
                    played = true;
                }
                entity.getComponent(PacmanSprite.class).dying();
            } else {
                entity.getComponent(PacmanSprite.class).idle();
            }

            if (deathAnimationTime >= 4) {
                entity.getComponent(PacmanSprite.class).dead();
                dead = true;
            }

            return; 
        }


        if (bufferDirection != "" && !playedSiren) {
            siren.getAudio().play();
            timerAction = getGameTimer().runAtInterval(() -> {
                siren.getAudio().play();
            }, Duration.millis(400));
            playedSiren = true;
        }

        posX = (int) entity.getPosition().getX();
        posY = (int) entity.getPosition().getY();

        if (!paused) {
            bufferDirection = getInputBufferDirection();
        } else {
            setInputBufferDirection("");
        }

        //atualiza a direção do jogador
        if ((onTile() || currentDirection.equals("")) && currentDirection != bufferDirection && getTileInFront(bufferDirection) == 0) {
            currentDirection = bufferDirection;
            
            //alinha exatamente no centro do tile
            entity.setX(posX);
            entity.setY(posY);
        }

        if (onTile() && getTileInFront(currentDirection) != 0) {
            currentDirection = "";

            //alinha exatamente no centro do tile
            entity.setX(posX);
            entity.setY(posY);

            entity.getComponent(PacmanSprite.class).idle(); 
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