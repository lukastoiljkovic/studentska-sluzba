package org.raflab.studsluzbadesktopclient.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MainView {

    private final ContextFXMLLoader fxmlLoader;
    private Scene scene;

    public Scene createScene() {
        try {
            FXMLLoader loader = fxmlLoader.getLoader("/fxml/main.fxml");
            Parent root = loader.load();
            this.scene = new Scene(root, 1000, 600);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/stylesheet.css")).toExternalForm());
            return scene;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Greška pri učitavanju glavne scene", e);
        }
    }

    public void changeRoot(String fxmlPath) {
        try {
            FXMLLoader loader = fxmlLoader.getLoader("/fxml/" + fxmlPath + ".fxml");
            scene.setRoot(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Node loadPane(String fxmlPath) {
        try {
            FXMLLoader loader = fxmlLoader.getLoader("/fxml/" + fxmlPath + ".fxml");
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void openModal(String fxmlPath, String title) {
        openModal(fxmlPath, title, 600, 500);
    }

    public void openModal(String fxmlPath, String title, int width, int height) {
        try {
            FXMLLoader loader = fxmlLoader.getLoader("/fxml/" + fxmlPath + ".fxml");
            Parent parent = loader.load();

            Scene modalScene = new Scene(parent, width, height);
            modalScene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/css/stylesheet.css"))
                            .toExternalForm()
            );

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(modalScene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> T openModalWithController(String fxmlPath, String title, int width, int height) {
        try {
            FXMLLoader loader = fxmlLoader.getLoader("/fxml/" + fxmlPath + ".fxml");
            Parent parent = loader.load();

            Scene modalScene = new Scene(parent, width, height);
            modalScene.getStylesheets().add(
                    Objects.requireNonNull(getClass().getResource("/css/stylesheet.css"))
                            .toExternalForm()
            );

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(modalScene);
            stage.showAndWait();

            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}