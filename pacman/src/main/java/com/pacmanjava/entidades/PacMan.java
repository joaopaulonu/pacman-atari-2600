package com.pacmanjava.entidades;

import com.almasb.fxgl.audio.Sound;
import javafx.util.Duration;
import com.almasb.fxgl.time.TimerAction;
import com.pacmanjava.consumables.Consumable;
import com.pacmanjava.consumables.Dot;
import com.pacmanjava.map.Lvl;
import com.pacmanjava.utils.Utils.PacmanSprite;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.pacmanjava.utils.Utils.*;

public class PacMan extends EntidadeJogo {

    private boolean dying;
    private float deathAnimationTime = 0;
    private Sound death, siren, siren1, siren2, siren3, siren4;
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
        siren1 = getAssetLoader().loadSound("siren1.wav");
        siren2 = getAssetLoader().loadSound("siren2.wav");
        siren3 = getAssetLoader().loadSound("siren3.wav");
        siren4 = getAssetLoader().loadSound("siren4.wav");
    }

    public void setDead(boolean dead) {
        dying = dead;
    }

    
    //faz a animação de morte
    private boolean playedDeath = false; //se já tocou o audio de morte
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
    
    private int playedSiren = 0; //se já tocou o audio do alarme
    private void playSiren() {
        switch(Dot.getConsumed()) {
            case 0:
                if (bufferDirection != "" && playedSiren == 0) {
                    siren.getAudio().play();
                    timerAction = getGameTimer().runAtInterval(() -> {
                        siren.getAudio().play();
                    }, Duration.millis(390));
                    playedSiren++;
                }
                break;
                case 116:
                    if (playedSiren == 1) {
                        timerAction.expire();
                        siren.getAudio().stop();
                        siren1.getAudio().play();
                        timerAction = getGameTimer().runAtInterval(() -> {
                            siren1.getAudio().play();
                        }, Duration.millis(340));
                        playedSiren++;
                    }
                    break;
                case 180:
                    if (playedSiren == 2) {
                        timerAction.expire();
                        siren.getAudio().stop();
                        siren2.getAudio().play();
                        timerAction = getGameTimer().runAtInterval(() -> {
                            siren2.getAudio().play();
                        }, Duration.millis(290));
                        playedSiren++;
                    }
                    break;
                case 212:
                    if (playedSiren == 3) {
                        timerAction.expire();
                        siren.getAudio().stop();
                        siren3.getAudio().play();
                        timerAction = getGameTimer().runAtInterval(() -> {
                            siren3.getAudio().play();
                        }, Duration.millis(240));
                        playedSiren++;
                    }
                    break;
                case 228:
                    if (playedSiren == 4) {
                        timerAction.expire();
                        siren.getAudio().stop();
                        siren4.getAudio().play();
                        timerAction = getGameTimer().runAtInterval(() -> {
                            siren4.getAudio().play();
                        }, Duration.millis(190));
                        playedSiren++;
                    }
                    break;
            }
        }
                    
    //maneja o movimento do jogador (movimentação e teleportagem nos extremos)
    private boolean spawned = true;
    public void handleMovement(double tpf, int epf) {
        if (dead) { return; }

        if (dying) { 
            playDeathAnimation(tpf);
            return; 
        }

        if (Lvl.getWin()) {
            entity.getComponent(PacmanSprite.class).idle();
            return;
        }

        //audio do alarme
        playSiren();

        if (paused) { return; } //não passa de aqui se o pac-man é pausado

        posX = (int) entity.getPosition().getX(); //atualiza a posição
        posY = (int) entity.getPosition().getY(); //atualiza a posição
        
        bufferDirection = getInputBufferDirection(); //guarda o input do usuario

        //consome os dots
        Consumable c = Consumable.getConsumable((posX - 4) / 8, (posY - 4) / 8);
        if (onTile() && c != null) {
            c.consume();
            if (epf == 1) {
                pauseFor(Duration.millis(16.67));
            }
            return;
        }

        if (spawned) {
            if (!bufferDirection.equals("") && getTileInFront(bufferDirection) != 1) {
                currentDirection = bufferDirection;
                spawned = false;
            }
        } else {
            if (bufferDirection.equals("")) {
                spawned = true;
            }
        }

        //atualiza a direção do jogador
        if (onTile()) {
            if (currentDirection != bufferDirection && getTileInFront(bufferDirection) != 1) {
                currentDirection = bufferDirection;
                
                //alinha exatamente no centro do tile
                entity.setX(posX);
                entity.setY(posY);

                entity.getComponent(PacmanSprite.class).setResumed();
            }

            //colisão com as paredes
            if (getTileInFront(currentDirection) == 1) {
                currentDirection = "";

                //alinha exatamente no centro do tile
                entity.setX(posX);
                entity.setY(posY);

                entity.getComponent(PacmanSprite.class).setPaused(); 
            }
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