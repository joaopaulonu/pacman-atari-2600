package com.pacmanjava.consumables;

import com.almasb.fxgl.texture.Texture;
import com.pacmanjava.map.Lvl;
import com.pacmanjava.PacManApp;
import javafx.geometry.Rectangle2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.pacmanjava.utils.Utils.*;

public class Dot extends Consumable {
    public static final int TOTAL = 244;

    private static int consumed = 0;
    private static boolean flip = true;
    private boolean hide = false;

    public void consume() {
        super.consume();

        /*
        if (this.id == 4) {
            onEnergizerEaten();
        }
        */

        if (flip) {
            play("eat_dot_0.wav");
        } else {
            play("eat_dot_1.wav");
        }
        flip = !flip;
        consumed++;
    }

    public static int getConsumed() {
        return consumed;
    }
    public static void setConsumed(int c) {
        consumed = c;
    }

    //construtor (private porque só o "placeConsumables()" deveria chamar ele)
    private Dot(byte id, int posX, int posY) {
        super(id, posX, posY);

        Texture texture = createSprite("dots.png");
        Texture dots;
        
        if (id == 3) {
            dots = texture.subTexture(new Rectangle2D(0, 0, 100, 100));
        } else {
            dots = texture.subTexture(new Rectangle2D(100, 0, 100, 100));
            getGameTimer().runAtInterval(() -> {
                if (PacManApp.getPacMan() != null && PacManApp.getPacMan().hasMovedAfterSpawn()) {
                    hide = !hide;
                    if (hide) {
                        dots.setOpacity(0.0);
                    } else {
                        dots.setOpacity(1.0);
                    }
                } else { dots.setOpacity(1.0); }
            }, Duration.seconds(0.15));
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

    //instancia todos os consumiveis no mapa
    public static void placeDots() {
        for (int i = 0; i < HEIGHT / 8; i++) {
            for (int j = 0; j < WIDTH / 8; j++) {
                switch (Lvl.level[i][j]) {
                    case 3 -> consumables.add(new Dot((byte) 3, j, i));
                    case 4 -> consumables.add(new Dot((byte) 4, j, i));
                }
            }
        }
    }
}