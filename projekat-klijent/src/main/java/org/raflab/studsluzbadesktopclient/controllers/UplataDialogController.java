package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.raflab.studsluzbadesktopclient.utils.AlertHelper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class UplataDialogController {

    private final StudentService studentService;

    private Long studentId;
    private Runnable onSaveCallback;

    @FXML private TextField iznosTf;
    @FXML private DatePicker datumDp;
    @FXML private TextArea napomenaTa;

    @FXML private Label errorLabel;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    public void initialize() {
        datumDp.setValue(LocalDate.now());
        iznosTf.setText("0.00");
    }

    @FXML
    public void handleSave() {
        if (!validate()) return;

        Double iznos = Double.parseDouble(iznosTf.getText());

        errorLabel.setText("Čuvanje u toku...");
        saveBtn.setDisable(true);

        studentService.dodajUplatu(studentId, iznos)
                .doOnSuccess(unused -> Platform.runLater(() -> {
                    AlertHelper.showInfo("Uspeh", "Uplata je uspešno evidentirana!");
                    if (onSaveCallback != null) onSaveCallback.run();
                    closeDialog();
                }))
                .doOnError(throwable -> Platform.runLater(() -> {
                    AlertHelper.showException("Greška pri dodavanju uplate", (Exception) throwable);
                    errorLabel.setText("Greška pri čuvanju");
                    saveBtn.setDisable(false);
                }))
                .subscribe();


    }

    private boolean validate() {
        try {
            Double iznos = Double.parseDouble(iznosTf.getText());
            if (iznos <= 0) {
                errorLabel.setText("Iznos mora biti veći od 0!");
                return false;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Iznos mora biti broj!");
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