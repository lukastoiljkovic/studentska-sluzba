package org.raflab.studsluzbadesktopclient.services;

import javafx.scene.Node;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class NavigationHistoryService {

    @Value("${navigation.history.max-size:10}")
    private int maxHistorySize;

    @Getter
    private final List<NavigationEntry> backStack = new ArrayList<>();

    @Getter
    private final List<NavigationEntry> forwardStack = new ArrayList<>();

    private NavigationEntry currentEntry;

    private boolean isNavigating = false;

    public void pushPage(String viewPath, String title, Node content) {
        pushPage(viewPath, title, content, null);
    }

    public void pushPage(String viewPath, String title, Node content, Object state) {
        if (isNavigating) {
            return;
        }

        if (currentEntry != null &&
                currentEntry.getViewPath().equals(viewPath) &&
                currentEntry.getTitle().equals(title) &&
                Objects.equals(currentEntry.getState(), state)) {
            return;
        }

        if (currentEntry != null) {
            backStack.add(currentEntry);

            if (backStack.size() > maxHistorySize) {
                backStack.remove(0);
            }
        }

        currentEntry = new NavigationEntry(viewPath, title, content, state);

        forwardStack.clear();

    }

    public void pushTab(String tabPaneId, int tabIndex, String tabTitle) {
        TabState state = new TabState(tabPaneId, tabIndex);
        pushPage("tab:" + tabPaneId + ":" + tabIndex, tabTitle, null, state);
    }

    public void pushListSelection(String listId, String itemTitle, Node content) {
        pushPage("list:" + listId + ":" + itemTitle, itemTitle, content, null);
    }

    public NavigationEntry goBack() {
        if (!canGoBack()) {
            return null;
        }

        isNavigating = true;

        try {
            if (currentEntry != null) {
                forwardStack.add(currentEntry);
            }

            NavigationEntry previousEntry = backStack.remove(backStack.size() - 1);
            currentEntry = previousEntry;

            System.out.println("⬅️ Back to: " + previousEntry.getTitle());

            return previousEntry;
        } finally {
            isNavigating = false;
        }
    }

    public NavigationEntry goForward() {
        if (!canGoForward()) {
            return null;
        }

        isNavigating = true;

        try {
            if (currentEntry != null) {
                backStack.add(currentEntry);
            }

            NavigationEntry nextEntry = forwardStack.remove(forwardStack.size() - 1);
            currentEntry = nextEntry;

            System.out.println("➡️ Forward to: " + nextEntry.getTitle());

            return nextEntry;
        } finally {
            isNavigating = false;
        }
    }

    public boolean canGoBack() {
        return !backStack.isEmpty();
    }

    public boolean canGoForward() {
        return !forwardStack.isEmpty();
    }

    public NavigationEntry getCurrentEntry() {
        return currentEntry;
    }

    public void clearHistory() {
        backStack.clear();
        forwardStack.clear();
        currentEntry = null;
    }

    public boolean isNavigating() {
        return isNavigating;
    }

    @Getter
    public static class NavigationEntry {
        private final String viewPath;
        private final String title;
        private final Node content;
        private final long timestamp;
        private final NavigationType type;
        private final Object state;

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

    public record TabState(String tabPaneId, int tabIndex) {}
}