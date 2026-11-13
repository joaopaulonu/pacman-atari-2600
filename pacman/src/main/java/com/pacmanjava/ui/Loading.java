package com.pacmanjava.ui;

import com.almasb.fxgl.app.scene.LoadingScene;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

import static com.pacmanjava.utils.Utils.*;

public class Loading extends LoadingScene {

    public Loading() {
        getContentRoot().getChildren().add(new Rectangle(WIDTH, HEIGHT, Color.BLACK));
    }
}
