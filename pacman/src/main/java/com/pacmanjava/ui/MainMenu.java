package com.pacmanjava.ui;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.input.TriggerListener;
import com.almasb.fxgl.input.Trigger;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

import static com.pacmanjava.utils.Utils.*;

public class MainMenu extends FXGLMenu {
    private Texture btnJogar, background;
    private Rectangle bg;
    private double count = 0;

    public MainMenu() {
        super(MenuType.MAIN_MENU);

        bg = new Rectangle(WIDTH, HEIGHT, Color.BLACK);
        background = createSprite("ui/menuimage.png");

        background.setTranslateY(75);
        background.setFitHeight(background.getFitHeight() - 5);
        background.setFitWidth(background.getFitWidth() - 30);

        btnJogar = createSprite("ui/pressimage.png");

        btnJogar.setTranslateX((WIDTH - btnJogar.getFitWidth()) / 2);
        btnJogar.setTranslateY(180);

        getInput().addTriggerListener(new TriggerListener() {
            @Override
            protected void onActionBegin(Trigger trigger) {
                fireNewGame();
            }
        });

        getContentRoot().getChildren().addAll(bg, background, btnJogar);
    }

    @Override
    protected void onUpdate(double tpf) {
        count = (count + tpf) % 2;

        if (count <= 1) {
            btnJogar.setOpacity(count);
        } else {
            btnJogar.setOpacity(btnJogar.getOpacity() - tpf);
        }

    }
}
