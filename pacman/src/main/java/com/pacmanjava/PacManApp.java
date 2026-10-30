package com.pacmanjava;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.Map;
import com.pacmanjava.entidade.PacMan;
import com.almasb.fxgl.entity.Entity;

import com.pacmanjava.utils.Debug;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.pacmanjava.utils.Utils.*;

public class PacManApp extends GameApplication {
    private static PacMan pacman;
    private static Entity red, pink, orange, cyan;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
        settings.setTitle("Pac-Man");
        settings.setVersion("1.0");

        settings.setManualResizeEnabled(true);
        settings.setPreserveResizeRatio(true);
        settings.setFullScreenAllowed(true);
        Debug.showFps();
    }

    @Override
    protected void initGame() {
        //cor do background
        getGameScene().setBackgroundColor(Color.BLACK);
        //criação do player
        pacman = new PacMan();
        red = entityBuilder()
            .at((WIDTH / 2) + 20, (HEIGHT / 2) + 12)
            .with(new GhostSprite(Color.RED))
            .buildAndAttach();
        pink = entityBuilder()
            .at((WIDTH / 2) + 4, (HEIGHT / 2) + 12)
            .with(new GhostSprite(Color.PINK))
            .buildAndAttach();
        orange = entityBuilder()
            .at((WIDTH / 2) - 12, (HEIGHT / 2) + 12)
            .with(new GhostSprite(Color.ORANGE))
            .buildAndAttach();
        cyan = entityBuilder()
            .at((WIDTH / 2) - 28, (HEIGHT / 2) + 12)
            .with(new GhostSprite(Color.CYAN))
            .buildAndAttach();

        Debug.viewLevelTiles();
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
        //cria um quadro preto ao redor do tabuleiro (para esconder o pacman se ele passa por uma fronteira)
        Rectangle rect1 = new Rectangle(0, 0, WIDTH, HEIGHT);
        rect1.setFill(Color.TRANSPARENT);
        rect1.setStroke(Color.BLACK);
        rect1.setStrokeWidth(16);
        getGameScene().addUINode(rect1);

        Debug.initFpsUI();
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("fps", 0);
    }

    @Override
    protected void onUpdate(double tpf) {
        pacman.handleMovement(tpf);

        Debug.updateFps(tpf);
        Debug.cycleGhostAnimationChannels(red.getComponent(GhostSprite.class), 10, tpf);
        Debug.cycleGhostAnimationChannels(pink.getComponent(GhostSprite.class), 10, tpf);
        Debug.cycleGhostAnimationChannels(orange.getComponent(GhostSprite.class), 10, tpf);
        Debug.cycleGhostAnimationChannels(cyan.getComponent(GhostSprite.class), 10, tpf);
    }

    public static void main(String[] args) {
        //deixa o jogo rodar na quantidade máxima de fps
        System.setProperty("prism.vsync", "false");
        System.setProperty("javafx.animation.fullspeed", "true");
        launch(args);
    }
}