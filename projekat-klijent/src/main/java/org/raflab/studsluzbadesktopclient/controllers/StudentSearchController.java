package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzbadesktopclient.app.MainView;
import org.raflab.studsluzbadesktopclient.dto.StudentSearchResultDTO;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.raflab.studsluzbadesktopclient.utils.AlertHelper;
import org.raflab.studsluzbadesktopclient.utils.ValidationHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentSearchController {

    private final StudentService studentService;
    private final MainView mainView;

    @FXML private TextField imeTf;
    @FXML private TextField prezimeTf;
    @FXML private TextField studProgramTf;
    @FXML private TextField godinaTf;
    @FXML private TextField brojTf;
    @FXML private TextField fastSearchTf;

    @FXML private TableView<StudentSearchResultDTO> resultTable;
    @FXML private TableColumn<StudentSearchResultDTO, String> imeCol;
    @FXML private TableColumn<StudentSearchResultDTO, String> prezimeCol;
    @FXML private TableColumn<StudentSearchResultDTO, String> indeksCol;
    @FXML private TableColumn<StudentSearchResultDTO, Integer> godinaCol;
    @FXML private TableColumn<StudentSearchResultDTO, Boolean> aktivanCol;

    @FXML private Label statusLabel;
    @FXML private Pagination pagination;

    private int currentPage = 0;
    private final int pageSize = 20;

    @FXML
    public void initialize() {
        setupTable();
        setupFastSearch();
        setupDoubleClick();
        loadData();
    }

    private void setupTable() {
        imeCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getIme()));
        prezimeCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getPrezime()));
        indeksCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getIndeksFormatirano()));
        godinaCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getGodinaUpisa()));

        aktivanCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleBooleanProperty(data.getValue().isAktivanIndeks()));
        aktivanCol.setCellFactory(col -> new TableCell<StudentSearchResultDTO, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Da" : "Ne");
                    setStyle(item ? "-fx-text-fill: green;" : "-fx-text-fill: gray;");
                }
            }
        });
    }

    private void setupFastSearch() {
        fastSearchTf.setOnAction(e -> handleFastSearch());
    }

    private void setupDoubleClick() {
        resultTable.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                StudentSearchResultDTO selected = resultTable.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    openStudentProfile(selected.getIdIndeks());
                }
            }
        });
    }

    @FXML
    public void handleSearch() {
        String ime = ValidationHelper.getTextOrNull(imeTf);
        String prezime = ValidationHelper.getTextOrNull(prezimeTf);
        String studProgram = ValidationHelper.getTextOrNull(studProgramTf);
        Integer godina = ValidationHelper.getIntegerOrNull(godinaTf);
        Integer broj = ValidationHelper.getIntegerOrNull(brojTf);

        statusLabel.setText("Pretraga u toku...");

        studentService.searchSync(ime, prezime, studProgram, godina, broj, currentPage, pageSize)
                .subscribe(
                        results -> Platform.runLater(() -> {
                            resultTable.setItems(FXCollections.observableArrayList(results));
                            statusLabel.setText("Pronađeno: " + results.size() + " rezultata");
                        }),
                        error -> Platform.runLater(() -> {
                            AlertHelper.showException("Greška pri pretrazi", (Exception) error);
                            statusLabel.setText("Greška pri pretrazi");
                        })
                );
    }

    @FXML
    public void handleFastSearch() {
        String indeksShort = fastSearchTf.getText().trim();
        if (indeksShort.isEmpty()) {
            AlertHelper.showWarning("Upozorenje", "Unesite broj indeksa!");
            return;
        }

        statusLabel.setText("Pretraga...");

        studentService.fastSearch(indeksShort)
                .subscribe(
                        indeks -> Platform.runLater(() -> {
                            if (indeks != null && indeks.getId() != null) {
                                openStudentProfile(indeks.getId());
                            } else {
                                AlertHelper.showInfo("Rezultat", "Student nije pronađen");
                            }
                            statusLabel.setText("");
                        }),
                        error -> Platform.runLater(() -> {
                            AlertHelper.showError("Greška", "Student nije pronađen");
                            statusLabel.setText("");
                        })
                );
    }

    @FXML
    public void handleClear() {
        imeTf.clear();
        prezimeTf.clear();
        studProgramTf.clear();
        godinaTf.clear();
        brojTf.clear();
        fastSearchTf.clear();
        resultTable.getItems().clear();
        statusLabel.setText("");
    }

    @FXML
    public void handleOpenProfile() {
        StudentSearchResultDTO selected = resultTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Upozorenje", "Izaberite studenta iz tabele!");
            return;
        }
        openStudentProfile(selected.getIdIndeks());
    }

    @FXML
    public void handleNewStudent() {
        mainView.changeRoot("student/studentForm");
    }

    private void openStudentProfile(Long indeksId) {
        // TODO: Implementirati prikaz profila studenta
        mainView.openModal("student/studentProfile", "Profil studenta", 900, 700);
    }

    private void loadData() {
        handleSearch();
    }
}