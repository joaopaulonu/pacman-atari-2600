package com.pacmanjava;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.core.util.EmptyRunnable;
import javafx.beans.binding.Bindings;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Duration;
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import java.util.Map;
import com.pacmanjava.entidade.PacMan;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.level.text.TextLevelLoader;
import com.almasb.fxgl.ui.FontType;
import com.almasb.fxgl.entity.level.Level;

import com.pacmanjava.ui.MainMenu;
import com.pacmanjava.map.Lvl;
//import com.pacmanjava.map.PacManEntityFactory;
import com.pacmanjava.utils.Debug;
import com.pacmanjava.utils.Utils.GhostSprite;

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
        settings.setMainMenuEnabled(true);
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newGameMenu() {
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
        //getGameWorld().addEntityFactory(new PacManEntityFactory());

       // Level level = getAssetLoader().loadLevel("tilemap.txt", new TextLevelLoader(8, 8, '0'));
        Lvl.createLevelMatrix("tilemap.txt");

        //getGameWorld().setLevel(level);

        //cor do background
        getGameScene().setBackgroundColor(Color.BLACK);
        //criação do player
        pacman = new PacMan();
        red = entityBuilder()
            .at((WIDTH / 2), (HEIGHT / 2) - 28)
            .with(new GhostSprite(Color.RED))
            .buildAndAttach();
        pink = entityBuilder()
            .at((WIDTH / 2), (HEIGHT / 2) - 4)
            .with(new GhostSprite(Color.PINK))
            .buildAndAttach();
        orange = entityBuilder()
            .at((WIDTH / 2) + 16, (HEIGHT / 2) - 4)
            .with(new GhostSprite(Color.ORANGE))
            .buildAndAttach();
        cyan = entityBuilder()
            .at((WIDTH / 2) - 16, (HEIGHT / 2) - 4)
            .with(new GhostSprite(Color.CYAN))
            .buildAndAttach();

        //Debug.viewLevelTiles();

        Lvl.viewMap();

        pacman.setDead(true);
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