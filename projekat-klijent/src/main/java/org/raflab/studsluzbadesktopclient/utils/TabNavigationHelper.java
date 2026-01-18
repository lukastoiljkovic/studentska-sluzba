package org.raflab.studsluzbadesktopclient.utils;
import javafx.scene.control.TabPane;
import org.raflab.studsluzbadesktopclient.services.NavigationHistoryService;

public class TabNavigationHelper {

    public static void setupTabNavigation(TabPane tabPane,
                                          NavigationHistoryService historyService) {

        tabPane.getSelectionModel().selectedIndexProperty().addListener(
                (obs, oldIdx, newIdx) -> {
                    if (newIdx == null) return;
                    if (historyService.isNavigating()) return;

                    String tabPaneId = tabPane.getId();
                    if (tabPaneId == null || tabPaneId.isBlank()) {
                        System.err.println("TabPane nema ID! Dodaj id=\"...\" u FXML!");
                        return;
                    }

                    int idx = newIdx.intValue();
                    if (idx < 0 || idx >= tabPane.getTabs().size()) return;

                    String title = tabPane.getTabs().get(idx).getText();

                    historyService.pushTab(tabPaneId, idx, title);

                    System.out.println("📑 Tab changed: " + title + " (index: " + idx + ")");
                }
        );

        System.out.println("Tab navigation setup for TabPane ID: " + tabPane.getId());
    }
}