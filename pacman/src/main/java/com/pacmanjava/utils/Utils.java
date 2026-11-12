package com.pacmanjava.utils;

import com.almasb.fxgl.animation.Animation;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.ImagesKt;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Utils {
    public static final int WIDTH = 240; //224 + 16
    public static final int HEIGHT = 304; //288 + 16

    private static final int UPSCALE_FACTOR = 10;
    private static String inputBufferDirection = ""; //guarda o input do jogador

    public static String getInputBufferDirection() { 
        return inputBufferDirection; 
    }
    public static void setInputBufferDirection(String direction) { 
        inputBufferDirection = direction; 
    }

    //cria uma textura sem Anti-Aliasing
    public static Texture createSprite(String local) {
        Image original = image(local);
        Image enlarged = ImagesKt.resize(original, (int)original.getWidth() * UPSCALE_FACTOR, (int)original.getHeight() * UPSCALE_FACTOR);

        Texture sprite = new Texture(enlarged);
        sprite.setFitWidth(original.getWidth());
        sprite.setFitHeight(original.getHeight());

        return sprite;
    }

    public static Texture createSprite(String local, double offsetX, double offsetY) {
        Texture sprite = createSprite(local);
        sprite.setTranslateX(offsetX);
        sprite.setTranslateY(offsetY);

        return sprite;
    }
    
    //Classe para as animações
    private static abstract class AnimationComponent extends Component {
        protected int frameCount;
        protected int frameSize;
        protected Image spriteSheet;
        protected int scaledFrameSize;

        protected AnimatedTexture texture;
        /*
            Acessa uma propriedade do AnimatedTexture que por alguma razão é private
            Ajuda a acessar os metodos de pausa e resume das animações
        */
        private Animation<Integer> animation() {
            return org.joor.Reflect
                .on(this.texture)
                .field("animation")
                .get();
        }
        public void setPaused() {
            animation().pause();
        }
        public void setResumed() {
            animation().resume();
        }

        //atualiza o tamanho do AnimatedTexture porque o createSprite() aumenta o tamanho
        //AnimatedTexture perde o tamanho original cada frame
        protected void updateTextureSize(AnimatedTexture t) {
            t.setFitWidth(frameSize);
            t.setFitHeight(frameSize);
            t.setTranslateX(-frameSize / 2);
            t.setTranslateY(-frameSize / 2);
        }

        public abstract void moveRight();
        public abstract void moveLeft();
        public abstract void moveUp();
        public abstract void moveDown();
    }

    //classe para as animações do pac-man
    public static class PacmanSprite extends AnimationComponent {
        private String dir = "";
        private boolean dying = false;
        private AnimationChannel animIdle, animEat, animDying;

        public PacmanSprite() {
            frameSize = 14;
            frameCount = 15;
            spriteSheet = createSprite("pacman.png").getImage();
            scaledFrameSize = frameSize * UPSCALE_FACTOR;

            animIdle = new AnimationChannel(spriteSheet, frameCount, scaledFrameSize, scaledFrameSize, Duration.seconds(1), 0, 0);
            animEat = new AnimationChannel(spriteSheet, frameCount, scaledFrameSize, scaledFrameSize, Duration.seconds(0.2), 0, 3);
            animDying = new AnimationChannel(spriteSheet, frameCount, scaledFrameSize, scaledFrameSize, Duration.seconds(2), 3, 14);

            texture = new AnimatedTexture(animIdle);
        }

        @Override
        public void onAdded() {
            entity.getViewComponent().addChild(texture);
        }

        @Override
        public void onUpdate(double tpf) {
            if (dying) {
                if (texture.getAnimationChannel() != animDying) {
                    texture.loopAnimationChannel(animDying);
                }
                updateTextureSize(texture);
                return;
            }

            if (!dir.equals("")) {
                if (texture.getAnimationChannel() != animEat) {
                    texture.loopAnimationChannel(animEat);
                }
            } else {
                if (texture.getAnimationChannel() != animIdle) {
                    texture.loopAnimationChannel(animIdle);
                }
            }

            updateTextureSize(texture);
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

        public void idle() {
            dir = "";
            getEntity().setRotation(0);
        }

        public void dying() {
            dying = true;
            getEntity().setRotation(-90);
        }

        public void dead() {
            dying = false;
            texture.setOpacity(0.0);
        }
    }

    //classe para as animações dos fantasmas
    public static class GhostSprite extends AnimationComponent {
        private String animState = "";
        private boolean dead = false;

        private AnimationChannel idleBody, animBody, animBodyScared1, animBodyScared2;
        private AnimationChannel animEyesX, animEyesUp, animEyesDown, animEyesScared1, animEyesScared2, animEyesScatter;

        private AnimatedTexture textureEyes;

        public GhostSprite(Color color) {
            frameSize = 16;
            frameCount = 15;
            spriteSheet = createSprite("ghost.png").replaceColor(Color.GREY, color).getImage();
            scaledFrameSize = frameSize * UPSCALE_FACTOR;
            
            idleBody = new AnimationChannel(spriteSheet, 2, scaledFrameSize, scaledFrameSize, Duration.seconds(1), 0, 0);
            animBody = new AnimationChannel(spriteSheet, 2, scaledFrameSize, scaledFrameSize, Duration.seconds(0.3), 0, 1);
            
            animEyesX = new AnimationChannel(spriteSheet, frameCount, scaledFrameSize, scaledFrameSize, Duration.seconds(1), 6, 6);
            animEyesUp = new AnimationChannel(spriteSheet, frameCount, scaledFrameSize, scaledFrameSize, Duration.seconds(1), 7, 7);
            animEyesDown = new AnimationChannel(spriteSheet, frameCount, scaledFrameSize, scaledFrameSize, Duration.seconds(1), 8, 8);

            animEyesScatter = new AnimationChannel(spriteSheet, frameCount, scaledFrameSize, scaledFrameSize, Duration.seconds(2), 9, 12);

            animEyesScared1 = new AnimationChannel(spriteSheet, frameCount, scaledFrameSize, scaledFrameSize, Duration.seconds(1), 13, 13);
            animBodyScared1 = new AnimationChannel(spriteSheet, frameCount, scaledFrameSize, scaledFrameSize, Duration.seconds(0.3), 2, 3);
            animEyesScared2 = new AnimationChannel(spriteSheet, frameCount, scaledFrameSize, scaledFrameSize, Duration.seconds(0.6), 13, 14);
            animBodyScared2 = new AnimationChannel(spriteSheet, frameCount, scaledFrameSize, scaledFrameSize, Duration.seconds(0.6), 2, 5);

            texture = new AnimatedTexture(idleBody);
            textureEyes = new AnimatedTexture(animEyesX);
        }

        @Override
        public void onAdded() {
            entity.getViewComponent().addChild(texture);
            entity.getViewComponent().addChild(textureEyes);
        }

        @Override
        public void onUpdate(double tpf) {

            if (!animState.equals("")) {
                if (texture.getAnimationChannel() == idleBody) {
                    texture.loopAnimationChannel(animBody);
                }
            }

            if (texture.getAnimationChannel() != idleBody) {
                switch(animState) {
                    case "RIGHT":
                    case "LEFT":
                        if (textureEyes.getAnimationChannel() != animEyesX) {
                            textureEyes.loopAnimationChannel(animEyesX);
                        }
                        if (texture.getAnimationChannel() != animBody) {
                            texture.loopAnimationChannel(animBody);
                        }
                        break;
                    case "UP":
                        if (textureEyes.getAnimationChannel() != animEyesUp) {
                            textureEyes.loopAnimationChannel(animEyesUp);
                        }
                        if (texture.getAnimationChannel() != animBody) {
                            texture.loopAnimationChannel(animBody);
                        }
                        break;
                    case "DOWN":
                        if (textureEyes.getAnimationChannel() != animEyesDown) {
                            textureEyes.loopAnimationChannel(animEyesDown);
                        }
                        if (texture.getAnimationChannel() != animBody) {
                            texture.loopAnimationChannel(animBody);
                        }
                        break;
                    case "SCATTER":
                        if (textureEyes.getAnimationChannel() != animEyesScatter) {
                            textureEyes.loopAnimationChannel(animEyesScatter);
                        }
                        if (texture.getAnimationChannel() != animBody) {
                            texture.loopAnimationChannel(animBody);
                        }
                        break;
                    case "SCARED1":
                        if (textureEyes.getAnimationChannel() != animEyesScared1) {
                            textureEyes.loopAnimationChannel(animEyesScared1);
                        }
                        if (texture.getAnimationChannel() != animBodyScared1) {
                            texture.loopAnimationChannel(animBodyScared1);
                        }
                        break;
                    case "SCARED2":
                        if (textureEyes.getAnimationChannel() != animEyesScared2) {
                            textureEyes.loopAnimationChannel(animEyesScared2);
                        }
                        if (texture.getAnimationChannel() != animBodyScared2) {
                            texture.loopAnimationChannel(animBodyScared2);
                        }
                        break;
                    default:
                        texture.loopAnimationChannel(idleBody);
                }
            }

            updateTextureSize(texture);
            updateTextureSize(textureEyes);
        }

        public void moveRight() {
            animState = "RIGHT";
            textureEyes.setScaleX(1);
            textureEyes.setScaleY(1);
        }

        public void moveLeft() {
            animState = "LEFT";
            textureEyes.setScaleX(-1);
            textureEyes.setScaleY(1);
        }

        public void moveUp() {
            animState = "UP";
            textureEyes.setScaleY(1);
            textureEyes.setScaleX(1);
        }

        public void moveDown() {
            animState = "DOWN";
            textureEyes.setScaleY(1);
            textureEyes.setScaleX(1);
        }

        public void scared1() {
            animState = "SCARED1";
            textureEyes.setScaleX(1);
            textureEyes.setScaleY(1);
        }

        public void scared2() {
            animState = "SCARED2";
            textureEyes.setScaleX(1);
            textureEyes.setScaleY(1);
        }

        public void scatter() {
            animState = "SCATTER";
            textureEyes.setScaleX(1);
            textureEyes.setScaleY(1);
        }

        public void dead() {
            dead = true;
            texture.setOpacity(0.0);
        }

        public void alive() {
            dead = false;
            texture.setOpacity(1.0);
        }
    }
}
