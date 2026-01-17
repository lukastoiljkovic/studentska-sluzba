package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzbadesktopclient.services.*;
import org.raflab.studsluzbadesktopclient.utils.AlertHelper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ObnovaGodineDialogController {

    private final ObnovaGodineService obnovaGodineService;
    private final SifarniciService sifarniciService;
    private final PredmetService predmetService;

    private org.raflab.studsluzbadesktopclient.dtos.StudentProfileDTO studentProfile;
    private Runnable onSaveCallback;

    @FXML private Label studentLabel;
    @FXML private Label indeksLabel;
    @FXML private Label espbLabel;

    @FXML private ComboBox<SkolskaGodinaResponse> skolskaGodinaCb;
    @FXML private ComboBox<Integer> godinaStudijaCb;
    @FXML private DatePicker datumDp;
    @FXML private TextArea napomenaTa;

    @FXML private ListView<PredmetResponse> nepolozeniListView;
    @FXML private ListView<PredmetResponse> dostupniPredmetiListView;
    @FXML private ListView<PredmetResponse> odabraniPredmetiListView;

    @FXML private Button dodajBtn;
    @FXML private Button ukloniBtn;
    @FXML private Label ukupnoEspbLabel;

    @FXML private Label errorLabel;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    private List<PredmetResponse> nepolozeniPredmeti;
    private ObservableList<PredmetResponse> dostupniPredmeti = FXCollections.observableArrayList();
    private ObservableList<PredmetResponse> odabraniPredmeti = FXCollections.observableArrayList();

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

        nepolozeniListView.setItems(FXCollections.observableArrayList());
        dostupniPredmetiListView.setItems(dostupniPredmeti);
        odabraniPredmetiListView.setItems(odabraniPredmeti);

        dostupniPredmetiListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        odabraniPredmetiListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        godinaStudijaCb.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadPredmetiZaNarednaGodina(newVal + 1);
            }
        });

        odabraniPredmeti.addListener((javafx.collections.ListChangeListener<PredmetResponse>) c -> {
            racunajUkupnoEspb();
        });
    }

    private void initializeData() {
        if (studentProfile == null) return;

        studentLabel.setText(studentProfile.getIme() + " " + studentProfile.getPrezime());
        indeksLabel.setText(studentProfile.getIndeksFormatirano());
        espbLabel.setText("Ostvareno ESPB: " + studentProfile.getOstvarenoEspb());

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
                                AlertHelper.showException("Greška pri učitavanju", (Exception) error))
                );

        loadNepolozeniPredmeti();
    }

    private void loadNepolozeniPredmeti() {
        if (studentProfile == null || studentProfile.getNepolozeniPredmeti() == null) return;

        nepolozeniPredmeti = studentProfile.getNepolozeniPredmeti().stream()
                .map(np -> {
                    PredmetResponse p = new PredmetResponse();
                    p.setId(np.getPredmetId());
                    p.setSifra(np.getPredmetSifra());
                    p.setNaziv(np.getPredmetNaziv());
                    p.setEspb(np.getEspb());
                    p.setSemestar(np.getSemestar());
                    return p;
                })
                .collect(Collectors.toList());

        nepolozeniListView.setItems(FXCollections.observableArrayList(nepolozeniPredmeti));
        racunajUkupnoEspb();
    }

    private void loadPredmetiZaNarednaGodina(Integer narednaGodina) {
        if (studentProfile == null || studentProfile.getStudProgramId() == null) return;

        Set<Long> nepolozeniIds = nepolozeniPredmeti.stream()
                .map(PredmetResponse::getId)
                .collect(Collectors.toSet());

        predmetService.getPredmetiNaStudijskomProgramu(studentProfile.getStudProgramId())
                .subscribe(
                        predmeti -> Platform.runLater(() -> {
                            List<PredmetResponse> filtered = predmeti.stream()
                                    .filter(p -> p.getSemestar() != null &&
                                            ((p.getSemestar() + 1) / 2) == narednaGodina)
                                    .filter(p -> !nepolozeniIds.contains(p.getId()))
                                    .collect(Collectors.toList());
                            dostupniPredmeti.setAll(filtered);
                        }),
                        error -> Platform.runLater(() ->
                                AlertHelper.showException("Greška pri učitavanju predmeta", (Exception) error))
                );
    }

    @FXML
    public void handleDodaj() {
        List<PredmetResponse> selected = dostupniPredmetiListView.getSelectionModel().getSelectedItems();
        if (selected.isEmpty()) return;

        int trenutnoEspb = racunajTrenutnoEspb();
        int dodajEspb = selected.stream().mapToInt(PredmetResponse::getEspb).sum();

        if (trenutnoEspb + dodajEspb > 60) {
            AlertHelper.showWarning("Prekoračenje ESPB",
                    "Maksimalni broj ESPB bodova je 60. Trenutno: " + trenutnoEspb +
                            ", pokušaj dodavanja: " + dodajEspb);
            return;
        }

        odabraniPredmeti.addAll(selected);
        dostupniPredmeti.removeAll(selected);
    }

    @FXML
    public void handleUkloni() {
        List<PredmetResponse> selected = odabraniPredmetiListView.getSelectionModel().getSelectedItems();
        if (selected.isEmpty()) return;

        dostupniPredmeti.addAll(selected);
        odabraniPredmeti.removeAll(selected);
    }

    private int racunajTrenutnoEspb() {
        int nepolozeniEspb = nepolozeniPredmeti.stream()
                .mapToInt(PredmetResponse::getEspb)
                .sum();
        int odabraniEspb = odabraniPredmeti.stream()
                .mapToInt(PredmetResponse::getEspb)
                .sum();
        return nepolozeniEspb + odabraniEspb;
    }

    private void racunajUkupnoEspb() {
        int ukupno = racunajTrenutnoEspb();
        ukupnoEspbLabel.setText("Ukupno ESPB: " + ukupno + " / 60");

        if (ukupno > 60) {
            ukupnoEspbLabel.setStyle("-fx-text-fill: red;");
        } else {
            ukupnoEspbLabel.setStyle("-fx-text-fill: green;");
        }
    }

    @FXML
    public void handleSave() {
        if (!validate()) return;

        Set<Long> nepolozeniIds = nepolozeniPredmeti.stream()
                .map(PredmetResponse::getId)
                .collect(Collectors.toSet());

        Set<Long> odabraniIds = odabraniPredmeti.stream()
                .map(PredmetResponse::getId)
                .collect(Collectors.toSet());

        errorLabel.setText("Čuvanje u toku...");
        saveBtn.setDisable(true);

        obnovaGodineService.addObnovaZaStudenta(
                studentProfile.getStudentIndeksId(),
                skolskaGodinaCb.getValue().getId(),
                godinaStudijaCb.getValue(),
                napomenaTa.getText(),
                nepolozeniIds,
                odabraniIds,
                datumDp.getValue()
        ).subscribe(
                result -> Platform.runLater(() -> {
                    AlertHelper.showInfo("Uspeh", "Obnova godine je uspešno sačuvana!");
                    if (onSaveCallback != null) {
                        onSaveCallback.run();
                    }
                    closeDialog();
                }),
                error -> Platform.runLater(() -> {
                    AlertHelper.showException("Greška pri obnovi godine", (Exception) error);
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

        int ukupno = racunajTrenutnoEspb();
        if (ukupno > 60) {
            errorLabel.setText("Ukupan broj ESPB ne sme biti veći od 60!");
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