package org.raflab.studsluzbadesktopclient.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzbadesktopclient.app.MainView;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainWindowController {

    private final MainView mainView;

    @FXML private BorderPane mainPane;
    @FXML private StackPane contentPane;
    @FXML private Label statusLabel;
    @FXML private Label userLabel;

    @FXML
    public void initialize() {
        // Postavi početni sadržaj
        //statusLabel.setText("Spremno");
        //userLabel.setText("Korisnik: Administrator");
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    public void setContent(javafx.scene.Node content) {
        contentPane.getChildren().clear();
        contentPane.getChildren().add(content);
    }

    public void show(String fxmlPath) {
        Node view = mainView.loadPane(fxmlPath);
        setContent(view);
    }

}