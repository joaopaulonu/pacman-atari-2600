package com.pacmanjava.ui;

import static com.pacmanjava.utils.Utils.createSprite;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.texture.Texture;

import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ResumeGame extends FXGLMenu {

    public ResumeGame() {
        super(MenuType.GAME_MENU);

        double screenw = FXGL.getAppWidth();
        double screenh = FXGL.getAppHeight();
        Rectangle fundoPreto = new Rectangle(screenw, screenh);
        fundoPreto.setFill(Color.color(0, 0, 0, 0.8));
        getContentRoot().getChildren().add(fundoPreto);

        Texture pause = createSprite("ui/pausedimage.png");
        pause.setPreserveRatio(true);
        pause.setFitWidth(50);
        pause.setTranslateX(94);
        pause.setTranslateY(109);

        Texture resume = createSprite("ui/resumeimage.png");
        resume.setPreserveRatio(true);
        resume.setFitWidth(150);
        resume.setTranslateX(45);
        resume.setTranslateY(115);

        Texture quit = createSprite("ui/quitimage.png");
        quit.setPreserveRatio(true);
        quit.setFitWidth(150);
        quit.setTranslateX(45);
        quit.setTranslateY(125);


        Button pressResume = new Button();
        pressResume.setStyle("-fx-background-color: transparent;");
        pressResume.setPrefSize(150, 20);
        pressResume.setTranslateX(45);
        pressResume.setTranslateY(65);
        pressResume.setOnAction(e -> fireResume());
        
        Button pressQuit = new Button();
        pressQuit.setStyle("-fx-background-color: transparent;");
        pressQuit.setPrefSize(150, 20);
        pressQuit.setTranslateX(45);
        pressQuit.setTranslateY(75);
        pressQuit.setOnAction(e -> fireExitToMainMenu());

        VBox box = new VBox(pause,resume,quit,pressResume,pressQuit);

        getContentRoot().getChildren().add(box);
    }
}
