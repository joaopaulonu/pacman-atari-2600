package com.pacmanjava.utils;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.ImagesKt;
import com.almasb.fxgl.texture.Texture;
import javafx.util.Duration;

import javafx.scene.image.Image;

public class Utils {
    public static final int WIDTH = 240; //224 + 16
    public static final int HEIGHT = 304; //288 + 16
    public static final int SPRITE_SCALE = 10;

    public static Texture createSprite(String local) {
        Image original = FXGL.image(local);
        Image enlarged = ImagesKt.resize(original, (int)original.getWidth() * SPRITE_SCALE, (int)original.getHeight() * SPRITE_SCALE);

        Texture sprite = new Texture(enlarged);
        sprite.setFitWidth(original.getWidth());
        sprite.setFitHeight(original.getHeight());

        return sprite;
    }

    public static Texture createSprite(String local, double offsetX, double offsetY) {
        Image original = FXGL.image(local);
        Image enlarged = ImagesKt.resize(original, (int)original.getWidth() * SPRITE_SCALE, (int)original.getHeight() * SPRITE_SCALE);

        Texture sprite = new Texture(enlarged);
        sprite.setFitWidth(original.getWidth());
        sprite.setFitHeight(original.getHeight());
        sprite.setTranslateX(offsetX);
        sprite.setTranslateY(offsetY);

        return sprite;
    }
    
    private static abstract class AnimationComponent extends Component {
        protected int frameCount;
        protected int frameSize;

        protected AnimatedTexture texture;

        protected void updateTextureSize() {
            texture.setFitWidth(frameSize);
            texture.setFitHeight(frameSize);
            texture.setTranslateX(-frameSize / 2.0);
            texture.setTranslateY(-frameSize / 2.0);
        }

        public abstract void moveRight();
        public abstract void moveLeft();
        public abstract void moveUp();
        public abstract void moveDown();
    }

    public static class PacmanSprite extends AnimationComponent {

        private String dir = "";

        private AnimationChannel animIdle, animEat;

        public PacmanSprite() {
            frameSize = 14;
            frameCount = 4;
            Image spriteSheet = createSprite("pacman.png").getImage();
            int scaledFrameSize = frameSize * SPRITE_SCALE;

            animIdle = new AnimationChannel(spriteSheet, frameCount, scaledFrameSize, scaledFrameSize, Duration.seconds(0.3), 0, 0);
            animEat = new AnimationChannel(spriteSheet, frameCount, scaledFrameSize, scaledFrameSize, Duration.seconds(0.3), 0, 3);

            texture = new AnimatedTexture(animIdle);
        }

        @Override
        public void onAdded() {
            entity.getViewComponent().addChild(texture);
        }

        @Override
        public void onUpdate(double tpf) {
            if (!dir.equals("")) {
                if (texture.getAnimationChannel() == animIdle) {
                    texture.loopAnimationChannel(animEat);
                }
            }
            updateTextureSize();
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
}
