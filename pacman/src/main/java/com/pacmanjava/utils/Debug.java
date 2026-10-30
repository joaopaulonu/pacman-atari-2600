package com.pacmanjava.utils;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Debug extends Utils {
    private static boolean showFps = false;
    private static double frames = 0;

    //visualização dos tiles (para fazer testes de posição dos entities)
    public static void viewLevelTiles() {
        entityBuilder()
            .at(8, 8)
            .view(createSprite("debug tiles.png"))
            .with(new IrremovableComponent())
            .zIndex(-100)
            .buildAndAttach();
    }

    //visualizador de fps (para testar o desempenho do jogo)
    public static void showFps() {
        showFps = true;
    }
    public static void initFpsUI() {
        if (showFps) {
            Text text = new Text("FPS ");
            text.setTranslateX(0);
            text.setTranslateY(10);
            text.setFill(Color.WHITE);
            getGameScene().addUINode(text);

            Text textFPS = new Text();
            textFPS.setTranslateX(25);
            textFPS.setTranslateY(10);
            textFPS.setFill(Color.WHITE);
            textFPS.textProperty().bind(getWorldProperties().intProperty("fps").asString());
            getGameScene().addUINode(textFPS);
        }
    }
    public static void updateFps(double tpf) {
        if (showFps) {
            //atualiza os fps
            frames += tpf;
            if ((int) frames % 2 == 0) {
                set("fps", (int)(1 / tpf));
                frames = 1;
            }
        }
    }

    //mostra todas as animações do fantasma
    private static double debugtimer = 1;
    private static int channel = 0;
    public static void cycleGhostAnimationChannels(GhostSprite sprite, int interval, double tpf) {
        debugtimer += tpf;
        if ((int) debugtimer % interval == 0) {
            channel = (channel + 1) % 11;
            debugtimer = 1;
        }
        switch(channel) {
            case 0:
                sprite.alive();
                sprite.moveRight();
                break;
            case 1:
                sprite.moveLeft();
                break;
            case 2:
                sprite.moveUp();
                break;
            case 3:
                sprite.moveDown();
                break;
            case 4:
                sprite.scared1();
                break;
            case 5:
                sprite.scared2();
                break;
            case 6:
                sprite.scatter();
                break;
            case 7:
                sprite.dead();
                sprite.moveRight();
                break;
            case 8:
                sprite.moveLeft();
                break;
            case 9:
                sprite.moveUp();
                break;
            case 10:
                sprite.moveDown();
                break;
        }
    }
}