// PacmanGameOrchestrator.java
package com.pacmanjava.entidades.fantasmas;

import java.util.List;

import com.pacmanjava.entidades.Ghost;
import com.pacmanjava.entidades.Ghost.GhostState;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.time.TimerAction;
import com.pacmanjava.PacManApp;
import com.pacmanjava.map.Lvl;
import com.pacmanjava.utils.Utils.Direction;

import javafx.util.Duration;

import static com.pacmanjava.entidades.Ghost.GhostState;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.pacmanjava.utils.Utils.*;

public final class PacmanGameOrchestrator {

    private final Blinky blinky;
    private final List<Ghost> ghosts;
    private final LevelSettings level;

    private int phaseIndex; //in modeDurations, gets the current phase
    private double phaseElapsed; //elapsed seconds of the phase
    private GhostState globalMode = GhostState.SCATTER; //ghost state
    private double frightenedRemaining; //remaining frightened seconds
    private double houseExitElapsed;
    private boolean pinkyReleased;
    private boolean inkyReleased;
    private boolean clydeReleased;
    private TimerAction frightLoop, eyesLoop;
    private Sound frightSound, eyesSound;

    public PacmanGameOrchestrator(LevelSettings level, Blinky blinky, Pinky pinky, Inky inky, Clyde clyde) {
        this.level = level;
        this.blinky = blinky;
        frightSound = getAssetLoader().loadSound("fright.wav");
        eyesSound = getAssetLoader().loadSound("eyes.wav");

        // Ordem fixa de atualização do arcade: Blinky, Pinky, Inky, Clyde
        this.ghosts = List.of(blinky, pinky, inky, clyde);
    }

    //update loop
    public void update(double deltaSeconds, Point pacmanTile, Direction pacmanDirection, int dotsRemaining) {
        if (!PacManApp.getPacMan().hasMovedAfterSpawn()
            || PacManApp.getPacMan().isDead()
            || PacManApp.getPacMan().isDying()
            || Lvl.getWin()) {
            return;
        }

        tickFrightened(deltaSeconds);
        if (frightenedRemaining == 0) { // o relógio Scatter/Chase pausa durante FRIGHTENED
            advanceSchedule(deltaSeconds);
        }
        advanceHouseExitSchedule(deltaSeconds);
        // A movimentação por pixel é executada por Ghost.handleMovement(), chamado pelo
        // FixedUpdateComponent da entidade, como no movimento do Pac-Man.
    }

    //EnergizerEatenEvent
    public void onEnergizerEaten() {
        double seconds = level.frightenedSeconds();
        if (seconds <= 0) {
            ghosts.forEach(Ghost::reverseOnly);
            return;
        }
        frightenedRemaining = seconds;
        ghosts.forEach(Ghost::frighten);

        if (frightLoop == null) {
            
            frightSound.getAudio().play();
            frightLoop = getGameTimer().runAtInterval(() -> {
                boolean eaten = false;
                boolean frightened = false;
                for (Ghost g: ghosts) {
                    if (g.state() == GhostState.EATEN) {
                        eaten = true;
                        break;
                    }

                    if (g.state() == GhostState.FRIGHTENED) {
                        frightened = true;
                        break;
                    }
                }

                if (!eaten && frightened) {           
                    frightSound.getAudio().play();
                }
            }, Duration.millis(1050));
        }
    }

    public void onGhostEaten(Ghost ghost) {
        if (!ghosts.contains(ghost)) {
            throw new IllegalArgumentException("fantasma desconhecido");
        }
        
        eyesSound.getAudio().pause();
        play("eat_ghost.wav");
        PacManApp.getPacMan().pauseFor(Duration.seconds(1.2));
        
        for (Ghost g : ghosts) {
            g.pauseFor(Duration.seconds(1.2));
        }

        if (eyesLoop == null) {
            
            eyesLoop = getGameTimer().runAtInterval(() -> {
                boolean eaten = false;
                for (Ghost g: ghosts) {
                    if (!g.isPaused() && g.state() == GhostState.EATEN) {
                        eaten = true;
                        break;
                    }
                }

                if (eaten) {    
                    System.out.println("hi");       
                    eyesSound.getAudio().play();
                }
            }, Duration.millis(600));
        }
    }

    public void allowGhostToLeave(Ghost ghost) {
        if (!ghosts.contains(ghost)) {
            throw new IllegalArgumentException("fantasma desconhecido");
        }
        ghost.requestHouseExit();
    }

    /** Elroy é suspenso após a morte do Pac-Man até Clyde sair da casa. */
    public void onPacmanLifeLost() {
        blinky.setElroySuspended(true);
    }

    public void onClydeLeftHouse() {
        blinky.setElroySuspended(false);
    }

    public GhostState globalMode() {
        return globalMode;
    }

    public double getFrightenedRemaining() {
        return frightenedRemaining;
    }

    public List<Ghost> ghosts() {
        return ghosts;
    }

    //ticks the fright timer
    private void tickFrightened(double deltaSeconds) {
        if (frightenedRemaining == 0) { return; }

        frightenedRemaining -= deltaSeconds;

        if (frightenedRemaining <= 0) {
            frightenedRemaining = 0;
            ghosts.forEach(Ghost::endFright);
        }
    }

    private void advanceHouseExitSchedule(double deltaSeconds) {
        houseExitElapsed += deltaSeconds;

        if (!pinkyReleased && houseExitElapsed >= 1.0) {
            pinkyReleased = true;
            for (Ghost ghost : ghosts) {
                if (ghost instanceof Pinky) {
                    allowGhostToLeave(ghost);
                }
            }
        }
        if (!inkyReleased && houseExitElapsed >= 3.0) {
            inkyReleased = true;
            for (Ghost ghost : ghosts) {
                if (ghost instanceof Inky) {
                    allowGhostToLeave(ghost);
                }
            }
        }
        if (!clydeReleased && houseExitElapsed >= 5.0) {
            clydeReleased = true;
            for (Ghost ghost : ghosts) {
                if (ghost instanceof Clyde) {
                    allowGhostToLeave(ghost);
                }
            }
        }
    }

    //advances de game phase
    private void advanceSchedule(double deltaSeconds) {
        List<Double> durations = level.modeDurations();

        phaseElapsed += deltaSeconds; //contador em segundos

        // A última fase é infinita (CHASE permanente), então o índice nunca estoura
        while (phaseElapsed >= durations.get(phaseIndex)) {

            phaseElapsed -= durations.get(phaseIndex); //reseta o timer
            phaseIndex++; //vai na seguinte fase
            
            globalMode = phaseIndex % 2 == 0 ? GhostState.SCATTER : GhostState.CHASE; //ciclo entre CHASE e SCATTER
            
            //aplica o estado do fantasma
            for (Ghost ghost : ghosts) {
                ghost.applyGlobalMode(globalMode);
            }
        }
    }
}