package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzbadesktopclient.services.*;
import org.raflab.studsluzbadesktopclient.utils.AlertHelper;
import org.raflab.studsluzbadesktopclient.utils.ValidationHelper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
@RequiredArgsConstructor
public class IspitFormController {

    private final IspitService ispitService;
    private final IspitniRokService ispitniRokService;
    private final SifarniciService sifarniciService;
    private final PredmetService predmetService;
    private final NastavnikService nastavnikService;

    private Runnable onSaveCallback;

    @FXML private ComboBox<IspitniRokResponse> ispitniRokCb;
    @FXML private ComboBox<PredmetResponse> predmetCb;
    @FXML private ComboBox<NastavnikResponse> nastavnikCb;
    @FXML private DatePicker datumDp;
    @FXML private TextField vremeTf; // HH:mm
    @FXML private CheckBox zakljucenCb;

    @FXML private Label errorLabel;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    public void initialize() {
        loadIspitniRokovi();
        loadPredmeti();
        loadNastavnici();

        datumDp.setValue(LocalDate.now());
        vremeTf.setText("09:00");
        zakljucenCb.setSelected(false);
    }

    private void loadIspitniRokovi() {
        // TODO: dodaj servis metodu za ispitne rokove
        // Za sada možeš kreirati dummy listu ili učitati sa servera
        AlertHelper.showInfo("Info", "Učitavanje ispitnih rokova nije implementirano");
    }

    private void loadPredmeti() {
        predmetService.getAll()
                .collectList()
                .subscribe(
                        predmeti -> Platform.runLater(() ->
                                predmetCb.setItems(FXCollections.observableArrayList(predmeti))
                        ),
                        error -> Platform.runLater(() ->
                                AlertHelper.showException("Greška", (Exception) error))
                );
    }

    private void loadNastavnici() {
        nastavnikService.getAll()
                .collectList()
                .subscribe(
                        nastavnici -> Platform.runLater(() ->
                                nastavnikCb.setItems(FXCollections.observableArrayList(nastavnici))
                        ),
                        error -> Platform.runLater(() ->
                                AlertHelper.showException("Greška", (Exception) error))
                );
    }

    @FXML
    public void handleSave() {
        if (!validate()) return;

        IspitRequest req = new IspitRequest();
        req.setIspitniRokId(ispitniRokCb.getValue().getId());
        req.setPredmetId(predmetCb.getValue().getId());
        req.setNastavnikId(nastavnikCb.getValue().getId());
        req.setZakljucen(zakljucenCb.isSelected());

        // Kombinuj datum i vreme
        LocalDate datum = datumDp.getValue();
        LocalTime vreme = LocalTime.parse(vremeTf.getText());
        req.setDatumVremePocetka(LocalDateTime.of(datum, vreme));

        errorLabel.setText("Čuvanje u toku...");
        saveBtn.setDisable(true);

        ispitService.save(req)
                .subscribe(
                        ispitId -> Platform.runLater(() -> {
                            AlertHelper.showInfo("Uspeh", "Ispit je uspešno kreiran! ID: " + ispitId);
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
        if (!ValidationHelper.isSelected(ispitniRokCb)) {
            errorLabel.setText("Izaberite ispitni rok!");
            return false;
        }
        if (!ValidationHelper.isSelected(predmetCb)) {
            errorLabel.setText("Izaberite predmet!");
            return false;
        }
        if (!ValidationHelper.isSelected(nastavnikCb)) {
            errorLabel.setText("Izaberite nastavnika!");
            return false;
        }
        if (!ValidationHelper.isSelected(datumDp)) {
            errorLabel.setText("Izaberite datum!");
            return false;
        }
        if (ValidationHelper.isEmpty(vremeTf)) {
            errorLabel.setText("Unesite vreme (HH:mm)!");
            return false;
        }

        // Validacija formata vremena
        try {
            LocalTime.parse(vremeTf.getText());
        } catch (Exception e) {
            errorLabel.setText("Vreme mora biti u formatu HH:mm!");
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