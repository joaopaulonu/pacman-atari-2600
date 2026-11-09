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
        Resume.setFitWidth(Resume.getFitWidth() - 310);

        Button pressResume = new Button();
        pressResume.setPrefWidth(pressResume.getWidth());
        pressResume.setOnAction(e -> fireResume());

        Texture Quit = createSprite("ui/quitimage.png");
        Quit.setFitWidth(Quit.getFitWidth() - 308);
        
        Button pressQuit = new Button();
        pressQuit.setPrefWidth(pressQuit.getWidth() - 308);
        pressQuit.setOnAction(e -> fireExitToMainMenu());

        VBox box = new VBox(Resume, Quit, pressResume, pressQuit);

        getContentRoot().getChildren().add(box);
    }
}
