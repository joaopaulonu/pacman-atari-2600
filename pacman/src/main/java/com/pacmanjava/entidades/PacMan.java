package com.pacmanjava.entidades;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.audio.Sound;
import javafx.util.Duration;
import com.almasb.fxgl.time.TimerAction;
import com.pacmanjava.consumables.Consumable;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.pacmanjava.utils.Utils.*;

public class PacMan extends EntidadeJogo {

    private boolean dying;
    private float deathAnimationTime = 0;
    private Sound death, siren;
    private TimerAction timerAction;

    public PacMan() {
        this.entity = entityBuilder()
            .at((WIDTH / 2), (HEIGHT / 2) + 68)
            .with(new PacmanSprite())
            .with(new FixedUpdateComponent(this))
            .buildAndAttach();
        speed = 60;

        death = getAssetLoader().loadSound("death.wav");
        siren = getAssetLoader().loadSound("siren0.wav");
    }

    public void setDead(boolean dead) {
        dying = dead;
    }

    private boolean playedDeath = false; //se já tocou o audio de morte
    private boolean playedSiren = false; //se já tocou o audio do alarme

    //faz a animação de morte
    private void playDeathAnimation(double tpf) {
        siren.getAudio().stop();

        if (timerAction != null) {
            timerAction.expire();
        }

        bufferDirection = "";
        currentDirection = "";

        deathAnimationTime += tpf;

        if (deathAnimationTime >= 2) {
            if (!playedDeath) {
                death.getAudio().play();
                playedDeath = true;
            }
            entity.getComponent(PacmanSprite.class).dying();
        } else {
            entity.getComponent(PacmanSprite.class).setPaused();
        }

        if (deathAnimationTime >= 4) {
            entity.getComponent(PacmanSprite.class).dead();
            dead = true;
        }
    }

    //maneja o movimento do jogador (movimentação e teleportagem nos extremos)
    public void handleMovement(double tpf) {

        if (dead) { return; }

        if (dying) { 
            playDeathAnimation(tpf);
            return; 
        }

        //audio do alarme
        if (bufferDirection != "" && !playedSiren) {
            siren.getAudio().play();
            timerAction = getGameTimer().runAtInterval(() -> {
                siren.getAudio().play();
            }, Duration.millis(400));
            playedSiren = true;
        }

        if (paused) { return; } //não passaa de aqui se o pac-man é pausado

        posX = (int) entity.getPosition().getX(); //atualiza a posição
        posY = (int) entity.getPosition().getY(); //atualiza a posição
        
        bufferDirection = getInputBufferDirection(); //guarda o input do usuario

        //consome os dots
        if (onTile() && Consumable.getConsumable((posX - 4) / 8, (posY - 4) / 8) != null) {
            Consumable.getConsumable((posX - 4) / 8, (posY - 4) / 8).consume();
            pauseFor(Duration.seconds(1 / 60));
            return;
        }

        //atualiza a direção do jogador
        if ((onTile() || currentDirection.equals("")) && currentDirection != bufferDirection && getTileInFront(bufferDirection) != 1) {
            currentDirection = bufferDirection;
            
            //alinha exatamente no centro do tile
            entity.setX(posX);
            entity.setY(posY);

            entity.getComponent(PacmanSprite.class).setResumed();
        }

        //colisão com as paredes
        if (onTile() && getTileInFront(currentDirection) == 1) {
            currentDirection = "";

            //alinha exatamente no centro do tile
            entity.setX(posX);
            entity.setY(posY);

            entity.getComponent(PacmanSprite.class).setPaused(); 
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