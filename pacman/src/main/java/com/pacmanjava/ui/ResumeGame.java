package com.pacmanjava.ui;

import static com.pacmanjava.utils.Utils.createSprite;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.texture.Texture;

import javafx.scene.control.Button;
import javafx.scene.layout.*;

public class ResumeGame extends FXGLMenu {

    public ResumeGame() {
        super(MenuType.GAME_MENU);

        Texture pause = createSprite("ui/pausedimage.png");
        pause.setFitWidth(pause.getFitWidth() - 96);

        Texture Resume = createSprite("ui/resumeimage.png");
        Resume.setFitWidth(250);
        Resume.setFitHeight(290);

        Button pressResume = new Button();
        pressResume.setPrefWidth(250);
        pressResume.setPrefHeight(150);
        pressResume.setStyle("-fx-background-image: url('/assets/textures/ui/resumeimage.png'); \" +\n" + //
                        "    \"-fx-background-repeat: no-repeat; \" + // Evita que a imagem se multiplique\n" + //
                        "    \"-fx-background-color: transparent;");
        pressResume.setOnAction(e -> fireResume());

        Texture Quit = createSprite("ui/quitimage.png");
        Quit.setFitWidth(250);
        Quit.setFitHeight(230);
        
        Button pressQuit = new Button();
        pressQuit.setPrefWidth(250);
        pressQuit.setPrefHeight(100);
        pressResume.setStyle("-fx-background-image: url('/assets/textures/ui/resumeimage.png'); \" +\n" + //
                        "    \"-fx-background-repeat: no-repeat; \" + // Evita que a imagem se multiplique\n" + //
                        "    \"-fx-background-color: transparent;");
        pressQuit.setOnAction(e -> fireExitToMainMenu());

        VBox box = new VBox(Resume, Quit, pressResume, pressQuit);

        getContentRoot().getChildren().add(box);
    }
}
