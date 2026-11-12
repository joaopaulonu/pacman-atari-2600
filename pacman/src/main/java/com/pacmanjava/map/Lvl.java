package com.pacmanjava.map;

import com.almasb.fxgl.texture.Texture;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.pacmanjava.consumables.Consumable;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.pacmanjava.utils.Utils.*;

public class Lvl {
    private static boolean win = false;
    private static int stage = 1;

    private static boolean stageFlash = true;

    public static byte[][] level = new byte[HEIGHT / 8][WIDTH / 8]; //matriz de todos os objetos no mapa (excluindo entidades e ui)
    
    //lé os valores de um documento de texto representando o mapa em bytes
    public static void createLevelMatrix(String directory) {
        File file = new File(("./src/main/resources/assets/levels/" + directory));
        String line;
        
        try (Scanner sc = new Scanner(file)) {
            for (int i = 0; i < HEIGHT / 8; i++) {
                line = sc.nextLine();

                for (int j = 0; j < WIDTH / 8; j++) {
                    level[i][j] = Byte.parseByte(String.valueOf(line.charAt(j)));
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        win = false;
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
    }
    
    public static void win() {
        if (!win) {
            win = true;
            stage++;
            
            runOnce(() -> {
                getGameTimer().runAtInterval(() -> {
                    if (stageFlash) {
                        getGameScene().addUINode(mapFlash);
                    } else {
                        getGameScene().removeUINode(mapFlash);
                    }
                    stageFlash = !stageFlash;    
                }, Duration.seconds(0.3));
            }, Duration.seconds(2));

            runOnce(() -> {
                Consumable.clearAll();
                getGameController().startNewGame();
            }, Duration.seconds(4));
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
}