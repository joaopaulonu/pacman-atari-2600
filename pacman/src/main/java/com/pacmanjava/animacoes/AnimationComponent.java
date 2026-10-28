package com.pacmanjava.animacoes;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.scene.image.Image;
import javafx.util.Duration;

import com.pacmanjava.PacManApp;

public class AnimationComponent extends Component {

    private static final int FRAME_COUNT = 4;
    private static final int FRAME_SIZE = 14;

    private String dir = "";

    private AnimatedTexture texture;
    private AnimationChannel animIdle, animEat;

    private void updateTextureSize() {
        texture.setFitWidth(FRAME_SIZE);
        texture.setFitHeight(FRAME_SIZE);
        texture.setTranslateX(-FRAME_SIZE / 2.0);
        texture.setTranslateY(-FRAME_SIZE / 2.0);
    }

    public AnimationComponent() {
        Image spriteSheet = PacManApp.createSprite("pacman.png").getImage();
        int scaledFrameSize = FRAME_SIZE * PacManApp.SPRITE_SCALE;

        animIdle = new AnimationChannel(spriteSheet, FRAME_COUNT, scaledFrameSize, scaledFrameSize, Duration.seconds(0.3), 0, 0);
        animEat = new AnimationChannel(spriteSheet, FRAME_COUNT, scaledFrameSize, scaledFrameSize, Duration.seconds(0.3), 0, 3);

        texture = new AnimatedTexture(animIdle);
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
    }

    @Override
    public void onUpdate(double tpf) {
        updateTextureSize();
        if (!dir.equals("")) {
            if (texture.getAnimationChannel() == animIdle) {
                texture.loopAnimationChannel(animEat);
                updateTextureSize();
            }
        }
    }

    public void moveRight() {
        dir = "RIGHT";

        getEntity().setRotation(0);
    }

    public void moveLeft() {
        dir = "LEFT";

        getEntity().setRotation(180);
    }

    public void moveUp() {
        dir = "UP";

        getEntity().setRotation(-90);
    }

    public void moveDown() {
        dir = "DOWN";

        getEntity().setRotation(90);
    }
}