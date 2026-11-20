// PacmanGameOrchestrator.java
package com.pacmanjava.entidades.fantasmas;

import java.util.List;
import java.util.Objects;

import com.pacmanjava.entidades.Ghost;
import static com.pacmanjava.entidades.Ghost.GhostState;

import static com.pacmanjava.utils.Utils.*;

public final class PacmanGameOrchestrator {

    private final Blinky blinky;
    private final List<Ghost> ghosts;
    private final LevelSettings level;

    private int phaseIndex; //in modeDurations, gets the current phase
    private double phaseElapsed; //elapsed seconds of the phase
    private GhostState globalMode = GhostState.SCATTER; //ghost state
    private double frightenedRemaining; //remaining frightened seconds

    public PacmanGameOrchestrator(LevelSettings level, Blinky blinky, Pinky pinky, Inky inky, Clyde clyde) {
        this.level = level;
        this.blinky = blinky;

        // Ordem fixa de atualização do arcade: Blinky, Pinky, Inky, Clyde
        this.ghosts = List.of(blinky, pinky, inky, clyde);
    }

    //update loop
    public void update(double deltaSeconds, Point pacmanTile, Direction pacmanDirection, int dotsRemaining) {
        tickFrightened(deltaSeconds);
        if (frightenedRemaining == 0) { // o relógio Scatter/Chase pausa durante FRIGHTENED
            advanceSchedule(deltaSeconds);
        }
        GameContext ctx = new GameContext(pacmanTile, pacmanDirection, dotsRemaining, level);
        for (Ghost ghost : ghosts) {
            ghost.update(deltaSeconds, ctx);
        }
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
    }

    public void onGhostEaten(Ghost ghost) {
        if (!ghosts.contains(ghost)) {
            throw new IllegalArgumentException("fantasma desconhecido");
        }
        ghost.eat();
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