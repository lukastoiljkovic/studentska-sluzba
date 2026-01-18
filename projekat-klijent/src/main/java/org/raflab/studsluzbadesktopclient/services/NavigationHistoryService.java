package org.raflab.studsluzbadesktopclient.services;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.ListView;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NavigationHistoryService {

    @Value("${navigation.history.max-size:10}")
    private int maxHistorySize;

    @Getter
    private final List<NavigationEntry> backStack = new ArrayList<>();

    @Getter
    private final List<NavigationEntry> forwardStack = new ArrayList<>();

    private NavigationEntry currentEntry;

    private boolean isNavigating = false; // Flag da izbegnemo duplikate

    /**
     * Dodaje novu stranicu u istoriju
     */
    public void pushPage(String viewPath, String title, Node content) {
        // Ignoriši ako smo u procesu navigacije (back/forward)
        if (isNavigating) {
            return;
        }

        // Proveri da li je ista stranica kao trenutna (izbegni duplikate)
        if (currentEntry != null &&
                currentEntry.getViewPath().equals(viewPath) &&
                currentEntry.getTitle().equals(title)) {
            return;
        }

        // Ako postoji trenutna stranica, dodaj je u back stack
        if (currentEntry != null) {
            backStack.add(currentEntry);

            // Proveri maksimalnu veličinu i ukloni najstariji ako je potrebno
            if (backStack.size() > maxHistorySize) {
                backStack.remove(0);
            }
        }

        // Nova stranica postaje trenutna
        currentEntry = new NavigationEntry(viewPath, title, content);

        // Obriši forward stack jer smo otvorili novu stranicu
        forwardStack.clear();
    }

    /**
     * Dodaje tab promenu u istoriju
     */
    public void pushTab(String tabPaneId, int tabIndex, String tabTitle) {
        pushPage("tabpane:" + tabPaneId, tabTitle, null, tabIndex);
    }

    public void pushPage(String viewPath, String title, Node content, Object state) {
        if (isNavigating) return;

        if (currentEntry != null &&
                currentEntry.getViewPath().equals(viewPath) &&
                currentEntry.getTitle().equals(title) &&
                ((currentEntry.getState() == null && state == null) || (currentEntry.getState()!=null && currentEntry.getState().equals(state)))
        ) return;

        if (currentEntry != null) {
            backStack.add(currentEntry);
            if (backStack.size() > maxHistorySize) backStack.remove(0);
        }

        currentEntry = new NavigationEntry(viewPath, title, content, state);
        forwardStack.clear();
    }

    /**
     * Dodaje selekciju iz liste/tabele u istoriju
     */
    public void pushListSelection(String listId, String itemTitle, Node content) {
        pushPage("list:" + listId + ":" + itemTitle, itemTitle, content);
    }

    /**
     * Vraća se na prethodnu stranicu
     */
    public NavigationEntry goBack() {
        if (!canGoBack()) {
            return null;
        }

        isNavigating = true;

        try {
            // Dodaj trenutnu stranicu u forward stack
            if (currentEntry != null) {
                forwardStack.add(currentEntry);
            }

            // Uzmi poslednju stranicu iz back stacka
            NavigationEntry previousEntry = backStack.remove(backStack.size() - 1);
            currentEntry = previousEntry;

            return previousEntry;
        } finally {
            isNavigating = false;
        }
    }

    /**
     * Ide na sledeću stranicu (forward)
     */
    public NavigationEntry goForward() {
        if (!canGoForward()) {
            return null;
        }

        isNavigating = true;

        try {
            // Dodaj trenutnu stranicu nazad u back stack
            if (currentEntry != null) {
                backStack.add(currentEntry);
            }

            // Uzmi poslednju stranicu iz forward stacka
            NavigationEntry nextEntry = forwardStack.remove(forwardStack.size() - 1);
            currentEntry = nextEntry;

            return nextEntry;
        } finally {
            isNavigating = false;
        }
    }

    /**
     * Proverava da li može da se ide nazad
     */
    public boolean canGoBack() {
        return !backStack.isEmpty();
    }

    /**
     * Proverava da li može da se ide napred
     */
    public boolean canGoForward() {
        return !forwardStack.isEmpty();
    }

    /**
     * Vraća trenutnu stranicu
     */
    public NavigationEntry getCurrentEntry() {
        return currentEntry;
    }

    /**
     * Briše celu istoriju
     */
    public void clearHistory() {
        backStack.clear();
        forwardStack.clear();
        currentEntry = null;
    }

    /**
     * Proverava da li trenutno navigiramo (korisno za izbegavanje duplikata)
     */
    public boolean isNavigating() {
        return isNavigating;
    }

    /**
     * Klasa koja predstavlja jedan unos u istoriji
     */
    @Getter
    public static class NavigationEntry {
        private final String viewPath;
        private final String title;
        private final Node content;
        private final long timestamp;
        private final NavigationType type;
        private final Object state; // Dodatno stanje (npr. indeks taba, id predmeta, itd.)

        public NavigationEntry(String viewPath, String title, Node content) {
            this(viewPath, title, content, null);
        }

        public NavigationEntry(String viewPath, String title, Node content, Object state) {
            this.viewPath = viewPath;
            this.title = title;
            this.content = content;
            this.state = state;
            this.timestamp = System.currentTimeMillis();
            this.type = determineType(viewPath);
        }

        private NavigationType determineType(String path) {
            if (path.startsWith("tab:")) return NavigationType.TAB;
            if (path.startsWith("list:")) return NavigationType.LIST_ITEM;
            if (path.startsWith("table:")) return NavigationType.TABLE_ITEM;
            return NavigationType.PAGE;
        }

        @Override
        public String toString() {
            return title + " (" + viewPath + ")";
        }
    }

    public enum NavigationType {
        PAGE,
        TAB,
        LIST_ITEM,
        TABLE_ITEM
    }
}
