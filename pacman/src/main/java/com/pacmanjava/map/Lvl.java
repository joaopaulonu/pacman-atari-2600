package com.pacmanjava.map;

import com.almasb.fxgl.texture.Texture;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.pacmanjava.utils.Utils.*;

public class Lvl {

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
    }

    //inicializa a imagem do mapa
    public static void viewMap() {
            Texture map = createSprite("maporiginal.png");

            //cria um quadro preto ao redor do tabuleiro (para esconder o pacman se ele passa por uma fronteira)
            Rectangle rect = new Rectangle(0, 0, WIDTH, HEIGHT);
            rect.setFill(Color.TRANSPARENT);
            rect.setStroke(Color.BLACK);
            rect.setStrokeWidth(16);

            getGameScene().setBackgroundColor(Color.BLACK);
            getGameScene().addUINode(map);
            getGameScene().addUINode(rect);
    }
}