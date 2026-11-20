package com.pacmanjava;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.pacmanjava.consumables.Dot;
import com.pacmanjava.consumables.Fruit;
import com.pacmanjava.entidades.PacMan;
import com.pacmanjava.map.Lvl;
import com.pacmanjava.ui.MainMenu;
import com.pacmanjava.ui.ResumeGame;
import com.pacmanjava.ui.Loading;
import com.almasb.fxgl.app.scene.LoadingScene;
import com.pacmanjava.utils.Debug;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import com.almasb.fxgl.texture.Texture;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.pacmanjava.utils.Utils.*;

public class PacManApp extends GameApplication {
    public static boolean newGame = true;

    private static PacMan pacman;
    private static byte fruit = 4;
    private static boolean bonus = false;
    private static boolean bonus2 = false;

    private Texture ready, gameOver;

    public static PacMan getPacMan() {
        return pacman;
    }

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
            @Override
            public LoadingScene newLoadingScene() {
                return new Loading();
            }
        });
        Debug.showFps();
    }

    @Override
    protected void initGame() {
        if (newGame) {
            Lvl.createLevelMatrix("tilemap.txt");
            bonus = false;
            bonus2 = false;
        }

        Dot.placeDots();
        pacman = new PacMan();

        ready = createSprite("ui/Ready.png");
        ready.setTranslateY(168);
        ready.setTranslateX((WIDTH - ready.getFitWidth()) / 2);
        getGameScene().addUINode(ready);

        //intro (musica e pausa o jogador)
        pacman.pause();
        setCanMove(false);
        setInputBufferDirection("");
        if (Lvl.getStage() == 1 && PacMan.getLives() == 4) {
            play("start.wav");
            runOnce(() -> {
                setCanMove(true);
                pacman.resume();
            }, Duration.seconds(4));
        } else {
            runOnce(() -> {
                setCanMove(true);
                pacman.resume();
            }, Duration.seconds(2));
        }

        runOnce(() -> {pacman.setDead(true);}, Duration.seconds(20));

        switch(Lvl.getStage()) {
            case 1 -> fruit = (byte) 5;
            case 2 -> fruit = (byte) 6;
            case 3 -> fruit = (byte) 7;
            case 4 -> fruit = (byte) 7;
            case 5 -> fruit = (byte) 8;
            case 6 -> fruit = (byte) 8;
            case 7 -> fruit = (byte) 9;
            case 8 -> fruit = (byte) 9;
            case 9 -> fruit = (byte) 10;
            case 10 -> fruit = (byte) 10;
            case 11 -> fruit = (byte) 11;
            case 12 -> fruit = (byte) 11;
            default -> fruit = (byte) 12;
        }
    }

    //adiciona e cria as interações dos controles do jogo
    @Override
    protected void initInput() {
        onKey(KeyCode.RIGHT, () -> {
            if (canMove()) {
                setInputBufferDirection("RIGHT");
            }
        });
        onKey(KeyCode.LEFT, () -> {
            if (canMove()) {
                setInputBufferDirection("LEFT");
            }
        });
        onKey(KeyCode.UP, () -> {
            if (canMove()) {
                setInputBufferDirection("UP");
            }
        });
        onKey(KeyCode.DOWN, () -> {
            if (canMove()) {
                setInputBufferDirection("DOWN");
            }
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

        if (pacman.hasMovedAfterSpawn() && getGameScene().getUINodes().contains(ready)) {
            getGameScene().removeUINode(ready);
        }

        if (Dot.getConsumed() >= 70 && !bonus) {
            Fruit.placeFruit(fruit);
            bonus = true;
        }

        if (Dot.getConsumed() >= 170 && !bonus2) {
            Fruit.placeFruit(fruit);
            bonus2 = true;
        }

        if (Dot.getConsumed() == 244) {
            Lvl.win();
        }

        if (pacman.isDead()) {
            if (PacMan.getLives() > 0) {
                Lvl.restart();
            } else {
                if (!getGameScene().getUINodes().contains(gameOver)) {
                    gameOver = createSprite("ui/GameOver.png");
                    gameOver.setTranslateY(168);
                    gameOver.setTranslateX((WIDTH - gameOver.getFitWidth()) / 2);

                    getGameScene().addUINode(gameOver);
                }
            }
        }
    }

    public static void main(String[] args) {
        //deixa o jogo rodar no máximo a 360 fps
        System.setProperty("prism.vsync", "false");
        System.setProperty("javafx.animation.pulse", "360");
        launch(args);
    }
}