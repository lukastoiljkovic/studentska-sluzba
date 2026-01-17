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
import org.raflab.studsluzbadesktopclient.services.SifarniciService;
import org.raflab.studsluzbadesktopclient.utils.AlertHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudijskiProgramListController {

    private final SifarniciService sifarniciService;
    private final MainView mainView;

    @FXML private TableView<StudijskiProgramResponse> programTable;
    @FXML private TableColumn<StudijskiProgramResponse, String> oznakaCol;
    @FXML private TableColumn<StudijskiProgramResponse, String> nazivCol;
    @FXML private TableColumn<StudijskiProgramResponse, Integer> godinaCol;
    @FXML private TableColumn<StudijskiProgramResponse, String> zvanjeCol;
    @FXML private TableColumn<StudijskiProgramResponse, Integer> espbCol;

    @FXML private Button predmetiBtn;
    @FXML private Button dodajPredmetBtn;
    @FXML private Button noviProgramBtn;

    @FXML
    public void initialize() {
        setupTable();
        setupDoubleClick();
        loadPrograms();
    }

    private void setupTable() {
        oznakaCol.setCellValueFactory(new PropertyValueFactory<>("oznaka"));
        nazivCol.setCellValueFactory(new PropertyValueFactory<>("naziv"));
        godinaCol.setCellValueFactory(new PropertyValueFactory<>("godinaAkreditacije"));
        zvanjeCol.setCellValueFactory(new PropertyValueFactory<>("zvanje"));
        espbCol.setCellValueFactory(new PropertyValueFactory<>("ukupnoEspb"));
    }

    private void setupDoubleClick() {
        programTable.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                handlePrikaziPredmete();
            }
        });
    }

    private void loadPrograms() {
        sifarniciService.getAllStudijskiProgrami()
                .collectList()
                .subscribe(
                        programi -> Platform.runLater(() ->
                                programTable.setItems(FXCollections.observableArrayList(programi))
                        ),
                        error -> Platform.runLater(() ->
                                AlertHelper.showException("Greška pri učitavanju", (Exception) error))
                );
    }

    @FXML
    public void handlePrikaziPredmete() {
        StudijskiProgramResponse selected = programTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Upozorenje", "Izaberite studijski program!");
            return;
        }

        PredmetListProgramController controller = mainView.openModalWithController(
                "predmetListProgram",
                "Predmeti - " + selected.getNaziv(),
                1000,
                600
        );

        if (controller != null) {
            controller.setStudijskiProgram(selected);
        }
    }

    @FXML
    public void handleDodajPredmet() {
        StudijskiProgramResponse selected = programTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Upozorenje", "Izaberite studijski program!");
            return;
        }

        PredmetFormController controller = mainView.openModalWithController(
                "predmetForm",
                "Novi predmet - " + selected.getNaziv(),
                700,
                600
        );

        if (controller != null) {
            controller.setStudijskiProgram(selected);
            controller.setOnSaveCallback(this::loadPrograms);
        }
    }

    @FXML
    public void handleNoviProgram() {
        mainView.openModal("studijskiProgramForm", "Novi studijski program", 600, 500);
        loadPrograms();
    }

    @FXML
    public void handleRefresh() {
        loadPrograms();
    }
}