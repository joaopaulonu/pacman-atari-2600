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

        background.setFitHeight(background.getFitHeight() - 20);
        background.setFitWidth(background.getFitWidth() - 20);

        background.setTranslateX(-20);
        background.setTranslateY(100);

        var btnJogar = new javafx.scene.control.Button("PRESS START");
        btnJogar.setStyle("-fx-background-color: black; -fx-text-fill: white; " +
                  "-fx-font-size: 24px; -fx-font-weight: bold; " +
                  "-fx-border-color: none; -fx-border-width: 3px; " +
                  "-fx-cursor: hand;");

        btnJogar.setTranslateX(-20);
        btnJogar.setTranslateY(150);

        btnJogar.setOnAction(event -> fireNewGame());

        getContentRoot().getChildren().addAll(bg, background, btnJogar);

        animation = FXGL.animationBuilder()
        .duration(Duration.seconds(1))
        .scale(btnJogar) // Exemplo: animando o botão
        .from(new Point2D(0, 0))
        .to(new Point2D(1, 1))
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
