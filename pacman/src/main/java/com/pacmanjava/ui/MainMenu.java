package com.pacmanjava.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.input.Trigger;
import com.almasb.fxgl.input.TriggerListener;
import com.almasb.fxgl.texture.Texture;
import com.pacmanjava.consumables.Consumable;
import com.pacmanjava.map.Lvl;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.pacmanjava.utils.Utils.*;

public class MainMenu extends FXGLMenu {
    private Texture pressStart, logo;
    private Rectangle background;
    private double count = 0;

    public MainMenu() {
        super(MenuType.MAIN_MENU);

        background = new Rectangle(WIDTH, HEIGHT, Color.BLACK);

        logo = createSprite("ui/menuimage.png");
        logo.setTranslateY(75);
        logo.setFitHeight(logo.getFitHeight() - 5);
        logo.setFitWidth(logo.getFitWidth() - 30);

        pressStart = createSprite("ui/pressimage.png");
        pressStart.setTranslateX((WIDTH - pressStart.getFitWidth()) / 2);
        pressStart.setTranslateY(180);

        //leia qualquer input
        getInput().addTriggerListener(new TriggerListener() {
            @Override
            protected void onActionBegin(Trigger trigger) {
                Consumable.clearAll(); //limpa os consumables da última sessão (se ainda existem)
                Lvl.setStage(1);
                fireNewGame();
            }
        });

        getContentRoot().getChildren().addAll(background, logo, pressStart);
    }

    @Override
    protected void onUpdate(double tpf) {
        count = (count + tpf) % 2;

        if (count <= 1) {
            pressStart.setOpacity(count);
        } else {
            pressStart.setOpacity(pressStart.getOpacity() - tpf);
        }

    }
}
