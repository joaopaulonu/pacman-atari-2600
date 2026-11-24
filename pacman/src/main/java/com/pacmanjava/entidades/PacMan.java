package com.pacmanjava.entidades;

import com.almasb.fxgl.audio.Sound;
import javafx.util.Duration;
import com.almasb.fxgl.time.TimerAction;
import com.pacmanjava.PacManApp;
import com.pacmanjava.consumables.Consumable;
import com.pacmanjava.consumables.Dot;
import com.pacmanjava.map.Lvl;
import com.pacmanjava.utils.Utils.Direction;
import com.pacmanjava.utils.Utils.PacmanSprite;
import com.pacmanjava.entidades.Ghost.GhostState;
import com.pacmanjava.entidades.fantasmas.PacmanGameOrchestrator;
import com.pacmanjava.entidades.fantasmas.Point;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.pacmanjava.utils.Utils.*;

public class PacMan extends EntidadeJogo {

    private boolean dying;
    private static int lives = 4;
    private float deathAnimationTime = 0;
    private Sound death, siren, siren1, siren2, siren3, siren4;
    private static TimerAction timerAction;
    private boolean movedAfterSpawn = false;
    public boolean hasMovedAfterSpawn() {
        return movedAfterSpawn;
    }
    public static int getLives() {
        return lives;
    }
    public static void setLives(int l) {
        lives = l;
    }

    public boolean isDying() {
        return dying;
    }

    public Point getPosition() {
        return new Point((posX - 4)/ 8, (posY - 4)/ 8);
    }

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
        movedAfterSpawn = false;
    }

    public void setDead() {
        dying = true;
    }

    public void pause() {
        if (entity.getComponent(PacmanSprite.class) != null) {
            entity.getComponent(PacmanSprite.class).setPaused();
        }
        super.pause();
    }

    public void resume() {
        if (entity.getComponent(PacmanSprite.class) != null) {
            entity.getComponent(PacmanSprite.class).setResumed();
        }
        super.resume();
    }
    
    //faz a animação de morte
    private boolean playedDeath = false; //se já tocou o audio de morte
    private void playDeathAnimation(double tpf) {
        siren.getAudio().stop();
        
        if (timerAction != null) {
            timerAction.expire();
            timerAction = null;
        }
        
        bufferDirection = Direction.NONE;
        currentDirection = Direction.NONE;
        
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
            movedAfterSpawn = false;
            lives--;
            //onPacmanLifeLost()
        }
    }
    
    private int playedSiren = 0; //se já tocou o audio do alarme
    private boolean eyespause;
    private void playSiren() {
        if (!hasMovedAfterSpawn()) { return; }

        if (timerAction != null) {
            for (Ghost g : PacManApp.getOrchestrator().ghosts()) {
                if (g.state() == GhostState.HOUSE_ENTER) {
                    eyespause = true;
                    break;
                }
                eyespause = false;
            }

            if (PacManApp.getOrchestrator().getFrightenedRemaining() > 0 || eyespause) {
                if (!timerAction.isPaused()) {
                    timerAction.pause();
                    return;
                }
            } else {
                if (timerAction.isPaused()) {
                    timerAction.resume();
                }
            }

            if (timerAction.isPaused()) { return; }
        }

        if (Dot.getConsumed() >= 0) {
            if (currentDirection != Direction.NONE && playedSiren == 0) {
                siren.getAudio().play();
                timerAction = getGameTimer().runAtInterval(() -> {
                    siren.getAudio().play();
                }, Duration.millis(390));
                playedSiren++;
            }
        }
        if (Dot.getConsumed() >= 116) {
            if (playedSiren == 1) {
                timerAction.expire();
                siren.getAudio().stop();
                siren1.getAudio().play();
                timerAction = getGameTimer().runAtInterval(() -> {
                    siren1.getAudio().play();
                }, Duration.millis(340));
                playedSiren++;
            }
        }
        if (Dot.getConsumed() >= 180) {
            if (playedSiren == 2) {
                timerAction.expire();
                siren.getAudio().stop();
                siren2.getAudio().play();
                timerAction = getGameTimer().runAtInterval(() -> {
                    siren2.getAudio().play();
                }, Duration.millis(320));
                playedSiren++;
            }
        }
        if (Dot.getConsumed() >= 212) {
            if (playedSiren == 3) {
                timerAction.expire();
                siren.getAudio().stop();
                siren3.getAudio().play();
                timerAction = getGameTimer().runAtInterval(() -> {
                    siren3.getAudio().play();
                }, Duration.millis(300));
                playedSiren++;
            }
        }
        if (Dot.getConsumed() >= 228) {
            if (playedSiren == 4) {
                timerAction.expire();
                siren.getAudio().stop();
                siren4.getAudio().play();
                timerAction = getGameTimer().runAtInterval(() -> {
                    siren4.getAudio().play();
                }, Duration.millis(280));
                playedSiren++;
            }
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
            if (timerAction != null) {
                timerAction.expire();
            }
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
            if (bufferDirection != Direction.NONE && getTileInFront(bufferDirection) != 1 && getTileInFront(bufferDirection) != 2) {
                currentDirection = bufferDirection;
                spawned = false;

                if (!movedAfterSpawn) {
                    movedAfterSpawn = true;
                }
            }
        } else {
            if (bufferDirection == Direction.NONE) {
                spawned = true;
            }
        }

        //atualiza a direção do jogador
        if (onTile()) {
            if (currentDirection != bufferDirection && getTileInFront(bufferDirection) != 1 && getTileInFront(bufferDirection) != 2) {
                currentDirection = bufferDirection;
                
                //alinha exatamente no centro do tile
                entity.setX(posX);
                entity.setY(posY);

                entity.getComponent(PacmanSprite.class).setResumed();
            }

            //colisão com as paredes
            if (getTileInFront(currentDirection) == 1 || getTileInFront(currentDirection) == 2) {
                currentDirection = Direction.NONE;

                //alinha exatamente no centro do tile
                entity.setX(posX);
                entity.setY(posY);

                entity.getComponent(PacmanSprite.class).setPaused(); 
            }
        }

        //movimentar jogador
        switch(currentDirection) {
            case RIGHT:  
                entity.translateX(speed * tpf); 
                entity.getComponent(PacmanSprite.class).moveRight(); 
                break;   
            case LEFT: 
                entity.translateX(-speed * tpf); 
                entity.getComponent(PacmanSprite.class).moveLeft(); 
                break;
            case UP: 
                entity.translateY(-speed * tpf); 
                entity.getComponent(PacmanSprite.class).moveUp(); 
                break;
            case DOWN: 
                entity.translateY(speed * tpf); 
                entity.getComponent(PacmanSprite.class).moveDown(); 
                break;
        }

        handleBoundaries();
    }
}