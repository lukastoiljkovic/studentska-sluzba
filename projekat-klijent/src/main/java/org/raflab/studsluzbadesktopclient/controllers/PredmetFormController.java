package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzbadesktopclient.services.PredmetService;
import org.raflab.studsluzbadesktopclient.utils.AlertHelper;
import org.raflab.studsluzbadesktopclient.utils.ValidationHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PredmetFormController {

    private final PredmetService predmetService;

    private StudijskiProgramResponse studijskiProgram;
    private Runnable onSaveCallback;

    @FXML private Label programLabel;
    @FXML private TextField sifraTf;
    @FXML private TextField nazivTf;
    @FXML private TextArea opisTa;
    @FXML private TextField espbTf;
    @FXML private CheckBox obavezanCb;
    @FXML private TextField semestarTf;
    @FXML private TextField predavanjaTf;
    @FXML private TextField vezbeTf;

    @FXML private Label errorLabel;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    public void setStudijskiProgram(StudijskiProgramResponse program) {
        this.studijskiProgram = program;
        if (programLabel != null) {
            programLabel.setText(program.getNaziv());
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    public void initialize() {
        obavezanCb.setSelected(true);
    }

    @FXML
    public void handleSave() {
        if (!validate()) return;

        PredmetRequest req = new PredmetRequest();
        req.setSifra(sifraTf.getText().trim());
        req.setNaziv(nazivTf.getText().trim());
        req.setOpis(ValidationHelper.getTextOrNull(opisTa));
        req.setEspb(Integer.parseInt(espbTf.getText()));
        req.setObavezan(obavezanCb.isSelected());
        req.setSemestar(Integer.parseInt(semestarTf.getText()));
        req.setFondPredavanja(ValidationHelper.getIntegerOrNull(predavanjaTf));
        req.setFondVezbi(ValidationHelper.getIntegerOrNull(vezbeTf));
        req.setStudProgramId(studijskiProgram.getId());

        errorLabel.setText("Čuvanje u toku...");
        saveBtn.setDisable(true);

        predmetService.save(req)
                .subscribe(
                        predmetId -> Platform.runLater(() -> {
                            AlertHelper.showInfo("Uspeh", "Predmet je uspešno sačuvan! ID: " + predmetId);
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
        if (ValidationHelper.isEmpty(sifraTf)) {
            errorLabel.setText("Šifra je obavezna!");
            return false;
        }
        if (ValidationHelper.isEmpty(nazivTf)) {
            errorLabel.setText("Naziv je obavezan!");
            return false;
        }
        if (!ValidationHelper.isValidInteger(espbTf)) {
            errorLabel.setText("ESPB mora biti broj!");
            return false;
        }
        if (!ValidationHelper.isValidInteger(semestarTf)) {
            errorLabel.setText("Semestar mora biti broj!");
            return false;
        }

        int semestar = Integer.parseInt(semestarTf.getText());
        if (semestar < 1 || semestar > studijskiProgram.getTrajanjeSemestara()) {
            errorLabel.setText("Semestar mora biti između 1 i " + studijskiProgram.getTrajanjeSemestara());
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