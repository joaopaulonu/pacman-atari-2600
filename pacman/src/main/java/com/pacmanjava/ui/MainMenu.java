package com.pacmanjava.ui;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.core.util.EmptyRunnable;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.pacmanjava.utils.Utils.*;

public class MainMenu extends FXGLMenu {
    private Animation<?> animation;

    private Texture btnJogar;
    private double count = 0;

    public MainMenu() {
        super(MenuType.MAIN_MENU);

        var bg = new javafx.scene.shape.Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight(), javafx.scene.paint.Color.BLACK);

        var background = createSprite("menuimage.png");

        background.setTranslateY(75);

        background.setFitHeight(background.getFitHeight()-5);
        background.setFitWidth(background.getFitWidth()-30);

        btnJogar = createSprite("pressimage.png");

        btnJogar.setTranslateX(60);
        btnJogar.setTranslateY(180);

        btnJogar.setOnMouseClicked(event -> fireNewGame());
        //getContentRoot().setOnKeyPressed(event -> fireNewGame());

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
