package com.pacmanjava.ui;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.ui.DialogFactoryService;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ConfirmationPopUp extends DialogFactoryService {
    //@Override
public Pane confirmationDialog(String message, Consumer<Boolean> callback) {
    double screenw = FXGL.getAppWidth();
    double screenh = FXGL.getAppHeight();

    Pane fundoOverlay = new Pane();
    fundoOverlay.setPrefSize(screenw, screenh);
    fundoOverlay.setStyle("-fx-background-color: black; -fx-opacity: 0.7;");

   
    VBox caixaDialogo = new VBox(25);
    caixaDialogo.setStyle("-fx-background-color: black; -fx-border-color: blue; -fx-border-width: 3px; -fx-padding: 30px; -fx-alignment: center;");
    
    caixaDialogo.setMaxSize(450, 200); 

    Text texto = FXGL.getUIFactoryService().newText("DESEJA SAIR DO JOGO?", Color.YELLOW, 10);

    var btnYes = FXGL.getUIFactoryService().newButton("SIM");
    btnYes.setOnAction(e -> callback.accept(true));

    var btnNo = FXGL.getUIFactoryService().newButton("NÃO");
    btnNo.setOnAction(e -> callback.accept(false));

    HBox botoes = new HBox(40, btnYes, btnNo);
    botoes.setStyle("-fx-alignment: center;");

    caixaDialogo.getChildren().addAll(texto, botoes);

    
    StackPane layoutFinal = new StackPane();
    layoutFinal.getChildren().addAll(fundoOverlay, caixaDialogo);

    return layoutFinal;
}

    @Override
    public Pane messageDialog(String message, Runnable callback) { return new VBox(); }

    @Override
    public Pane messageDialog(String message) { return new VBox(); }

    @Override
    public Pane inputDialog(String message, Predicate<String> filter, Consumer<String> callback) { return new VBox(); }

    @Override
    public Pane inputDialogWithCancel(String message, Predicate<String> filter, Consumer<String> callback) { return new VBox(); }

    @Override
    public Pane errorDialog(Throwable error, Runnable callback) { return new VBox(); }

    @Override
    public <T> Pane choiceDialog(String message, Consumer<T> resultCallback, T firstOption, T... options) {
        throw new UnsupportedOperationException("Unimplemented method 'choiceDialog'");
    }

    //@Override
    public <T> Pane choiceDialog(String message, List<T> options, Consumer<T> resultCallback) {
        throw new UnsupportedOperationException("Unimplemented method 'choiceDialog'");
    }

    @Override
    public Pane inputDialog(String message, Consumer<String> callback) {
        throw new UnsupportedOperationException("Unimplemented method 'inputDialog'");
    }

    @Override
    public Pane errorDialog(Throwable error) {
        throw new UnsupportedOperationException("Unimplemented method 'errorDialog'");
    }

    @Override
    public Pane errorDialog(String errorMessage) {
        throw new UnsupportedOperationException("Unimplemented method 'errorDialog'");
    }

    @Override
    public Pane errorDialog(String errorMessage, Runnable callback) {
        throw new UnsupportedOperationException("Unimplemented method 'errorDialog'");
    }

    @Override
    public Pane progressDialog(String message, ReadOnlyDoubleProperty observable, Runnable callback) {
        throw new UnsupportedOperationException("Unimplemented method 'progressDialog'");
    }

    @Override
    public Pane progressDialogIndeterminate(String message, Runnable callback) {
        throw new UnsupportedOperationException("Unimplemented method 'progressDialogIndeterminate'");
    }

    @Override
    public Pane customDialog(String message, Node content, Runnable callback, Button... buttons) {
        throw new UnsupportedOperationException("Unimplemented method 'customDialog'");
    }
}