package org.raflab.studsluzbadesktopclient.utils;

import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import org.raflab.studsluzbadesktopclient.services.NavigationHistoryService;

/**
 * Helper klasa za automatsko praćenje list/table navigacije
 */
public class ListNavigationHelper {

    /**
     * Postavi automatsko praćenje ListView selekcije
     */
    public static <T> void setupListNavigation(ListView<T> listView,
                                               String listId,
                                               NavigationHistoryService historyService,
                                               ListItemTitleProvider<T> titleProvider) {
        listView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, newValue) -> {
                    if (newValue != null && !historyService.isNavigating()) {
                        String title = titleProvider.getTitle(newValue);
                        historyService.pushListSelection(listId, title, null);
                    }
                }
        );
    }

    /**
     * Postavi automatsko praćenje TableView selekcije
     */
    public static <T> void setupTableNavigation(TableView<T> tableView,
                                                String tableId,
                                                NavigationHistoryService historyService,
                                                ListItemTitleProvider<T> titleProvider) {
        tableView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldValue, newValue) -> {
                    if (newValue != null && !historyService.isNavigating()) {
                        String title = titleProvider.getTitle(newValue);
                        historyService.pushListSelection(tableId, title, null);
                    }
                }
        );
    }

    /**
     * Interface za dobijanje naslova iz itema
     */
    @FunctionalInterface
    public interface ListItemTitleProvider<T> {
        String getTitle(T item);
    }
}
