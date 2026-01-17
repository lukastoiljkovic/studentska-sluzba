package org.raflab.studsluzbadesktopclient.utils;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TableHelper {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static <S, T> TableColumn<S, T> createColumn(String text,
                                                        String property,
                                                        int prefWidth) {
        TableColumn<S, T> column = new TableColumn<>(text);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(prefWidth);
        return column;
    }

    public static <S> TableColumn<S, LocalDate> createDateColumn(String text,
                                                                 String property,
                                                                 int prefWidth) {
        TableColumn<S, LocalDate> column = new TableColumn<>(text);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(prefWidth);

        column.setCellFactory(col -> new TableCell<S, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(DATE_FORMATTER));
                }
            }
        });

        return column;
    }

    public static <S> TableColumn<S, LocalDateTime> createDateTimeColumn(String text,
                                                                         String property,
                                                                         int prefWidth) {
        TableColumn<S, LocalDateTime> column = new TableColumn<>(text);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(prefWidth);

        column.setCellFactory(col -> new TableCell<S, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime dateTime, boolean empty) {
                super.updateItem(dateTime, empty);
                if (empty || dateTime == null) {
                    setText(null);
                } else {
                    setText(dateTime.format(DATETIME_FORMATTER));
                }
            }
        });

        return column;
    }

    public static <S> TableColumn<S, Boolean> createBooleanColumn(String text,
                                                                  String property,
                                                                  int prefWidth) {
        TableColumn<S, Boolean> column = new TableColumn<>(text);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(prefWidth);

        column.setCellFactory(col -> new TableCell<S, Boolean>() {
            @Override
            protected void updateItem(Boolean value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(value ? "Da" : "Ne");
                }
            }
        });

        return column;
    }
}