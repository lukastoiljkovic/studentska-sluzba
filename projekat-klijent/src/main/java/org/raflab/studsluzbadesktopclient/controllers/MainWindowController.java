package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
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
        setupTouchpadGestures();
    }

    private void setupKeyboardNavigation() {
        KeyCombination backCombo = new KeyCodeCombination(
                KeyCode.OPEN_BRACKET,
                KeyCombination.CONTROL_DOWN
        );

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

        Platform.runLater(() -> mainPane.requestFocus());
    }

    private void setupMouseNavigation() {
        mainPane.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton().toString().equals("BACK")) {
                handleNavigateBack();
                event.consume();
            }
            else if (event.getButton().toString().equals("FORWARD")) {
                handleNavigateForward();
                event.consume();
            }
        });

        mainPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            int buttonCode = event.getButton().ordinal();

            if (buttonCode == 3) {
                handleNavigateBack();
                event.consume();
            }
            else if (buttonCode == 4) {
                handleNavigateForward();
                event.consume();
            }
        });
    }

    private void handleNavigateBack() {
        try {
            if (!historyService.canGoBack()) {
                return;
            }

            NavigationHistoryService.NavigationEntry entry = historyService.goBack();

            if (entry != null) {
                restoreNavigationEntry(entry);
                setStatus("⬅️ Nazad: " + entry.getTitle());
            }
        } catch (Exception e) {
            System.err.println("Navigation back error (silently handled): " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleNavigateForward() {
        try {
            if (!historyService.canGoForward()) {
                return;
            }

            NavigationHistoryService.NavigationEntry entry = historyService.goForward();

            if (entry != null) {
                restoreNavigationEntry(entry);
                setStatus("➡️ Napred: " + entry.getTitle());
            }
        } catch (Exception e) {
            System.err.println("Navigation forward error (silently handled): " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void restoreNavigationEntry(NavigationHistoryService.NavigationEntry entry) {
        System.out.println("🔄 Restoring: " + entry.getTitle() + " (type: " + entry.getType() + ")");

        switch (entry.getType()) {
            case TAB:
                restoreTab(entry);
                break;
            case LIST_ITEM:
            case TABLE_ITEM:
                if (entry.getContent() != null) {
                    setContent(entry.getContent());
                }
                break;
            case PAGE:
            default:
                if (entry.getContent() != null) {
                    setContent(entry.getContent());
                }
                break;
        }
    }


    private void restoreTab(NavigationHistoryService.NavigationEntry entry) {
        if (!(entry.getState() instanceof NavigationHistoryService.TabState)) {
            System.err.println("️State is not TabState!");
            return;
        }

        NavigationHistoryService.TabState st =
                (NavigationHistoryService.TabState) entry.getState();

        Node lookupRoot = contentPane.getChildren().isEmpty()
                ? mainPane
                : contentPane.getChildren().get(0);

        TabPane tabPane = (TabPane) lookupRoot.lookup("#" + st.tabPaneId());

        if (tabPane == null) {
            System.err.println("TabPane not found: #" + st.tabPaneId());
            return;
        }

        int idx = st.tabIndex();
        if (idx < 0 || idx >= tabPane.getTabs().size()) {
            System.err.println("Tab index out of bounds: " + idx);
            return;
        }

        Platform.runLater(() -> {
            tabPane.getSelectionModel().select(idx);
            System.out.println("Tab restored: " + tabPane.getTabs().get(idx).getText() + " (index: " + idx + ")");
        });
    }

    private void setupTouchpadGestures() {
        mainPane.setOnSwipeLeft(event -> {
            handleNavigateForward();
            event.consume();
        });

        mainPane.setOnSwipeRight(event -> {
            handleNavigateBack();
            event.consume();
        });
    }

    public void setStatus(String status) {
        if (statusLabel != null) {
            statusLabel.setText(status);
        }
    }

    public void setContent(Node content) {
        contentPane.getChildren().clear();
        contentPane.getChildren().add(content);

        Platform.runLater(() -> mainPane.requestFocus());
    }

    public void show(String fxmlPath) {
        Node view = mainView.loadPane(fxmlPath);
        setContent(view);
    }
}

