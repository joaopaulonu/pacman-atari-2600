package com.pacmanjava.consumables;

import com.almasb.fxgl.entity.Entity;
import com.pacmanjava.map.Lvl;

import java.util.LinkedList;

//classe para todos os objetos consumiveis (por agora só os dots)
public class Consumable {
    protected byte id; //tipo de consumivel
    protected int posX, posY; //em tiles
    protected Entity entity; //fxgl.entity associada ao Consumivel
    protected static LinkedList<Consumable> consumables = new LinkedList<Consumable>(); //lista que guarda os consumiveis no nivel

    //consome o consumivel
    public void consume() {
        Lvl.level[posY][posX] = 0;
        consumables.remove(this);
        entity.removeFromWorld();
    }

    //limpa a lista e os entities associados
    public static void clearAll() {
        for (Consumable c : consumables) {
            Lvl.level[c.posY][c.posX] = 0;
            if (c instanceof Fruit) {
                Lvl.level[c.posY][c.posX - 1] = 0;
            }
            c.entity.removeFromWorld();
        }
        Dot.setConsumed(0);

        consumables.clear();
    }

    //verifica se um byte representa um consumivel
    public static boolean isConsumable(byte id) {
        if (id >= 3 && id <= 12) {
            return true;
        }

        return false;
    }

    //pega o consumable na posição
    public static Consumable getConsumable(int posX, int posY) {
        for (Consumable c : consumables) {
            if (c.posX == posX && c.posY == posY) {
                return c;
            }
            if (isConsumable(Lvl.level[posY][posX]) && c.posX - 1 == posX && c.posY == posY) {
                return c;
            }
        }

        return null;
    }

    protected Consumable(byte id, int posX, int posY) {
        this.id = id;
        this.posX = posX;
        this.posY = posY;
    }
}
