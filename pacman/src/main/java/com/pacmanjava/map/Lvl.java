package com.pacmanjava.map;

import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.TimerAction;

import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.pacmanjava.PacManApp;
import com.pacmanjava.consumables.Consumable;
import com.pacmanjava.entidades.PacMan;
import com.pacmanjava.score.Pontuacao;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.pacmanjava.utils.Utils.*;

public class Lvl {
    private static boolean oneup = false;
    private static boolean win = false;
    private static boolean reset = false;
    private static int stage = 1;

    private static boolean stageFlash = true;
    private static TimerAction action;

    private static final byte[][] currentLevel = {{0, 0, 0, 0, 0, 0, 5}, {0, 0, 0, 0, 0, 0, 5}, {0, 0, 0, 0, 0, 6, 5}, {0, 0, 0, 0, 7, 6, 5}, {0, 0, 0, 7, 7, 6, 5}, {0, 0, 8, 7, 7, 6, 5}, {0, 8, 8, 7, 7, 6, 5}, {9, 8, 8, 7, 7, 6, 5}, {9, 9, 8, 8, 7, 7, 6}, {10, 9, 9, 8, 8, 7, 7}, {10, 10, 9, 9, 8, 8, 7}, {11, 10, 10, 9, 9, 8, 8}, {11, 11, 10, 10, 9, 9, 8}, {12, 11, 11, 10, 10, 9, 9}, {12, 12, 11, 11, 10, 10, 9}, {12, 12, 12, 11, 11, 10, 10}, {12, 12, 12, 12, 11, 11, 10}, {12, 12, 12, 12, 12, 11, 11}, {12, 12, 12, 12, 12, 12, 11}, {12, 12, 12, 12, 12, 12, 12}};

    public static byte[][] level = new byte[HEIGHT / 8][WIDTH / 8]; //matriz de todos os objetos no mapa (excluindo entidades e ui)
    
    //lé os valores de um documento de texto representando o mapa em bytes
    public static void createLevelMatrix(String directory) {
        File file = new File(("./src/main/resources/assets/levels/" + directory));
        String line;
        
        try (Scanner sc = new Scanner(file)) {
            for (int i = 0; i < HEIGHT / 8; i++) {
                line = sc.nextLine();

                for (int j = 0; j < WIDTH / 8; j++) {
                    level[i][j] = Byte.parseByte(String.valueOf(line.charAt(j)), 16);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        win = false;
        reset = false;
    }

    //inicializa a imagem do mapa
    private static Texture mapFlash;
    public static void viewMap() {
        Texture map = createSprite("maporiginal.png");
        mapFlash = map.replaceColor(Color.rgb(0, 26, 255), Color.WHITE);
        mapFlash.setFitHeight(mapFlash.getHeight() / 10);
        mapFlash.setFitWidth(mapFlash.getWidth() / 10);
        
        //cria um quadro preto ao redor do tabuleiro (para esconder o pacman se ele passa por uma fronteira)
        Rectangle rect = new Rectangle(0, 0, WIDTH, HEIGHT);
        rect.setFill(Color.TRANSPARENT);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(16);
        
        getGameScene().setBackgroundColor(Color.BLACK);
        
        getGameScene().addUINodes(map, rect);

        Texture pacmanSprite = createSprite("pacman.png");
        Texture[] pacmanLife = new Texture[7];
        for (int i = 0; i < PacMan.getLives() - 1; i++) { 
            pacmanLife[i] = pacmanSprite.subTexture(new Rectangle2D(140, 0, 140, 140));
            pacmanLife[i].setFitHeight(14);
            pacmanLife[i].setFitWidth(14);
            pacmanLife[i].setScaleX(-1);
            pacmanLife[i].setX(17 + ((i) * 16));
            pacmanLife[i].setY(284);
            getGameScene().addUINode(pacmanLife[i]);
        }

        Texture fruitSprite = createSprite("fruits.png");
        Texture[] fruit = new Texture[7];

        if (stage < 20) {
            for (int i = 0; i < 7; i++) { 
                if (currentLevel[stage][i] == 0) { continue; }
                fruit[i] = fruitSprite.subTexture(new Rectangle2D(0 + (160 * (currentLevel[stage][i] - 5)), 0, 160, 160));
                fruit[i].setFitHeight(16);
                fruit[i].setFitWidth(16);
                fruit[i].setX(113 + ((i) * 16));
                fruit[i].setY(284);
                getGameScene().addUINode(fruit[i]);
            }
        } else {
            for (int i = 0; i < 7; i++) { 
                fruit[i] = fruitSprite.subTexture(new Rectangle2D(1280, 0, 160, 160));
                fruit[i].setFitHeight(16);
                fruit[i].setFitWidth(16);
                fruit[i].setX(113 + ((i) * 16));
                fruit[i].setY(284);
                getGameScene().addUINode(fruit[i]);
            }
        }
        reset = false;
    }
    
    public static void win() {
        if (!win) {
            win = true;
            PacManApp.newGame = true;
            Pontuacao.resetarMultiplicadorFantasma();
            stage++;
            
            if (Pontuacao.getPontuacao() >= 10000 && !oneup) {
                PacMan.setLives(PacMan.getLives() + 1);
                oneup = true;
            }

            runOnce(() -> {
                action = getGameTimer().runAtInterval(() -> {
                    if (stageFlash) {
                        getGameScene().addUINode(mapFlash);
                    } else {
                        getGameScene().removeUINode(mapFlash);
                    }
                    stageFlash = !stageFlash;    
                }, Duration.seconds(0.3));
            }, Duration.seconds(2));

            runOnce(() -> {
                action.expire();
                Consumable.clearAll();
                getGameController().startNewGame();
            }, Duration.seconds(4));
        }
    }

    public static void restart() {
        if (!reset) {
            reset = true;
            PacManApp.newGame = false;
            Pontuacao.resetarMultiplicadorFantasma();

            runOnce(() -> {
                Consumable.clearConsumed();
                getGameController().startNewGame();
            }, Duration.seconds(1));
        }
    }

    public static boolean getWin() {
        return win;
    }

    public static int getStage() {
        return stage;
    }
    public static void setStage(int s) {
        stage = s;
    }

    public static void setOneup(boolean value) {
        oneup = value;
    }
}