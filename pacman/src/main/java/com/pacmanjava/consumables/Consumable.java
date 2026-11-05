package com.pacmanjava.consumables;

import com.pacmanjava.entidades.EntidadeJogo;
import com.pacmanjava.map.Lvl;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Rectangle2D;
import java.util.LinkedList;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.pacmanjava.utils.Utils.*;

public class Consumable {
    private byte id;
    private int posX, posY;
    private Entity entity;
    private static LinkedList<Consumable> consumables = new LinkedList<Consumable>();
    private static boolean flip = true;

    public void consume() {
        if (flip) {
            play("eat_dot_0.wav");
        } else {
            play("eat_dot_1.wav");
        }

        Lvl.level[posY][posX] = 0;
        consumables.remove(this);
        entity.removeFromWorld();
        flip = !flip;
    }

    public static boolean isConsumable(byte id) {
        if (id == 3 || id == 4) {
            return true;
        }

        return false;
    }

    public static Consumable getConsumable(int posX, int posY) {
        for (Consumable c : consumables) {
            if (c.posX == posX && c.posY == posY) {
                return c;
            }
        }

        return null;
    }

    public Consumable(byte id, int posX, int posY) {
        Texture texture = createSprite("dots.png");
        Texture dots;
        
        if (id == 3) {
            dots = texture.subTexture(new Rectangle2D(0, 0, 100, 100));
        } else {
            dots = texture.subTexture(new Rectangle2D(100, 0, 100, 100));
        }

        dots.setFitHeight(10);
        dots.setFitWidth(10);
        dots.setTranslateX(-5);
        dots.setTranslateY(-5);

        entity = entityBuilder()
            .at((posX * 8) + 4, (posY * 8) + 4)
            .view(dots)
            .buildAndAttach();

        this.id = id;
        this.posX = posX;
        this.posY = posY;
    }

    public static void placeConsumables() {
        for (int i = 0; i < HEIGHT / 8; i++) {
            for (int j = 0; j < WIDTH / 8; j++) {
                switch (Lvl.level[i][j]) {
                    case 3 -> consumables.add(new Consumable((byte) 3, j, i));
                    case 4 -> consumables.add(new Consumable((byte) 4, j, i));
                }
            }
        }
    }
}
