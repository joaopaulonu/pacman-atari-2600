import static com.pacmanjava.utils.Utils.createSprite;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class ResumeGame extends FXGLMenu {

    public ResumeGame() {
        super(MenuType.GAME_MENU);

        pause = createSprite("ui/pausedimage.png");
        pause.setFitWidth(pause.getFitWidth() - 30);

        pressResume = createSprite("ui/resumeimage.png");
        pressResume.setFitWidth(pressResume.getFitWidth() - 30);

        pressResume = createSprite("ui/resumeimage.png");
        pressResume.setFitWidth(pressResume.getFitWidth() - 30);

        // Criação do botão personalizado
        Button btnResume = new Button("Retomar Partida");
        
        // Ação principal do FXGL para fechar o menu e voltar ao jogo
        btnResume.setOnAction(e -> fireResume());

        // Adicionando um botão de sair como extra
        Button btnSair = new Button("Sair para o Menu Principal");
        btnSair.setOnAction(e -> fireExitToMainMenu());

        // Organizando na tela
        VBox box = new VBox(20, btnResume, btnSair);
        box.setTranslateX(getAppWidth() / 2.0 - 100);
        box.setTranslateY(getAppHeight() / 2.0 - 50);

        // Adicionando a interface ao painel (root) do menu
        getContentRoot().getChildren().add(box);
    }
}
