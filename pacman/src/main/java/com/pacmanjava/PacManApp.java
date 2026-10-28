package com.pacmanjava;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import javafx.scene.text.Text;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import java.util.Map;
import com.pacmanjava.entidade.PacMan;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.pacmanjava.utils.Utils.*;

//arquivo principal
public class PacManApp extends GameApplication {
    private static PacMan pacman;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(WIDTH);
        settings.setHeight(HEIGHT);
        settings.setTitle("Pac-Man");
        settings.setVersion("1.0");

        settings.setManualResizeEnabled(true);
        settings.setPreserveResizeRatio(true);
        settings.setFullScreenAllowed(true);
    }

    @Override
    protected void initGame() {
        //cor do background
        getGameScene().setBackgroundColor(Color.BLACK);
        
        //visualização dos tiles (só para fazer testes de posição dos entities)
        entityBuilder()
            .at(8, 8)
            .view(createSprite("debug tiles.png"))
            .with(new IrremovableComponent())
            .zIndex(-100)
            .buildAndAttach();

        //criação do player
        pacman = new PacMan(
            entityBuilder()
            .at((WIDTH / 2) - 4, (HEIGHT / 2) - 4)
            .with(new PacmanSprite())
            .buildAndAttach()
        );
    }

    @Override
    protected void initInput() {
        initMovement();
    }

    @Override
    protected void initUI() {
        //cria um quadro preto ao redor do tabuleiro (para esconder o pacman se ele passa por uma fronteira)
        Rectangle rect1 = new Rectangle(0, 0, WIDTH, HEIGHT);
        rect1.setFill(Color.TRANSPARENT);
        rect1.setStroke(Color.BLACK);
        rect1.setStrokeWidth(16);
        getGameScene().addUINode(rect1);

        //visualizador de fps (só para testar o desempenho do jogo)
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

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("fps", 0);
    }

    private static double frames = 1;

    @Override
    protected void onUpdate(double tpf) {
        pacman.handleMovement(tpf);

        //atualiza os fps
        frames += tpf;
        if ((int) frames % 2 == 0) {
            set("fps", (int)(1 / tpf));
            frames = 1;
        }
    }

    public static void main(String[] args) {
        //deixa o jogo rodar na quantidade máxima de fps
        System.setProperty("prism.vsync", "false");
        System.setProperty("javafx.animation.fullspeed", "true");
        launch(args);
    }
}