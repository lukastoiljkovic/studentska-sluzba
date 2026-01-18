package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzbadesktopclient.services.IspitService;
import org.raflab.studsluzbadesktopclient.services.PolozenPredmetService;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.raflab.studsluzbadesktopclient.utils.AlertHelper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IspitPrijavaDialogController {

    private final IspitService ispitService;
    private final StudentService studentService;
    private final PolozenPredmetService polozenPredmetService;

    private Long studentIndeksId;
    private Runnable onSaveCallback;

    @FXML private Label studentLabel;
    @FXML private ComboBox<IspitResponse> ispitCb;
    @FXML private DatePicker datumDp;
    @FXML private TextArea napomenaTa;

    @FXML private Label errorLabel;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public void setStudentIndeksId(Long studentIndeksId) {
        this.studentIndeksId = studentIndeksId;
        initializeData();
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    @FXML
    public void initialize() {
        datumDp.setValue(LocalDate.now());

        // Custom StringConverter za ispite
        ispitCb.setConverter(new StringConverter<IspitResponse>() {
            @Override
            public String toString(IspitResponse ispit) {
                if (ispit == null) return "";
                return String.format("%s - %s (%s)",
                        ispit.getPredmetNaziv(),
                        ispit.getIspitniRokNaziv(),
                        ispit.getDatumVremePocetka().format(FORMATTER));
            }

            @Override
            public IspitResponse fromString(String string) {
                return null;
            }
        });
    }

    private void initializeData() {
        if (studentIndeksId == null) return;

        // Učitaj ime studenta
        studentService.getStudentIndeksById(studentIndeksId)
                .subscribe(
                        indeks -> Platform.runLater(() -> {
                            if (indeks != null && indeks.getStudent() != null) {
                                studentLabel.setText(String.format("%s %s - %s%d/%02d",
                                        indeks.getStudent().getIme(),
                                        indeks.getStudent().getPrezime(),
                                        indeks.getStudProgramOznaka(),
                                        indeks.getBroj(),
                                        indeks.getGodina() % 100));
                            }
                        }),
                        error -> Platform.runLater(() ->
                                studentLabel.setText("Student ID: " + studentIndeksId))
                );

        // Učitaj sve AKTIVNE ispite koje student može da prijavi
        ispitService.getAll()
                .collectList()
                .subscribe(
                        ispiti -> {
                            // prvo dobavi polozene predmete
                            polozenPredmetService.getPolozeniIspiti(studentIndeksId, 0, 1000)
                                    .subscribe(
                                            polozeniPage -> Platform.runLater(() -> {
                                                // ekstraktuj id-eve polozenih predmeta
                                                java.util.Set<Long> polozeniPredmetiIds = polozeniPage.getContent()
                                                        .stream()
                                                        .map(PolozenPredmetResponse::getPredmetId)
                                                        .collect(Collectors.toSet());

                                                // filtriraj ispite:
                                                // 1. nije zakljucen
                                                // 2. predmet nije polozen
                                                var dostupni = ispiti.stream()
                                                        .filter(i -> !i.isZakljucen())
                                                        .filter(i -> !polozeniPredmetiIds.contains(i.getPredmetId()))
                                                        .sorted((a, b) ->
                                                                a.getDatumVremePocetka()
                                                                        .compareTo(b.getDatumVremePocetka()))
                                                        .collect(Collectors.toList());

                                                ispitCb.setItems(
                                                        FXCollections.observableArrayList(dostupni)
                                                );

                                                if (dostupni.isEmpty()) {
                                                    errorLabel.setText("Nema dostupnih ispita za prijavu!");
                                                } else {
                                                    errorLabel.setText("");
                                                }
                                            }),
                                            error -> Platform.runLater(() ->
                                                    AlertHelper.showException("Greška", (Exception) error))
                                    );
                        },
                        error -> Platform.runLater(() ->
                                AlertHelper.showException("Greška", (Exception) error))
                );
    }

    public void handleSave() {
        if (ispitCb.getValue() == null) {
            errorLabel.setText("Izaberite ispit!");
            return;
        }
        if (datumDp.getValue() == null) {
            errorLabel.setText("Izaberite datum!");
            return;
        }

        errorLabel.setText("Prijava u toku...");
        saveBtn.setDisable(true);

        ispitService.prijaviIspit(ispitCb.getValue().getId(), studentIndeksId)
                .subscribe(
                        response -> Platform.runLater(() -> {
                            AlertHelper.showInfo("Uspeh", "Student je uspešno prijavljen na ispit!");
                            if (onSaveCallback != null) {
                                onSaveCallback.run();
                            }
                            closeDialog();
                        }),
                        error -> Platform.runLater(() -> {
                            AlertHelper.showException("Greška pri prijavi", (Exception) error);
                            errorLabel.setText("Greška pri prijavi");
                            saveBtn.setDisable(false);
                        })
                );
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