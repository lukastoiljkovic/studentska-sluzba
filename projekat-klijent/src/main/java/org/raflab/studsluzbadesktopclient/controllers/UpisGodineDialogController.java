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
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class UpisGodineDialogController {

    private final UpisGodineService upisGodineService;
    private final SifarniciService sifarniciService;

    // VAŽNO: Koristi klijent DTO!
    private org.raflab.studsluzbadesktopclient.dtos.StudentProfileDTO studentProfile;
    private Runnable onSaveCallback;

    @FXML private Label studentLabel;
    @FXML private Label indeksLabel;

    @FXML private ComboBox<SkolskaGodinaResponse> skolskaGodinaCb;
    @FXML private ComboBox<Integer> godinaStudijaCb;
    @FXML private TextArea napomenaTa;
    @FXML private DatePicker datumDp;

    @FXML private Label errorLabel;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    public void setStudentIndeks(org.raflab.studsluzbadesktopclient.dtos.StudentProfileDTO profile) {
        this.studentProfile = profile;
        initializeData();
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    public void initialize() {
        godinaStudijaCb.setItems(FXCollections.observableArrayList(1, 2, 3, 4));
        datumDp.setValue(LocalDate.now());
    }

    private void initializeData() {
        if (studentProfile == null) return;

        studentLabel.setText(studentProfile.getIme() + " " + studentProfile.getPrezime());
        indeksLabel.setText(studentProfile.getIndeksFormatirano());

        // Učitaj školske godine
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
                                AlertHelper.showException("Greška pri učitavanju školskih godina", (Exception) error))
                );

        // Postavi default vrednosti
        godinaStudijaCb.getSelectionModel().select(0);
    }

    @FXML
    public void handleSave() {
        if (!validate()) return;

        UpisGodineRequest request = new UpisGodineRequest();
        request.setStudentIndeksId(studentProfile.getStudentIndeksId());
        request.setSkolskaGodinaId(skolskaGodinaCb.getValue().getId());
        request.setGodinaStudija(godinaStudijaCb.getValue());
        request.setNapomena(napomenaTa.getText());
        request.setDatum(datumDp.getValue());

        errorLabel.setText("Čuvanje u toku...");
        saveBtn.setDisable(true);

        upisGodineService.create(request)
                .subscribe(
                        result -> Platform.runLater(() -> {
                            AlertHelper.showInfo("Uspeh", "Upis godine je uspešno sačuvan!");
                            if (onSaveCallback != null) {
                                onSaveCallback.run();
                            }
                            closeDialog();
                        }),
                        error -> Platform.runLater(() -> {
                            AlertHelper.showException("Greška pri upisu godine", (Exception) error);
                            errorLabel.setText("Greška pri čuvanju");
                            saveBtn.setDisable(false);
                        })
                );
    }

    private boolean validate() {
        if (skolskaGodinaCb.getValue() == null) {
            errorLabel.setText("Izaberite školsku godinu!");
            return false;
        }
        if (godinaStudijaCb.getValue() == null) {
            errorLabel.setText("Izaberite godinu studija!");
            return false;
        }
        if (datumDp.getValue() == null) {
            errorLabel.setText("Izaberite datum!");
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