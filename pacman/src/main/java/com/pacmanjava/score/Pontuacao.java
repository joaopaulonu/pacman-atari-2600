package com.pacmanjava.score;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.ui.FontFactory;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import com.pacmanjava.PacManApp;
import static com.pacmanjava.utils.Utils.*;
import static com.almasb.fxgl.dsl.FXGL.*;

public class Pontuacao {
    public static final FontFactory fontFactory = getAssetLoader().loadFont("pacmanFont.ttf");

    private static int pontuacaoAtual = 0;
    private static int multiplicadorFantasma = 200;

    public static void adicionar(int pontos) {
        pontuacaoAtual += pontos;
    }

    public static void comerFruta(int pontos) {
        adicionar(pontos);

        Text textPontuacao = getUIFactoryService().newText(String.valueOf(pontos), Color.WHITE, 8);

        Entity e = entityBuilder()
            .at((WIDTH / 2) - (textPontuacao.getLayoutBounds().getWidth() / 2), 176)
            .view(textPontuacao)
            .zIndex(-100)
            .buildAndAttach();

        runOnce(() -> {
            e.removeFromWorld();
        }, Duration.seconds(3));
    }

    public static int getPontuacao() {
        return pontuacaoAtual;
    }

    public static void resetar() {
        pontuacaoAtual = 0;
        resetarMultiplicadorFantasma();
    }

    // Chamado quando o jogador come uma nova pastilha para resetar a sequência
    public static void resetarMultiplicadorFantasma() {
        multiplicadorFantasma = 200;
    }

    // Dobra os pontos do fantasma a cada vez que um é comido
    public static int comerFantasma() {
        int pontosDoFantasma = multiplicadorFantasma;
        adicionar(pontosDoFantasma);
        
        if (multiplicadorFantasma < 1600) {
            multiplicadorFantasma *= 2;
        }

        Text textPontuacao = getUIFactoryService().newText(String.valueOf(pontosDoFantasma), Color.CYAN, 8);
        textPontuacao.setX(PacManApp.getPacMan().getPosX() - (textPontuacao.getLayoutBounds().getWidth() / 2));
        textPontuacao.setY(PacManApp.getPacMan().getPosY() + 4);

        getGameScene().addUINode(textPontuacao);

        runOnce(() -> {
            getGameScene().removeUINode(textPontuacao);
        }, Duration.seconds(1));
        
        return pontosDoFantasma;
    }

    public static void initScoreBoard() {
        Text textPontuacao = getUIFactoryService().newText("", Color.WHITE, 8);
        textPontuacao.setX(8);
        textPontuacao.setY(24);
        textPontuacao.textProperty().bind(getWorldProperties().intProperty("score").asString("%07d"));
        getGameScene().addUINode(textPontuacao);
    }

    public static void updateScore() {
        set("score", pontuacaoAtual);
    }
}