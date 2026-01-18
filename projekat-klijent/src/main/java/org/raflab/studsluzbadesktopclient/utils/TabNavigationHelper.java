package org.raflab.studsluzbadesktopclient.utils;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.raflab.studsluzbadesktopclient.services.NavigationHistoryService;

public class TabNavigationHelper {

    /**
     * Postavi automatsko praćenje tab promena
     */
    public static void setupTabNavigation(TabPane tabPane, NavigationHistoryService historyService) {
        if (tabPane.getId() == null || tabPane.getId().isBlank()) {
            throw new IllegalStateException("TabPane mora imati setId() ili fx:id da bi history radio.");
        }

        tabPane.getSelectionModel().selectedIndexProperty().addListener((obs, oldIdx, newIdx) -> {
            if (newIdx == null) return;
            if (historyService.isNavigating()) return;

            int idx = newIdx.intValue();
            Tab tab = tabPane.getTabs().get(idx);

            // bitno: content ovde ne treba!
            historyService.pushTab(tabPane.getId(), idx, tab.getText());
        });
    }
}
