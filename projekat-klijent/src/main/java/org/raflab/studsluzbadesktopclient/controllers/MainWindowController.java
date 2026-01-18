package org.raflab.studsluzbadesktopclient.controllers;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzbadesktopclient.app.MainView;
import org.raflab.studsluzbadesktopclient.services.NavigationHistoryService;
import org.raflab.studsluzbadesktopclient.utils.AlertHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainWindowController {

    private final MainView mainView;
    private final NavigationHistoryService historyService;

    @Value("${navigation.keyboard.back:CTRL+OPEN_BRACKET}")
    private String backKeyBinding;

    @Value("${navigation.keyboard.forward:CTRL+CLOSE_BRACKET}")
    private String forwardKeyBinding;

    @FXML private BorderPane mainPane;
    @FXML private StackPane contentPane;
    @FXML private Label statusLabel;
    @FXML private Label userLabel;

    @FXML
    public void initialize() {
        setupKeyboardNavigation();
        setupMouseNavigation();
    }

    /**
     * Postavlja tastaturne prečice za navigaciju
     */
    private void setupKeyboardNavigation() {
        // Ctrl + [ za nazad
        KeyCombination backCombo = new KeyCodeCombination(
                KeyCode.OPEN_BRACKET,
                KeyCombination.CONTROL_DOWN
        );

        // Ctrl + ] za napred
        KeyCombination forwardCombo = new KeyCodeCombination(
                KeyCode.CLOSE_BRACKET,
                KeyCombination.CONTROL_DOWN
        );

        mainPane.setOnKeyPressed(event -> {
            if (backCombo.match(event)) {
                handleNavigateBack();
                event.consume();
            } else if (forwardCombo.match(event)) {
                handleNavigateForward();
                event.consume();
            }
        });

        // Omogući da mainPane dobije focus
        Platform.runLater(() -> mainPane.requestFocus());
    }

    /**
     * Postavlja navigaciju mišem (dugmići 4 i 5)
     */
    private void setupMouseNavigation() {
        mainPane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            // Mouse button 4 = BACK
            if (event.getButton().toString().equals("BACK")) {
                handleNavigateBack();
                event.consume();
            }
            // Mouse button 5 = FORWARD
            else if (event.getButton().toString().equals("FORWARD")) {
                handleNavigateForward();
                event.consume();
            }
        });

        // Alternativno, možeš koristiti i button codes
        mainPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            int buttonCode = event.getButton().ordinal();

            // Button 4 (BACK) - ordinal je obično 3
            if (buttonCode == 3) {
                handleNavigateBack();
                event.consume();
            }
            // Button 5 (FORWARD) - ordinal je obično 4
            else if (buttonCode == 4) {
                handleNavigateForward();
                event.consume();
            }
        });
    }

    /**
     * Navigacija nazad
     */
    private void handleNavigateBack() {
        try {
            if (!historyService.canGoBack()) {
                return;
            }

            NavigationHistoryService.NavigationEntry entry = historyService.goBack();

            if (entry != null && entry.getContent() != null) {
                restoreNavigationEntry(entry);
                setStatus("Nazad: " + entry.getTitle());
            }
        } catch (Exception e) {
            System.err.println("Navigation back error (silently handled): " + e.getMessage());
        }
    }

    /**
     * Navigacija napred
     */
    private void handleNavigateForward() {
        try {
            if (!historyService.canGoForward()) {
                return;
            }

            NavigationHistoryService.NavigationEntry entry = historyService.goForward();

            if (entry != null && entry.getContent() != null) {
                restoreNavigationEntry(entry);
                setStatus("Napred: " + entry.getTitle());
            }
        } catch (Exception e) {
            System.err.println("Navigation forward error (silently handled): " + e.getMessage());
        }
    }

    /**
     * Vraća navigation entry u UI
     */
    private void restoreNavigationEntry(NavigationHistoryService.NavigationEntry entry) {
        switch (entry.getType()) {
            case TAB:
                restoreTab(entry);
                break;
            case LIST_ITEM:
            case TABLE_ITEM:
                // Ako je list/table item, samo prikaži sadržaj
                setContent(entry.getContent());
                break;
            case PAGE:
            default:
                setContent(entry.getContent());
                break;
        }
    }

    /**
     * Vraća tab selekciju
     */
    private void restoreTab(NavigationHistoryService.NavigationEntry entry) {
        // viewPath: "tabpane:studentProfileTabs" npr.
        String viewPath = entry.getViewPath();
        if (!viewPath.startsWith("tabpane:")) {
            return;
        }

        String tabPaneId = viewPath.substring("tabpane:".length());
        Object st = entry.getState();
        if (!(st instanceof Integer)) {
            return;
        }
        int tabIndex = (Integer) st;

        // lookup po #id
        Node n = mainPane.getScene().lookup("#" + tabPaneId);
        if (n instanceof TabPane tabPane) {
            int safeIndex = Math.max(0, Math.min(tabIndex, tabPane.getTabs().size() - 1));
            tabPane.getSelectionModel().select(safeIndex);

        }
    }

    public void setStatus(String status) {
        if (statusLabel != null) {
            statusLabel.setText(status);
        }
    }

    public void setContent(Node content) {
        contentPane.getChildren().clear();
        contentPane.getChildren().add(content);

        // Vrati focus na glavni pane za tastaturnu navigaciju
        Platform.runLater(() -> mainPane.requestFocus());
    }

    public void show(String fxmlPath) {
        Node view = mainView.loadPane(fxmlPath);
        setContent(view);
    }
}
