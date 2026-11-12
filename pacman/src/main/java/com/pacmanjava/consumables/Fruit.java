package com.pacmanjava.consumables;

import com.almasb.fxgl.texture.Texture;
import com.pacmanjava.map.Lvl;
import javafx.geometry.Rectangle2D;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.pacmanjava.utils.Utils.*;

public class Fruit extends Consumable {
    public void consume() {
        super.consume();
        Lvl.level[21][14] = 0;
        play("eat_fruit.wav");
    }

    //construtor (private porque só o "placeConsumables()" deveria chamar ele)
    private Fruit(byte id, int posX, int posY) {
        super(id, posX, posY);

        Texture texture = createSprite("fruits.png");
        Texture fruits;
        
        fruits = texture.subTexture(new Rectangle2D(0 + (160 * (id - 4)), 0, 160, 160));

        fruits.setFitHeight(16);
        fruits.setFitWidth(16);
        fruits.setTranslateX(-8);
        fruits.setTranslateY(-8);

        entity = entityBuilder()
            .at((posX * 8), (posY * 8) + 4)
            .view(fruits)
            .buildAndAttach();
        
        Lvl.level[21][15] = id;
        Lvl.level[21][14] = id;
    }

    //instancia todos os consumiveis no mapa
    public static void placeFruit(byte id) {
        consumables.add(new Fruit(id, 15, 21));
    }
}