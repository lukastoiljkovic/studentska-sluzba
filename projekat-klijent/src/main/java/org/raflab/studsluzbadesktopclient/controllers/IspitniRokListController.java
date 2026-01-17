package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzbadesktopclient.app.MainView;
import org.raflab.studsluzbadesktopclient.services.IspitService;
import org.raflab.studsluzbadesktopclient.utils.AlertHelper;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class IspitniRokListController {

    private final IspitService ispitService;
    private final MainView mainView;

    @FXML private TableView<IspitResponse> ispitTable;
    @FXML private TableColumn<IspitResponse, String> predmetCol;
    @FXML private TableColumn<IspitResponse, String> rokCol;
    @FXML private TableColumn<IspitResponse, String> datumCol;
    @FXML private TableColumn<IspitResponse, String> nastavnikCol;
    @FXML private TableColumn<IspitResponse, Boolean> zakljucenCol;

    @FXML private Button noviIspitBtn;
    @FXML private Button prikaziPrijavljeneBtn;
    @FXML private Button prikaziRezultateBtn;
    @FXML private Button refreshBtn;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @FXML
    public void initialize() {
        setupTable();
        setupDoubleClick();
        loadIspiti();
    }

    private void setupTable() {
        predmetCol.setCellValueFactory(new PropertyValueFactory<>("predmetNaziv"));
        rokCol.setCellValueFactory(new PropertyValueFactory<>("ispitniRokNaziv"));

        datumCol.setCellValueFactory(data -> {
            if (data.getValue().getDatumVremePocetka() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDatumVremePocetka().format(FORMATTER)
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        nastavnikCol.setCellValueFactory(new PropertyValueFactory<>("nastavnikIme"));
        zakljucenCol.setCellValueFactory(new PropertyValueFactory<>("zakljucen"));

        zakljucenCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : (item ? "Da" : "Ne"));
                if (!empty && item != null && item) {
                    setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void setupDoubleClick() {
        ispitTable.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                handlePrikaziPrijavljene();
            }
        });
    }

    private void loadIspiti() {
        ispitService.getAll()
                .collectList()
                .subscribe(
                        ispiti -> Platform.runLater(() ->
                                ispitTable.setItems(FXCollections.observableArrayList(ispiti))
                        ),
                        error -> Platform.runLater(() ->
                                AlertHelper.showException("Greška pri učitavanju", (Exception) error))
                );
    }

    @FXML
    public void handleNoviIspit() {
        IspitFormController controller = mainView.openModalWithController(
                "ispitForm",
                "Novi ispit",
                600,
                400
        );

        if (controller != null) {
            controller.setOnSaveCallback(this::loadIspiti);
        }
    }

    @FXML
    public void handlePrikaziPrijavljene() {
        IspitResponse selected = ispitTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Upozorenje", "Izaberite ispit!");
            return;
        }

        IspitPrijavljeniController controller = mainView.openModalWithController(
                "ispitPrijavljeni",
                "Prijavljeni studenti - " + selected.getPredmetNaziv(),
                900,
                600
        );

        if (controller != null) {
            controller.setIspit(selected);
        }
    }

    @FXML
    public void handlePrikaziRezultate() {
        IspitResponse selected = ispitTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Upozorenje", "Izaberite ispit!");
            return;
        }

        IspitRezultatiController controller = mainView.openModalWithController(
                "ispitRezultati",
                "Rezultati ispita - " + selected.getPredmetNaziv(),
                1000,
                700
        );

        if (controller != null) {
            controller.setIspit(selected);
        }
    }

    @FXML
    public void handleRefresh() {
        loadIspiti();
    }
}