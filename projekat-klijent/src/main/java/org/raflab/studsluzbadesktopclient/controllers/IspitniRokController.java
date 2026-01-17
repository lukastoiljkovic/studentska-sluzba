package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzbadesktopclient.app.MainView;
import org.raflab.studsluzbadesktopclient.services.IspitniRokService;
import org.raflab.studsluzbadesktopclient.utils.AlertHelper;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class IspitniRokController {

    private final IspitniRokService ispitniRokService;
    private final MainView mainView;

    @FXML private TableView<IspitniRokResponse> rokTable;
    @FXML private TableColumn<IspitniRokResponse, String> nazivCol;
    @FXML private TableColumn<IspitniRokResponse, String> pocetakCol;
    @FXML private TableColumn<IspitniRokResponse, String> zavrsetakCol;
    @FXML private TableColumn<IspitniRokResponse, String> skolskaGodinaCol;

    @FXML private Button noviRokBtn;
    @FXML private Button obrisiBtn;
    @FXML private Button refreshBtn;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @FXML
    public void initialize() {
        setupTable();
        loadRokovi();
    }

    private void setupTable() {
        nazivCol.setCellValueFactory(new PropertyValueFactory<>("naziv"));

        pocetakCol.setCellValueFactory(data -> {
            if (data.getValue().getDatumPocetka() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDatumPocetka().format(FORMATTER)
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        zavrsetakCol.setCellValueFactory(data -> {
            if (data.getValue().getDatumZavrsetka() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDatumZavrsetka().format(FORMATTER)
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        skolskaGodinaCol.setCellValueFactory(new PropertyValueFactory<>("skolskaGodinaNaziv"));
    }

    private void loadRokovi() {
        ispitniRokService.getAll()
                .collectList()
                .subscribe(
                        rokovi -> Platform.runLater(() ->
                                rokTable.setItems(FXCollections.observableArrayList(rokovi))
                        ),
                        error -> Platform.runLater(() ->
                                AlertHelper.showException("Greška pri učitavanju", (Exception) error))
                );
    }

    @FXML
    public void handleNoviRok() {
        IspitniRokFormController controller = mainView.openModalWithController(
                "ispitniRokForm",
                "Novi ispitni rok",
                500,
                400
        );

        if (controller != null) {
            controller.setOnSaveCallback(this::loadRokovi);
        }
    }

    @FXML
    public void handleObrisi() {
        IspitniRokResponse selected = rokTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Upozorenje", "Izaberite ispitni rok!");
            return;
        }

        boolean confirmed = AlertHelper.showConfirmation(
                "Potvrda brisanja",
                "Da li ste sigurni da želite da obrišete ispitni rok: " + selected.getNaziv() + "?"
        );

        if (!confirmed) return;

        ispitniRokService.delete(selected.getId())
                .subscribe(
                        unused -> Platform.runLater(() -> {
                            AlertHelper.showInfo("Uspeh", "Ispitni rok je obrisan!");
                            loadRokovi();
                        }),
                        error -> Platform.runLater(() ->
                                AlertHelper.showException("Greška pri brisanju", (Exception) error))
                );
    }

    @FXML
    public void handleRefresh() {
        loadRokovi();
    }
}