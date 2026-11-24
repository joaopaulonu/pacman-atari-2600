package com.pacmanjava.consumables;

import com.almasb.fxgl.texture.Texture;
import com.pacmanjava.map.Lvl;
import com.pacmanjava.score.Pontuacao;

import javafx.geometry.Rectangle2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.pacmanjava.utils.Utils.*;

public class Fruit extends Consumable {
    public void consume() {
        super.consume();
        
        switch (id) {
            case 5 -> Pontuacao.comerFruta(100);
            case 6 -> Pontuacao.comerFruta(300);
            case 7 -> Pontuacao.comerFruta(500);
            case 8 -> Pontuacao.comerFruta(700);
            case 9 -> Pontuacao.comerFruta(1000);
            case 10 -> Pontuacao.comerFruta(2000);
            case 11 -> Pontuacao.comerFruta(3000);
            case 12 -> Pontuacao.comerFruta(5000);
        }

        Lvl.level[21][14] = 0;
        play("eat_fruit.wav");
    }

    //construtor (private porque só o "placeConsumables()" deveria chamar ele)
    private Fruit(byte id, int posX, int posY) {
        super(id, posX, posY);

        Texture texture = createSprite("fruits.png");
        Texture fruits;
        
        fruits = texture.subTexture(new Rectangle2D(0 + (160 * (id - 5)), 0, 160, 160));

        fruits.setFitHeight(16);
        fruits.setFitWidth(16);
        fruits.setTranslateX(-8);
        fruits.setTranslateY(-8);

        entity = entityBuilder()
            .at((posX * 8), (posY * 8) + 4)
            .view(fruits)
            .zIndex(-100)
            .buildAndAttach();
        
        Lvl.level[21][15] = id;
        Lvl.level[21][14] = id;
    }

    //instancia todos os consumiveis no mapa
    public static void placeFruit(byte id) {
        Fruit fruit = new Fruit(id, 15, 21);
        consumables.add(fruit);
        runOnce(() -> {
            if (getConsumable(15, 21) != null) {
                consumables.remove(fruit);;
                Lvl.level[21][15] = 0;
                Lvl.level[21][14] = 0;
                fruit.entity.removeFromWorld();
            }
        }, Duration.seconds(8));
    }
}