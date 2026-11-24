package com.pacmanjava;

import com.almasb.fxgl.app.CursorInfo;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.pacmanjava.consumables.Dot;
import com.pacmanjava.consumables.Fruit;
import com.pacmanjava.entidades.Ghost;
import com.pacmanjava.entidades.PacMan;
import com.pacmanjava.map.Lvl;
import com.pacmanjava.score.Pontuacao;
import com.pacmanjava.ui.MainMenu;
import com.pacmanjava.ui.ResumeGame;
import com.pacmanjava.ui.Loading;
import com.almasb.fxgl.app.scene.LoadingScene;
import com.pacmanjava.utils.Debug;
import com.pacmanjava.utils.Utils.Direction;

import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import com.almasb.fxgl.texture.Texture;

import com.pacmanjava.entidades.fantasmas.Blinky;
import com.pacmanjava.entidades.fantasmas.Pinky;
import com.pacmanjava.entidades.fantasmas.Inky;
import com.pacmanjava.entidades.fantasmas.Clyde;
import com.pacmanjava.entidades.fantasmas.GridMap;
import com.pacmanjava.entidades.fantasmas.PacmanGameOrchestrator;
import com.pacmanjava.entidades.fantasmas.LevelSettings;
import com.pacmanjava.entidades.fantasmas.Point;

import java.util.Map;
import java.util.random.RandomGenerator;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.pacmanjava.utils.Utils.*;

public class PacManApp extends GameApplication {
    public static boolean newGame = true;

    private static PacMan pacman;
    private static Blinky blinky;
    private static Pinky pinky;
    private static Inky inky;
    private static Clyde clyde;

    private static PacmanGameOrchestrator gameOrchestrator;
    private static byte fruit = 4;
    private static boolean bonus = false;
    private static boolean bonus2 = false;
    private boolean winGhostSequenceStarted;

    private Texture ready, gameOver;

    public static PacMan getPacMan() {
        return pacman;
    }

    public static PacmanGameOrchestrator getOrchestrator() {
        return gameOrchestrator;
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
        settings.setFontUI("pacmanFont.ttf");
        settings.setFontGame("pacmanFont.ttf");
        settings.setFontMono("pacmanFont.ttf");
        settings.setFontText("pacmanFont.ttf");
        settings.setTitle("Pac-Man");
        settings.setVersion("1.0");
        settings.setManualResizeEnabled(true);
        settings.setPreserveResizeRatio(true);
        settings.setFullScreenFromStart(true);
        settings.setFullScreenAllowed(true);
        settings.setMainMenuEnabled(true);
        settings.setGameMenuEnabled(true);
        settings.setAppIcon("pacmangame.png");
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
        winGhostSequenceStarted = false;

        if (newGame) {
            Lvl.createLevelMatrix("tilemap.txt");
            bonus = false;
            bonus2 = false;
        }

        Dot.placeDots();
        pacman = new PacMan();
        blinky = new Blinky(new GridMap(), new Point(13, 12), Direction.LEFT, RandomGenerator.getDefault());
        pinky = new Pinky(new GridMap(), new Point(13, 17), Direction.DOWN, RandomGenerator.getDefault());
        inky = new Inky(new GridMap(), blinky, new Point(15, 17), Direction.DOWN, RandomGenerator.getDefault());
        clyde = new Clyde(new GridMap(), new Point(13, 18), Direction.UP, RandomGenerator.getDefault());

        gameOrchestrator = new PacmanGameOrchestrator(LevelSettings.forLevel(Lvl.getStage()), blinky, pinky, inky, clyde);
        pinky.beginHouseBounce(new Point(13, 17));
        inky.beginHouseBounce(new Point(15, 17));
        clyde.beginHouseBounce(new Point(13, 18));
        blinky.spawnAt(120.0, 124.0);
        pinky.spawnAt(120.0, 148.0);
        inky.spawnAt(105.0, 148.0);
        clyde.spawnAt(135.0, 148.0);
        
        ready = createSprite("ui/Ready.png");
        ready.setTranslateY(168);
        ready.setTranslateX((WIDTH - ready.getFitWidth()) / 2);
        getGameScene().addUINode(ready);
        
        //intro (musica e pausa o jogador)
        pacman.pause();
        blinky.pause();
        pinky.pause();
        inky.pause();
        clyde.pause();
        setCanMove(false);
        setInputBufferDirection(Direction.NONE);
        if (Lvl.getStage() == 1 && PacMan.getLives() == PacMan.STARTING_LIVES) {
            pacman.setInvisibleFor(Duration.seconds(1.5));
            blinky.setInvisibleFor(Duration.seconds(1.5));
            pinky.setInvisibleFor(Duration.seconds(1.5));
            inky.setInvisibleFor(Duration.seconds(1.5));
            clyde.setInvisibleFor(Duration.seconds(1.5));
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
                setInputBufferDirection(Direction.RIGHT);
            }
        });
        onKey(KeyCode.LEFT, () -> {
            if (canMove()) {
                setInputBufferDirection(Direction.LEFT);
            }
        });
        onKey(KeyCode.UP, () -> {
            if (canMove()) {
                setInputBufferDirection(Direction.UP);
            }
        });
        onKey(KeyCode.DOWN, () -> {
            if (canMove()) {
                setInputBufferDirection(Direction.DOWN);
            }
        });
    }

    @Override
    protected void initUI() {
        Lvl.viewMap();
        Pontuacao.initScoreBoard();
        Debug.initFpsUI();
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("fps", 0);
        vars.put("score", 0);
    }

    @Override
    protected void onUpdate(double tpf) {
        Debug.updateFps(tpf);
        Pontuacao.updateScore();

        gameOrchestrator.update(tpf, getPacMan().getPosition(), getPacMan().getCurrentDirection(), Dot.TOTAL - Dot.getConsumed());

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

        if (Lvl.getWin() && !winGhostSequenceStarted) {
            winGhostSequenceStarted = true;
            blinky.pause();
            pinky.pause();
            inky.pause();
            clyde.pause();

            runOnce(() -> {
                blinky.setDead();
                pinky.setDead();
                inky.setDead();
                clyde.setDead();
            }, Duration.seconds(2));
        }

        if (pacman.isDying() && !blinky.isDead() && !inky.isDead() && !pinky.isDead() && !clyde.isDead()) {
            runOnce(() -> {
                blinky.setDead();
                pinky.setDead();
                inky.setDead();
                clyde.setDead();
            }, Duration.seconds(2));
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