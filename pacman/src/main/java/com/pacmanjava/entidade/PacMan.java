package com.pacmanjava.entidade;

import com.almasb.fxgl.entity.Entity;
import javafx.scene.input.KeyCode;

import com.pacmanjava.animacoes.AnimationComponent;
import com.pacmanjava.PacManApp;

import static com.almasb.fxgl.dsl.FXGL.*;

//to-do: 
// Extender EntidadeJogo
// Fazer que as propriedades e métodos não sejam static
public class PacMan {
    private static int posX;
    private static int posY;
    private static double speed = 60;
    private static String bufferDirection = ""; //última direção presionada pelo teclado
    private static String currentDirection = ""; //direção atual do pacman
    private static Entity player;

    private static final int WIDTH = PacManApp.WIDTH;
    private static final int HEIGHT = PacManApp.HEIGHT;

    //verifica se a a posição passada por argumento tá dentro de um tile
    private static boolean onTile(int x, int y) {
        //um tile == 8 pixels
        float posTilesX = (float)(x - 4) / 8;
        float posTilesY = (float)(y - 4) / 8;
        float nearestTileX = (float)Math.round(posTilesX);
        float nearestTileY = (float)Math.round(posTilesY);

        if (posTilesX == nearestTileX && posTilesY == nearestTileY) {
            return true;
        }

        return false;
    } 

    //coloca o player criado no PacManApp na variável player
    public static void initPacMan(Entity plr) {
        player = plr;
    }

    //adiciona e cria as interações dos controles do jogo
    public static void initMovementKeys() {
        onKey(KeyCode.RIGHT, () -> {
            bufferDirection = "RIGHT";
        });
        onKey(KeyCode.LEFT, () -> {
            bufferDirection = "LEFT";
        });
        onKey(KeyCode.UP, () -> {
            bufferDirection = "UP";
        });
        onKey(KeyCode.DOWN, () -> {
            bufferDirection = "DOWN";
        });
    }

    //maneja o movimento do jogador (movimentação e teleportagem nos extremos)
    public static void handleMovement(double tpf) {
        posX = (int) player.getPosition().getX();
        posY = (int) player.getPosition().getY();

        //atualiza a direção do jogador
        if (onTile(posX, posY) && currentDirection != bufferDirection) {
            currentDirection = bufferDirection;
            
            //teleporta para exatamente o centro do tile
            player.setX(posX);
            player.setY(posY);
        }

        //movimentar jogador
        switch(currentDirection) {
            case "RIGHT":  
                player.translateX(speed * tpf); 
                player.getComponent(AnimationComponent.class).moveRight(); 
                break;   
            case "LEFT": 
                player.translateX(-speed * tpf); 
                player.getComponent(AnimationComponent.class).moveLeft(); 
                break;
            case "UP": 
                player.translateY(-speed * tpf); 
                player.getComponent(AnimationComponent.class).moveUp(); 
                break;
            case "DOWN": 
                player.translateY(speed * tpf); 
                player.getComponent(AnimationComponent.class).moveDown(); 
                break;
        }

        //teleporta o jogador para o extremo oposto do mapa
        if (Math.floorMod(posX, WIDTH) != posX) { 
            player.setX(Math.floorMod(posX, WIDTH));
        }
        if (Math.floorMod(posY, HEIGHT) != posY) {
            player.setY(Math.floorMod(posY, HEIGHT));
        }
    }
}