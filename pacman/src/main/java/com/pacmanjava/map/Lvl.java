package com.pacmanjava.map;

import java.util.Scanner;

import com.almasb.fxgl.entity.components.IrremovableComponent;

import java.io.File;
import java.io.FileNotFoundException;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.pacmanjava.utils.Utils.*;

public class Lvl {

    public static byte[][] level = new byte[HEIGHT / 8][WIDTH / 8];
    
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

    public static void viewMap() {
        entityBuilder()
            .at(0, 0)
            .view(createSprite("maporiginal.png"))
            .with(new IrremovableComponent())
            .zIndex(-10)
            .buildAndAttach();
    }
}