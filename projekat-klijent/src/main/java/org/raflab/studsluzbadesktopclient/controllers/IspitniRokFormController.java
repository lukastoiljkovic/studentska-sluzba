package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzbadesktopclient.services.IspitniRokService;
import org.raflab.studsluzbadesktopclient.services.SifarniciService;
import org.raflab.studsluzbadesktopclient.utils.AlertHelper;
import org.raflab.studsluzbadesktopclient.utils.ValidationHelper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class IspitniRokFormController {

    private final IspitniRokService ispitniRokService;
    private final SifarniciService sifarniciService;

    private Runnable onSaveCallback;

    @FXML private TextField nazivTf;
    @FXML private ComboBox<SkolskaGodinaResponse> skolskaGodinaCb;
    @FXML private DatePicker pocetakDatumDp;
    @FXML private TextField pocetakVremeTf;
    @FXML private DatePicker zavrsetakDatumDp;
    @FXML private TextField zavrsetakVremeTf;

    @FXML private Label errorLabel;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    public void initialize() {
        loadSkolskeGodine();
        pocetakDatumDp.setValue(LocalDate.now());
        zavrsetakDatumDp.setValue(LocalDate.now().plusDays(7));
        pocetakVremeTf.setText("00:00");
        zavrsetakVremeTf.setText("23:59");
    }

    private void loadSkolskeGodine() {
        sifarniciService.getAllSkolskeGodine()
                .collectList()
                .subscribe(
                        godine -> Platform.runLater(() -> {
                            skolskaGodinaCb.setItems(FXCollections.observableArrayList(godine));
                            godine.stream()
                                    .filter(SkolskaGodinaResponse::isAktivna)
                                    .findFirst()
                                    .ifPresent(skolskaGodinaCb.getSelectionModel()::select);
                        }),
                        error -> Platform.runLater(() ->
                                AlertHelper.showException("Greška", (Exception) error))
                );
    }

    @FXML
    public void handleSave() {
        if (!validate()) return;

        IspitniRokRequest req = new IspitniRokRequest();
        req.setNaziv(nazivTf.getText().trim());
        req.setSkolskaGodinaId(skolskaGodinaCb.getValue().getId());

        // Kombinuj datum i vreme za početak
        LocalDate pocetakDatum = pocetakDatumDp.getValue();
        LocalTime pocetakVreme = LocalTime.parse(pocetakVremeTf.getText());
        req.setDatumPocetka(LocalDateTime.of(pocetakDatum, pocetakVreme));

        // Kombinuj datum i vreme za završetak
        LocalDate zavrsetakDatum = zavrsetakDatumDp.getValue();
        LocalTime zavrsetakVreme = LocalTime.parse(zavrsetakVremeTf.getText());
        req.setDatumZavrsetka(LocalDateTime.of(zavrsetakDatum, zavrsetakVreme));

        errorLabel.setText("Čuvanje u toku...");
        saveBtn.setDisable(true);

        ispitniRokService.save(req)
                .subscribe(
                        rokId -> Platform.runLater(() -> {
                            AlertHelper.showInfo("Uspeh", "Ispitni rok je uspešno kreiran! ID: " + rokId);
                            if (onSaveCallback != null) {
                                onSaveCallback.run();
                            }
                            closeDialog();
                        }),
                        error -> Platform.runLater(() -> {
                            AlertHelper.showException("Greška pri čuvanju", (Exception) error);
                            errorLabel.setText("Greška pri čuvanju");
                            saveBtn.setDisable(false);
                        })
                );
    }

    private boolean validate() {
        if (ValidationHelper.isEmpty(nazivTf)) {
            errorLabel.setText("Naziv je obavezan!");
            return false;
        }
        if (!ValidationHelper.isSelected(skolskaGodinaCb)) {
            errorLabel.setText("Izaberite školsku godinu!");
            return false;
        }
        if (!ValidationHelper.isSelected(pocetakDatumDp)) {
            errorLabel.setText("Izaberite datum početka!");
            return false;
        }
        if (!ValidationHelper.isSelected(zavrsetakDatumDp)) {
            errorLabel.setText("Izaberite datum završetka!");
            return false;
        }

        // Validacija formata vremena
        try {
            LocalTime.parse(pocetakVremeTf.getText());
            LocalTime.parse(zavrsetakVremeTf.getText());
        } catch (Exception e) {
            errorLabel.setText("Vreme mora biti u formatu HH:mm!");
            return false;
        }

        // Provera da je završetak posle početka
        LocalDateTime pocetak = LocalDateTime.of(
                pocetakDatumDp.getValue(),
                LocalTime.parse(pocetakVremeTf.getText())
        );
        LocalDateTime zavrsetak = LocalDateTime.of(
                zavrsetakDatumDp.getValue(),
                LocalTime.parse(zavrsetakVremeTf.getText())
        );

        if (zavrsetak.isBefore(pocetak)) {
            errorLabel.setText("Datum završetka mora biti posle datuma početka!");
            return false;
        }

        errorLabel.setText("");
        return true;
    }

    @FXML
    public void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }
}