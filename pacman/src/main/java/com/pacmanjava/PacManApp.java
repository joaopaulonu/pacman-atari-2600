package com.pacmanjava;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.pacmanjava.consumables.Consumable;
import com.pacmanjava.consumables.Dot;
import com.pacmanjava.consumables.Fruit;
import com.pacmanjava.entidades.PacMan;
import com.pacmanjava.map.Lvl;
import com.pacmanjava.ui.MainMenu;
import com.pacmanjava.ui.ResumeGame;
import com.pacmanjava.utils.Debug;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.pacmanjava.utils.Utils.*;

public class PacManApp extends GameApplication {
    private PacMan pacman;
    private boolean bonus = false;
    private boolean bonus2 = false;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
        settings.setTitle("Pac-Man");
        settings.setVersion("1.0");
        settings.setManualResizeEnabled(true);
        settings.setPreserveResizeRatio(true);
        settings.setFullScreenAllowed(true);
        settings.setMainMenuEnabled(true);
        settings.setGameMenuEnabled(true);
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newMainMenu() {
                return new MainMenu();
            }

            @Override
            public FXGLMenu newGameMenu() {
                return new ResumeGame();
            }
        });
        Debug.showFps();
    }

    @Override
    protected void initGame() {
        Lvl.createLevelMatrix("tilemap.txt");
        Dot.placeDots();

        pacman = new PacMan();

        //intro (musica e pausa o jogador)
        play("start.wav");
        pacman.pause();
        runOnce(() -> {
            setInputBufferDirection("");
            pacman.resume();
        }, Duration.seconds(4));

        bonus = false;
        bonus2 = false;
    }

    //adiciona e cria as interações dos controles do jogo
    @Override
    protected void initInput() {
        onKey(KeyCode.RIGHT, () -> {
            setInputBufferDirection("RIGHT");
        });
        onKey(KeyCode.LEFT, () -> {
            setInputBufferDirection("LEFT");
        });
        onKey(KeyCode.UP, () -> {
            setInputBufferDirection("UP");
        });
        onKey(KeyCode.DOWN, () -> {
            setInputBufferDirection("DOWN");
        });
    }

    @Override
    protected void initUI() {
        Lvl.viewMap();
        Debug.initFpsUI();
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("fps", 0);
    }

    @Override
    protected void onUpdate(double tpf) {
        Debug.updateFps(tpf);

        

        if (Dot.getConsumed() >= 70 && !bonus) {
            switch(Lvl.getStage()) {
                case 1 -> Fruit.placeFruit((byte) 4);
                case 2 -> Fruit.placeFruit((byte) 5);
                case 3 -> Fruit.placeFruit((byte) 6);
                case 4 -> Fruit.placeFruit((byte) 6);
                case 5 -> Fruit.placeFruit((byte) 7);
                case 6 -> Fruit.placeFruit((byte) 7);
                case 7 -> Fruit.placeFruit((byte) 8);
                case 8 -> Fruit.placeFruit((byte) 8);
                case 9 -> Fruit.placeFruit((byte) 9);
                case 10 -> Fruit.placeFruit((byte) 9);
                case 11 -> Fruit.placeFruit((byte) 10);
                case 12 -> Fruit.placeFruit((byte) 10);
                default -> Fruit.placeFruit((byte) 11);
            }
            bonus = true;
        }

        if (Dot.getConsumed() >= 170 && !bonus2) {
            switch(Lvl.getStage()) {
                case 1 -> Fruit.placeFruit((byte) 4);
                case 2 -> Fruit.placeFruit((byte) 5);
                case 3 -> Fruit.placeFruit((byte) 6);
                case 4 -> Fruit.placeFruit((byte) 6);
                case 5 -> Fruit.placeFruit((byte) 7);
                case 6 -> Fruit.placeFruit((byte) 7);
                case 7 -> Fruit.placeFruit((byte) 8);
                case 8 -> Fruit.placeFruit((byte) 8);
                case 9 -> Fruit.placeFruit((byte) 9);
                case 10 -> Fruit.placeFruit((byte) 9);
                case 11 -> Fruit.placeFruit((byte) 10);
                case 12 -> Fruit.placeFruit((byte) 10);
                default -> Fruit.placeFruit((byte) 11);
            }
            bonus2 = true;
        }

        if (Dot.getConsumed() == 244) {
            Lvl.win();
        }
    }

    public static void main(String[] args) {
        //deixa o jogo rodar no máximo a 360 fps
        System.setProperty("prism.vsync", "false");
        System.setProperty("javafx.animation.pulse", "360");
        launch(args);
    }
}