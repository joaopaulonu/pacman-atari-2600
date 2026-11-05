package com.pacmanjava.consumable;

import com.pacmanjava.entitidade.EntidadeJogo;
import com.pacmanjava.map.Lvl;

public class Consumable {
    private byte id;
    private int posX, posY;

    public void canConsume(EntidadeJogo entity) {
        if (entity.getTile() == id) {
            return true;
        }
        return false;
    }

    public 
}
