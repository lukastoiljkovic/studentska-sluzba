package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzbadesktopclient.services.IspitService;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.raflab.studsluzbadesktopclient.utils.AlertHelper;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class IspitPrijavljeniController {

    private final IspitService ispitService;
    private final StudentService studentService;

    private IspitResponse ispit;

    @FXML private Label ispitLabel;
    @FXML private TableView<IspitPrijavaResponse> prijaveTable;
    @FXML private TableColumn<IspitPrijavaResponse, String> indeksCol;
    @FXML private TableColumn<IspitPrijavaResponse, String> imeCol;
    @FXML private TableColumn<IspitPrijavaResponse, String> datumPrijaveCol;
    @FXML private TableColumn<IspitPrijavaResponse, String> izlazakCol;

    @FXML private Label ukupnoLabel;
    @FXML private Button refreshBtn;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public void setIspit(IspitResponse ispit) {
        this.ispit = ispit;
        initializeData();
    }

    @FXML
    public void initialize() {
        setupTable();
    }

    private void setupTable() {
        indeksCol.setCellValueFactory(data -> {
            IspitPrijavaResponse p = data.getValue();
            String indeks = String.format("%s%d/%02d",
                    p.getStudProgramOznaka(),
                    p.getIndeksBroj(),
                    p.getIndeksGodina() % 100);
            return new javafx.beans.property.SimpleStringProperty(indeks);
        });

        imeCol.setCellValueFactory(data -> {
            // Učitavamo podatke o studentu asinhrono
            IspitPrijavaResponse p = data.getValue();
            javafx.beans.property.SimpleStringProperty prop =
                    new javafx.beans.property.SimpleStringProperty("Učitavanje...");

            // Asinhrono dohvatanje imena
            studentService.getStudentIndeksById(p.getStudentIndeksId())
                    .subscribe(
                            indeks -> {
                                if (indeks != null && indeks.getStudent() != null) {
                                    String imePrezime = indeks.getStudent().getIme() + " " +
                                            indeks.getStudent().getPrezime();
                                    Platform.runLater(() -> prop.setValue(imePrezime));
                                }
                            },
                            error -> Platform.runLater(() -> prop.setValue("N/A"))
                    );

            return prop;
        });

        datumPrijaveCol.setCellValueFactory(data -> {
            if (data.getValue().getDatum() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDatum().format(DATE_FORMATTER)
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        izlazakCol.setCellValueFactory(data -> {
            Long izlazakId = data.getValue().getIspitIzlazakId();
            return new javafx.beans.property.SimpleStringProperty(
                    izlazakId != null ? "Da" : "Ne"
            );
        });
    }

    private void initializeData() {
        if (ispit == null) return;

        ispitLabel.setText(String.format("%s - %s",
                ispit.getPredmetNaziv(),
                ispit.getIspitniRokNaziv()));

        loadPrijavljeni();
    }

    private void loadPrijavljeni() {
        ispitService.getPrijavljeni(ispit.getId())
                .collectList()
                .subscribe(
                        prijave -> Platform.runLater(() -> {
                            prijaveTable.setItems(FXCollections.observableArrayList(prijave));
                            ukupnoLabel.setText("Ukupno: " + prijave.size());
                        }),
                        error -> Platform.runLater(() ->
                                AlertHelper.showException("Greška pri učitavanju", (Exception) error))
                );
    }

    @FXML
    public void handleRefresh() {
        loadPrijavljeni();
    }
}