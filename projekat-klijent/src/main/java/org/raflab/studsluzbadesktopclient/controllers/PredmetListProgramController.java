package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzbadesktopclient.app.MainView;
import org.raflab.studsluzbadesktopclient.services.PredmetService;
import org.raflab.studsluzbadesktopclient.utils.AlertHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PredmetListProgramController {

    private final PredmetService predmetService;
    private final MainView mainView;

    private StudijskiProgramResponse studijskiProgram;

    @FXML private Label programLabel;
    @FXML private TableView<PredmetResponse> predmetTable;
    @FXML private TableColumn<PredmetResponse, String> sifraCol;
    @FXML private TableColumn<PredmetResponse, String> nazivCol;
    @FXML private TableColumn<PredmetResponse, Integer> semestarCol;
    @FXML private TableColumn<PredmetResponse, Integer> espbCol;
    @FXML private TableColumn<PredmetResponse, Boolean> obavezanCol;
    @FXML private TableColumn<PredmetResponse, Integer> predavanjaCol;
    @FXML private TableColumn<PredmetResponse, Integer> vezbeCol;

    @FXML private TextField fromYearTf;
    @FXML private TextField toYearTf;
    @FXML private Button prosekBtn;
    @FXML private Button stampaBtn;

    public void setStudijskiProgram(StudijskiProgramResponse program) {
        this.studijskiProgram = program;
        initializeData();
    }

    @FXML
    public void initialize() {
        setupTable();
    }

    private void setupTable() {
        sifraCol.setCellValueFactory(new PropertyValueFactory<>("sifra"));
        nazivCol.setCellValueFactory(new PropertyValueFactory<>("naziv"));
        semestarCol.setCellValueFactory(new PropertyValueFactory<>("semestar"));
        espbCol.setCellValueFactory(new PropertyValueFactory<>("espb"));
        obavezanCol.setCellValueFactory(new PropertyValueFactory<>("obavezan"));
        predavanjaCol.setCellValueFactory(new PropertyValueFactory<>("fondPredavanja"));
        vezbeCol.setCellValueFactory(new PropertyValueFactory<>("fondVezbi"));

        obavezanCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : (item ? "Da" : "Ne"));
            }
        });
    }

    private void initializeData() {
        if (studijskiProgram == null) return;

        programLabel.setText(studijskiProgram.getNaziv());

        predmetService.getPredmetiNaStudijskomProgramu(studijskiProgram.getId())
                .subscribe(
                        predmeti -> Platform.runLater(() ->
                                predmetTable.setItems(FXCollections.observableArrayList(predmeti))
                        ),
                        error -> Platform.runLater(() ->
                                AlertHelper.showException("Greška pri učitavanju predmeta", (Exception) error))
                );
    }

    @FXML
    public void handleProsecnaOcena() {
        PredmetResponse selected = predmetTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Upozorenje", "Izaberite predmet!");
            return;
        }

        String fromStr = fromYearTf.getText().trim();
        String toStr = toYearTf.getText().trim();

        if (fromStr.isEmpty() || toStr.isEmpty()) {
            AlertHelper.showWarning("Upozorenje", "Unesite raspon godina!");
            return;
        }

        try {
            Integer from = Integer.parseInt(fromStr);
            Integer to = Integer.parseInt(toStr);

            if (from > to) {
                AlertHelper.showWarning("Upozorenje", "Početna godina mora biti manja od krajnje!");
                return;
            }

            predmetService.getProsecnaOcena(selected.getId(), from, to)
                    .subscribe(
                            prosek -> Platform.runLater(() -> {
                                String msg = String.format("Prosečna ocena za %s (%d-%d): %.2f",
                                        selected.getNaziv(), from, to, prosek);
                                AlertHelper.showInfo("Prosečna ocena", msg);
                            }),
                            error -> Platform.runLater(() ->
                                    AlertHelper.showException("Greška", (Exception) error))
                    );

        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Upozorenje", "Godine moraju biti brojevi!");
        }
    }

    @FXML
    public void handleStampaProseka() {
        AlertHelper.showInfo("Info", "Funkcionalnost štampanja izveštaja biće implementirana naknadno.");
    }

    @FXML
    public void handleRefresh() {
        initializeData();
    }
}