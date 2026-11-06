package com.pacmanjava;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import javafx.scene.input.KeyCode;
import java.util.Map;
import com.pacmanjava.entidades.PacMan;
import com.pacmanjava.consumables.Consumable;
import com.almasb.fxgl.entity.Entity;
import com.pacmanjava.ui.MainMenu;
import com.pacmanjava.map.Lvl;
import com.pacmanjava.utils.Debug;
import com.pacmanjava.utils.Utils.GhostSprite;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.pacmanjava.utils.Utils.*;

public class PacManApp extends GameApplication {
    private static PacMan pacman;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
        settings.setTitle("Pac-Man");
        settings.setVersion("1.0");
        settings.setMainMenuEnabled(true);
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newMainMenu() {
                return new MainMenu();
            }
        });
        settings.setManualResizeEnabled(true);
        settings.setPreserveResizeRatio(true);
        settings.setFullScreenAllowed(true);
        Debug.showFps();
    }

    @Override
    protected void initGame() {
        Lvl.createLevelMatrix("tilemap.txt");
        Consumable.placeConsumables();

        pacman = new PacMan();

        //intro (musica e pausa o jogador)
        play("start.wav");
        pacman.pause();
        runOnce(() -> {
            setInputBufferDirection("");
            pacman.resume();
        }, Duration.seconds(4));
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
    }

    public static void main(String[] args) {
        //deixa o jogo rodar no máximo a 360 fps
        System.setProperty("prism.vsync", "false");
        System.setProperty("javafx.animation.pulse", "360");
        launch(args);
    }
}