package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzbadesktopclient.app.MainView;
import org.raflab.studsluzbadesktopclient.dtos.StudentProfileDTO;
import org.raflab.studsluzbadesktopclient.services.*;
import org.raflab.studsluzbadesktopclient.utils.AlertHelper;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentService studentService;
    private final IspitService ispitService;
    private final UpisGodineService upisGodineService;
    private final SifarniciService sifarniciService;
    private final MainView mainView;

    private Long currentStudentIndeksId;
    private StudentProfileDTO currentProfile;

    // OSNOVNI PODACI
    @FXML private Label imeLabel;
    @FXML private Label prezimeLabel;
    @FXML private Label indeksLabel;
    @FXML private Label jmbgLabel;
    @FXML private Label emailLabel;
    @FXML private Label studProgramLabel;
    @FXML private Label espbLabel;
    @FXML private Label prosekLabel;
    @FXML private Label statusLabel;

    // TAB PANE
    @FXML private TabPane tabPane;

    // TAB: LIČNI PODACI
    @FXML private Tab licniPodaciTab;
    @FXML private TextField imeTf;
    @FXML private TextField prezimeTf;
    @FXML private TextField srednjeImeTf;
    @FXML private TextField jmbgTf;
    @FXML private DatePicker datumRodjenjaDp;
    @FXML private TextField mestoRodjenjaTf;
    @FXML private TextField drzavaRodjenjaTf;
    @FXML private TextField adresaTf;
    @FXML private TextField telefonTf;
    @FXML private TextField emailPrivatniTf;
    @FXML private TextField emailFakultetskiTf;
    @FXML private TextField srednjaSkolaTf;

    // TAB: POLOŽENI ISPITI
    @FXML private Tab polozeniTab;
    @FXML private TableView<PolozenPredmetResponse> polozeniTable;
    @FXML private TableColumn<PolozenPredmetResponse, String> polPredmetCol;
    @FXML private TableColumn<PolozenPredmetResponse, Integer> polOcenaCol;
    @FXML private TableColumn<PolozenPredmetResponse, String> polDatumCol;
    @FXML private TableColumn<PolozenPredmetResponse, Integer> polEspbCol;

    // TAB: NEPOLOŽENI ISPITI
    @FXML private Tab nepolozeniTab;
    @FXML private TableView<NepolozenPredmetResponse> nepolozeniTable;
    @FXML private TableColumn<NepolozenPredmetResponse, String> nepolPredmetCol;
    @FXML private TableColumn<NepolozenPredmetResponse, Integer> nepolEspbCol;
    @FXML private TableColumn<NepolozenPredmetResponse, Integer> nepolIzlasciCol;

    // TAB: UPISANE GODINE
    @FXML private Tab upisaneGodineTab;
    @FXML private TableView<UpisGodineResponse> upisaneGodineTable;
    @FXML private TableColumn<UpisGodineResponse, String> ugSkolskaGodinaCol;
    @FXML private TableColumn<UpisGodineResponse, Integer> ugGodinaStudijaCol;

    // TAB: OBNOVE GODINE
    @FXML private Tab obnoveTab;
    @FXML private TableView<ObnovaGodineResponse> obnoveTable;
    @FXML private TableColumn<ObnovaGodineResponse, String> obSkolskaGodinaCol;
    @FXML private TableColumn<ObnovaGodineResponse, Integer> obGodinaStudijaCol;
    @FXML private TableColumn<ObnovaGodineResponse, String> obDatumCol;
    @FXML private TableColumn<ObnovaGodineResponse, String> obNapomenaCol;

    // TAB: UPLATE
    @FXML private Tab uplateTab;
    @FXML private TableView<UplataResponse> uplateTable;
    @FXML private TableColumn<UplataResponse, String> uplDatumCol;
    @FXML private TableColumn<UplataResponse, Double> uplIznosEURCol;
    @FXML private TableColumn<UplataResponse, Double> uplIznosRSDCol;

    // AKCIONI DUGMIĆI
    @FXML private Button upisGodineBtn;
    @FXML private Button obnovaGodineBtn;
    @FXML private Button dodajUplatuBtn;
    @FXML private Button refreshBtn;
    @FXML private Button prijaviIspitBtn;
    @FXML private Button closeBtn;


    public void setStudentIndeksId(Long studentIndeksId) {
        this.currentStudentIndeksId = studentIndeksId;
        loadStudentProfile();
    }

    @FXML
    public void initialize() {
        setupTables();
    }

    private void setupTables() {
        // ===== POLOŽENI ISPITI =====
        polPredmetCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getPredmetNaziv())
        );
        polOcenaCol.setCellValueFactory(d ->
                new SimpleObjectProperty<>(d.getValue().getOcena())
        );
        polDatumCol.setCellValueFactory(d -> {
            if (d.getValue().getDatumPolaganja() != null) {
                return new SimpleStringProperty(
                        d.getValue().getDatumPolaganja()
                                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                );
            }
            return new SimpleStringProperty("");
        });
        polEspbCol.setCellValueFactory(d ->
                new SimpleObjectProperty<>(d.getValue().getEspb())
        );

        // ===== NEPOLOŽENI ISPITI =====
        nepolPredmetCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getPredmetNaziv())
        );
        nepolEspbCol.setCellValueFactory(d ->
                new SimpleObjectProperty<>(d.getValue().getEspb())
        );
        nepolIzlasciCol.setCellValueFactory(d ->
                new SimpleObjectProperty<>(d.getValue().getBrojIzlazaka())
        );

        // ===== UPISANE GODINE =====
        ugSkolskaGodinaCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getSkolskaGodinaNaziv())
        );
        ugGodinaStudijaCol.setCellValueFactory(d ->
                new SimpleObjectProperty<>(d.getValue().getGodinaStudija())
        );

        // ===== OBNOVE =====
        obSkolskaGodinaCol.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getSkolskaGodinaId() == null ? "" : d.getValue().getSkolskaGodinaId().toString()
                )
        );

        obGodinaStudijaCol.setCellValueFactory(d ->
                new SimpleObjectProperty<>(d.getValue().getGodinaStudija())
        );
        obDatumCol.setCellValueFactory(d -> {
            if (d.getValue().getDatum() != null) {
                return new SimpleStringProperty(
                        d.getValue().getDatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                );
            }
            return new SimpleStringProperty("");
        });
        obNapomenaCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNapomena())
        );

        // ===== UPLATE =====
        uplDatumCol.setCellValueFactory(d -> {
            if (d.getValue().getDatum() != null) {
                return new SimpleStringProperty(
                        d.getValue().getDatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                );
            }
            return new SimpleStringProperty("");
        });
        uplIznosEURCol.setCellValueFactory(d ->
                new SimpleObjectProperty<>(d.getValue().getIznosEUR())
        );
        uplIznosRSDCol.setCellValueFactory(d ->
                new SimpleObjectProperty<>(d.getValue().getIznosRSD())
        );
    }


    private void loadStudentProfile() {
        if (currentStudentIndeksId == null) return;

        statusLabel.setText("Učitavanje profila...");

        studentService.getStudentProfile(currentStudentIndeksId)
                .subscribe(
                        profile -> Platform.runLater(() -> {
                            this.currentProfile = profile;
                            displayProfile(profile);
                            statusLabel.setText("Profil učitan");
                        }),
                        error -> Platform.runLater(() -> {
                            AlertHelper.showException("Greška pri učitavanju profila", (Exception) error);
                            statusLabel.setText("Greška");
                        })
                );
    }

    private void displayProfile(StudentProfileDTO profile) {
        // HEADER - osnovni podaci
        imeLabel.setText(profile.getIme());
        prezimeLabel.setText(profile.getPrezime());
        indeksLabel.setText(profile.getIndeksFormatirano());
        jmbgLabel.setText(profile.getJmbg());
        emailLabel.setText(profile.getEmailFakultetski());
        studProgramLabel.setText(profile.getStudProgramNaziv());
        espbLabel.setText(String.valueOf(profile.getOstvarenoEspb()));
        prosekLabel.setText(String.format("%.2f", profile.getProsecnaOcena()));

        // TAB: LIČNI PODACI
        imeTf.setText(profile.getIme());
        prezimeTf.setText(profile.getPrezime());
        srednjeImeTf.setText(profile.getSrednjeIme());
        jmbgTf.setText(profile.getJmbg());
        datumRodjenjaDp.setValue(profile.getDatumRodjenja());
        mestoRodjenjaTf.setText(profile.getMestoRodjenja());
        drzavaRodjenjaTf.setText(profile.getDrzavaRodjenja());
        adresaTf.setText(profile.getAdresaPrebivalista());
        telefonTf.setText(profile.getBrojTelefonaMobilni());
        emailPrivatniTf.setText(profile.getEmailPrivatni());
        emailFakultetskiTf.setText(profile.getEmailFakultetski());
        srednjaSkolaTf.setText(profile.getSrednjaSkola());

        // TAB: POLOŽENI
        polozeniTable.setItems(FXCollections.observableArrayList(
                profile.getPolozeniPredmeti() != null ? profile.getPolozeniPredmeti() : List.of()
        ));

        // TAB: NEPOLOŽENI
        nepolozeniTable.setItems(FXCollections.observableArrayList(
                profile.getNepolozeniPredmeti() != null ? profile.getNepolozeniPredmeti() : List.of()
        ));

        // TAB: UPISANE GODINE
        upisaneGodineTable.setItems(FXCollections.observableArrayList(
                profile.getUpisaneGodine() != null ? profile.getUpisaneGodine() : List.of()
        ));

        // TAB: OBNOVE
        obnoveTable.setItems(FXCollections.observableArrayList(
                profile.getObnoveGodine() != null ? profile.getObnoveGodine() : List.of()
        ));

        // TAB: UPLATE
        uplateTable.setItems(FXCollections.observableArrayList(
                profile.getUplate() != null ? profile.getUplate() : List.of()
        ));
    }

    @FXML
    public void handleRefresh() {
        loadStudentProfile();
    }

    @FXML
    public void handleUpisGodine() {
        if (currentProfile == null) return;

        UpisGodineDialogController controller = mainView.openModalWithController(
                "student/upisGodineDialog",
                "Upis Godine",
                700,
                500
        );

        if (controller != null) {
            controller.setStudentIndeks(currentProfile);
            controller.setOnSaveCallback(() -> loadStudentProfile());
        }
    }

    @FXML
    public void handleObnovaGodine() {
        if (currentProfile == null) return;

        ObnovaGodineDialogController controller = mainView.openModalWithController(
                "student/obnovaGodineDialog",
                "Obnova Godine",
                800,
                600
        );

        if (controller != null) {
            controller.setStudentIndeks(currentProfile);
            controller.setOnSaveCallback(() -> loadStudentProfile());
        }
    }

    @FXML
    public void handleDodajUplatu() {
        if (currentProfile == null || currentProfile.getStudentId() == null) {
            AlertHelper.showWarning("Greška", "Nema učitanog profila studenta");
            return;
        }

        UplataDialogController controller = mainView.openModalWithController(
                "student/uplataDialog",
                "Dodaj Uplatu",
                500,
                400
        );

        if (controller != null) {
            controller.setStudentId(currentProfile.getStudentId());
            controller.setOnSaveCallback(() -> loadStudentProfile());
        }
    }

    @FXML
    public void handleClose() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }


    @FXML
    public void handlePrijaviIspit() {
        if (currentProfile == null || currentProfile.getStudentIndeksId() == null) {
            AlertHelper.showWarning("Greška", "Nema učitanog profila studenta");
            return;
        }

        IspitPrijavaDialogController controller = mainView.openModalWithController(
                "ispitPrijavaDialog",
                "Prijava na ispit",
                600,
                400
        );

        if (controller != null) {
            controller.setStudentIndeksId(currentProfile.getStudentIndeksId());
            controller.setOnSaveCallback(() -> loadStudentProfile());
        }
    }
}