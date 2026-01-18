package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzbadesktopclient.dtos.ZapisnikHeaderDTO;
import org.raflab.studsluzbadesktopclient.services.IspitService;
import org.raflab.studsluzbadesktopclient.reports.ReportService;
import org.raflab.studsluzbadesktopclient.utils.AlertHelper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IspitRezultatiController {

    private final IspitService ispitService;

    private IspitResponse ispit;

    @FXML private Label ispitLabel;
    @FXML private TableView<IspitRezultatResponse> rezultatiTable;
    @FXML private TableColumn<IspitRezultatResponse, String> indeksCol;
    @FXML private TableColumn<IspitRezultatResponse, String> imeCol;
    @FXML private TableColumn<IspitRezultatResponse, String> prezimeCol;
    @FXML private TableColumn<IspitRezultatResponse, Integer> predispitniCol;
    @FXML private TableColumn<IspitRezultatResponse, Integer> ispitniCol;
    @FXML private TableColumn<IspitRezultatResponse, Integer> ukupnoCol;

    @FXML private Label prosekLabel;
    @FXML private Label polozioLabel;
    @FXML private Button stampajBtn;
    @FXML private Button refreshBtn;

    private final ReportService reportService;

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
            IspitRezultatResponse r = data.getValue();
            String indeks = String.format("%s%d/%02d",
                    r.getStudProgramOznaka(),
                    r.getBrojIndeksa(),
                    r.getGodinaUpisa() % 100);
            return new javafx.beans.property.SimpleStringProperty(indeks);
        });

        imeCol.setCellValueFactory(new PropertyValueFactory<>("ime"));
        prezimeCol.setCellValueFactory(new PropertyValueFactory<>("prezime"));
        predispitniCol.setCellValueFactory(new PropertyValueFactory<>("predispitni"));
        ispitniCol.setCellValueFactory(new PropertyValueFactory<>("ispitni"));
        ukupnoCol.setCellValueFactory(new PropertyValueFactory<>("ukupno"));

        // Oboji ukupno: zeleno ako >= 51, crveno inače
        ukupnoCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    if (item >= 51) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: red;");
                    }
                }
            }
        });
    }

    private void initializeData() {
        if (ispit == null) return;

        ispitLabel.setText(String.format("%s - %s",
                ispit.getPredmetNaziv(),
                ispit.getIspitniRokNaziv()));

        loadRezultati();
        loadProsek();
    }

    private void loadRezultati() {
        ispitService.getRezultati(ispit.getId())
                .collectList()
                .subscribe(
                        rezultati -> Platform.runLater(() -> {
                            rezultatiTable.setItems(FXCollections.observableArrayList(rezultati));

                            long polozilo = rezultati.stream()
                                    .filter(r -> r.getUkupno() >= 51)
                                    .count();

                            polozioLabel.setText(String.format("Položilo: %d / %d",
                                    polozilo, rezultati.size()));
                        }),
                        error -> Platform.runLater(() ->
                                AlertHelper.showException("Greška pri učitavanju", (Exception) error))
                );
    }

    private void loadProsek() {
        ispitService.getProsecnaOcena(ispit.getId())
                .subscribe(
                        prosek -> Platform.runLater(() ->
                                prosekLabel.setText(String.format("Prosečna ocena: %.2f", prosek))
                        ),
                        error -> Platform.runLater(() ->
                                prosekLabel.setText("Prosečna ocena: N/A"))
                );
    }

    @FXML
    public void handleStampaj() {
        if (ispit == null) {
            AlertHelper.showWarning("Greška", "Nema učitanog ispita!");
            return;
        }

        // Prikaži loading indicator
        stampajBtn.setDisable(true);
        stampajBtn.setText("Generisanje...");

        // Učitaj header i rezultate
        ispitService.getZapisnikHeader(ispit.getId())
                .zipWith(
                        ispitService.getRezultati(ispit.getId()).collectList()
                )
                .subscribe(
                        tuple -> Platform.runLater(() -> {
                            try {
                                ZapisnikHeaderDTO header = tuple.getT1();
                                List<IspitRezultatResponse> rezultati = tuple.getT2();

                                // Generiši PDF
                                reportService.generateAndOpenZapisnik(header, rezultati);

                                AlertHelper.showInfo("Uspeh",
                                        "Zapisnik je uspešno generisan i otvoren!");

                            } catch (Exception e) {
                                AlertHelper.showException(
                                        "Greška pri generisanju zapisnika", e);
                            } finally {
                                stampajBtn.setDisable(false);
                                stampajBtn.setText("Štampaj");
                            }
                        }),
                        error -> Platform.runLater(() -> {
                            AlertHelper.showException(
                                    "Greška pri učitavanju podataka", (Exception) error);
                            stampajBtn.setDisable(false);
                            stampajBtn.setText("Štampaj");
                        })
                );
    }

    @FXML
    public void handleRefresh() {
        loadRezultati();
        loadProsek();
    }
}