package com.pacmanjava.ui;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.core.util.EmptyRunnable;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.pacmanjava.utils.Utils.*;

public class MainMenu extends FXGLMenu {
    private Animation<?> animation;

    public MainMenu() {
        super(MenuType.MAIN_MENU);

        var bg = new javafx.scene.shape.Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight(), javafx.scene.paint.Color.BLACK);

        var background = createSprite("menuimage.png");

        background.setFitHeight(background.getFitHeight()-5);
        background.setFitWidth(background.getFitWidth()-5);

        var btnJogar = FXGL.texture("pressimage.png");

        btnJogar.setTranslateX(20);
        btnJogar.setTranslateY(120);

        btnJogar.setOnMouseClicked(event -> fireNewGame());

        getContentRoot().getChildren().addAll(bg, background, btnJogar);

        animation = FXGL.animationBuilder()
        .duration(Duration.seconds(2))
        .fade(btnJogar)       
        .from(0.0)            
        .to(1.0)              
        .build();
    }

    @Override
    public void onCreate() {
        animation.setOnFinished(EmptyRunnable.INSTANCE);
        animation.stop();
        animation.start();
    }

    @Override
    protected void onUpdate(double tpf) {
        animation.onUpdate(tpf);
    }
}
