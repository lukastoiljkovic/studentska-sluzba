package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzbadesktopclient.app.MainView;
import org.raflab.studsluzbadesktopclient.dtos.IspitZDTO;
import org.raflab.studsluzbadesktopclient.dtos.PolozeniIspitiDTO;
import org.raflab.studsluzbadesktopclient.dtos.StudentProfileDTO;
import org.raflab.studsluzbadesktopclient.dtos.UverenjeOStudiranjuDTO;
import org.raflab.studsluzbadesktopclient.reports.ReportService;
import org.raflab.studsluzbadesktopclient.services.*;
import org.raflab.studsluzbadesktopclient.utils.AlertHelper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StudentProfileController {

    private final StudentService studentService;
    private final IspitService ispitService;
    private final UpisGodineService upisGodineService;
    private final SifarniciService sifarniciService;
    private final MainView mainView;
    private final UplataService uplataService;
    private final NavigationHistoryService historyService;

    private Long currentStudentIndeksId;
    private StudentProfileDTO currentProfile;

    // OSNOVNI PODACI
    @FXML
    private Label imeLabel;
    @FXML
    private Label prezimeLabel;
    @FXML
    private Label indeksLabel;
    @FXML
    private Label jmbgLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label studProgramLabel;
    @FXML
    private Label espbLabel;
    @FXML
    private Label prosekLabel;
    @FXML
    private Label statusLabel;

    // TAB PANE
    @FXML
    private TabPane tabPane;

    // TAB: LIČNI PODACI
    @FXML
    private Tab licniPodaciTab;
    @FXML
    private TextField imeTf;
    @FXML
    private TextField prezimeTf;
    @FXML
    private TextField srednjeImeTf;
    @FXML
    private TextField jmbgTf;
    @FXML
    private DatePicker datumRodjenjaDp;
    @FXML
    private TextField mestoRodjenjaTf;
    @FXML
    private TextField drzavaRodjenjaTf;
    @FXML
    private TextField adresaTf;
    @FXML
    private TextField telefonTf;
    @FXML
    private TextField emailPrivatniTf;
    @FXML
    private TextField emailFakultetskiTf;
    @FXML
    private TextField srednjaSkolaTf;

    // TAB: POLOŽENI ISPITI
    @FXML
    private Tab polozeniTab;
    @FXML
    private TableView<PolozenPredmetResponse> polozeniTable;
    @FXML
    private TableColumn<PolozenPredmetResponse, String> polPredmetCol;
    @FXML
    private TableColumn<PolozenPredmetResponse, Integer> polOcenaCol;
    @FXML
    private TableColumn<PolozenPredmetResponse, Integer> polEspbCol;
    @FXML
    private TableColumn<PolozenPredmetResponse, String> polDatumCol;
    @FXML
    private Pagination polozeniPagination;

    // TAB: NEPOLOŽENI ISPITI
    @FXML
    private Tab nepolozeniTab;
    @FXML
    private TableView<NepolozenPredmetResponse> nepolozeniTable;
    @FXML
    private TableColumn<NepolozenPredmetResponse, String> nepolPredmetCol;
    @FXML
    private TableColumn<NepolozenPredmetResponse, Integer> nepolEspbCol;
    @FXML
    private TableColumn<NepolozenPredmetResponse, Integer> nepolIzlasciCol;
    @FXML
    private Pagination nepolozeniPagination;

    // TAB: UPISANE GODINE
    @FXML
    private Tab upisaneGodineTab;
    @FXML
    private TableView<UpisGodineResponse> upisaneGodineTable;
    @FXML
    private TableColumn<UpisGodineResponse, String> ugSkolskaGodinaCol;
    @FXML
    private TableColumn<UpisGodineResponse, Integer> ugGodinaStudijaCol;
    @FXML
    private TableColumn<UpisGodineResponse, String> ugDatumCol;
    @FXML
    private TableColumn<UpisGodineResponse, String> ugNapomenaCol;

    // TAB: UPLATE
    @FXML
    private Tab uplateTab;
    @FXML
    private TableView<UplataResponse> uplateTable;
    @FXML
    private TableColumn<UplataResponse, String> uplDatumCol;
    @FXML
    private TableColumn<UplataResponse, Double> uplIznosEURCol;
    @FXML
    private TableColumn<UplataResponse, Double> uplIznosRSDCol;
    @FXML
    private TableColumn<UplataResponse, Double> uplKursCol;
    @FXML
    private Label preostaloEURLabel;
    @FXML
    private Label preostaloRSDLabel;

    // AKCIONI DUGMIĆI
    @FXML
    private Button upisGodineBtn;
    @FXML
    private Button dodajUplatuBtn;
    @FXML
    private Button refreshBtn;
    @FXML
    private Button uverenjeOStudiranjuBtn;
    @FXML
    private Button polozeniIspitiBtn;
    @FXML
    private Button prijaviIspitBtn;
    @FXML
    private Button closeBtn;

    private final ReportService reportService;


    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public void setStudentIndeksId(Long studentIndeksId) {
        this.currentStudentIndeksId = studentIndeksId;
        loadStudentProfile();
    }

    @FXML
    public void initialize() {
        setupTables();
        setupPagination();
    }

    private void setupTables() {
        // ===== POLOŽENI ISPITI =====
        polPredmetCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getPredmetNaziv())
        );
        polOcenaCol.setCellValueFactory(d ->
                new SimpleObjectProperty<>(d.getValue().getOcena())
        );
        polEspbCol.setCellValueFactory(d ->
                new SimpleObjectProperty<>(d.getValue().getEspb())
        );
        polDatumCol.setCellValueFactory(d -> {
            if (d.getValue().getDatumPolaganja() != null) {
                return new SimpleStringProperty(
                        d.getValue().getDatumPolaganja().format(DATE_FORMATTER)
                );
            }
            return new SimpleStringProperty("");
        });

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
        ugDatumCol.setCellValueFactory(d -> {
            if (d.getValue().getDatum() != null) {
                return new SimpleStringProperty(
                        d.getValue().getDatum().format(DATE_FORMATTER)
                );
            }
            return new SimpleStringProperty("");
        });
        ugNapomenaCol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNapomena())
        );

        // ===== UPLATE =====
        uplDatumCol.setCellValueFactory(d -> {
            if (d.getValue().getDatum() != null) {
                return new SimpleStringProperty(
                        d.getValue().getDatum().format(DATE_FORMATTER)
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
        uplKursCol.setCellValueFactory(d ->
                new SimpleObjectProperty<>(d.getValue().getKurs())
        );
    }

    private void setupPagination() {
        // Položeni ispiti paginacija
        polozeniPagination.setPageFactory(pageIndex -> {
            loadPolozeniPage(pageIndex);
            return new Label(); // prazan node jer table već prikazuje podatke
        });

        // Nepoloženi ispiti paginacija
        nepolozeniPagination.setPageFactory(pageIndex -> {
            loadNepolozeniPage(pageIndex);
            return new Label();
        });
    }

    private void loadStudentProfile() {
        if (currentStudentIndeksId == null) return;

        statusLabel.setText("Učitavanje profila...");

        studentService.getStudentProfile(currentStudentIndeksId)
                .subscribe(
                        profile -> Platform.runLater(() -> {
                            this.currentProfile = profile;
                            displayProfile(profile);
                            loadPolozeniPage(0);
                            loadNepolozeniPage(0);
                            loadUpisaneGodine();
                            loadUplate();
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
    }

    private void loadPolozeniPage(int pageIndex) {
        if (currentStudentIndeksId == null) return;

        studentService.getPolozeniIspiti(currentStudentIndeksId, pageIndex, 10)
                .subscribe(
                        page -> Platform.runLater(() -> {
                            polozeniTable.setItems(
                                    FXCollections.observableArrayList(page.getContent())
                            );
                            polozeniPagination.setPageCount(page.getTotalPages());
                        }),
                        error -> Platform.runLater(() ->
                                AlertHelper.showException("Greška", (Exception) error))
                );
    }

    private void loadNepolozeniPage(int pageIndex) {
        if (currentStudentIndeksId == null) return;

        studentService.getNepolozeniIspiti(currentStudentIndeksId, pageIndex, 10)
                .subscribe(
                        page -> Platform.runLater(() -> {
                            nepolozeniTable.setItems(
                                    FXCollections.observableArrayList(page.getContent())
                            );
                            nepolozeniPagination.setPageCount(page.getTotalPages());
                        }),
                        error -> Platform.runLater(() ->
                                AlertHelper.showException("Greška", (Exception) error))
                );
    }

    private void loadUpisaneGodine() {
        if (currentProfile == null) return;

        upisGodineService.list(currentStudentIndeksId, null)
                .collectList()
                .subscribe(
                        upisi -> Platform.runLater(() -> {
                            upisaneGodineTable.setItems(FXCollections.observableArrayList(upisi));
                            currentProfile.setUpisaneGodine(upisi);
                            loadUplate();
                        }),
                        error -> Platform.runLater(() ->
                                AlertHelper.showException("Greška", (Exception) error))
                );
    }


    private void loadUplate() {
        if (currentProfile == null) return;

        if (currentProfile.getUpisaneGodine() == null || currentProfile.getUpisaneGodine().isEmpty()) {
            uplateTable.setItems(FXCollections.observableArrayList());
            return;
        }

        Long upisGodineId = currentProfile.getUpisaneGodine()
                .get(currentProfile.getUpisaneGodine().size() - 1)
                .getId();

        uplataService.getUplateZaUpisGodine(upisGodineId)
                .collectList()
                .subscribe(
                        uplate -> Platform.runLater(() -> {
                            currentProfile.setUplate(uplate);
                            uplateTable.setItems(FXCollections.observableArrayList(uplate));
                        }),
                        error -> Platform.runLater(() ->
                                AlertHelper.showException("Greška pri učitavanju uplata", (Exception) error))
                );

        // preostali iznos ostaje isto
        studentService.getPreostaliIznos(upisGodineId)
                .subscribe(
                        mapa -> Platform.runLater(() -> {
                            preostaloEURLabel.setText(String.format("Preostalo EUR: %.2f", mapa.get("eur")));
                            preostaloRSDLabel.setText(String.format("Preostalo RSD: %.2f", mapa.get("rsd")));
                        }),
                        error -> Platform.runLater(() -> {
                            preostaloEURLabel.setText("Greška pri učitavanju");
                            preostaloRSDLabel.setText("");
                        })
                );
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
            controller.setOnSaveCallback(this::loadStudentProfile);
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
            controller.setOnSaveCallback(this::loadStudentProfile);
        }
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
            controller.setOnSaveCallback(this::loadStudentProfile);
        }
    }

    @FXML
    public void handleClose() {
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleUverenjeOStudiranju() {
        if (currentProfile == null) {
            AlertHelper.showWarning("Greška", "Nema učitanog profila studenta");
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
            String danas = sdf.format(new Date());

            UverenjeOStudiranjuDTO dto = new UverenjeOStudiranjuDTO(
                    currentProfile.getIme(),
                    currentProfile.getPrezime(),
                    currentProfile.getJmbg(),
                    currentProfile.getIndeksFormatirano(),
                    currentProfile.getStudProgramNaziv(),
                    currentProfile.getProsecnaOcena(),
                    currentProfile.getOstvarenoEspb(),
                    danas  // datumIzdavanja
            );

            System.out.println("📄 DTO za Uverenje o studiranju:");
            System.out.println("   Ime: " + dto.getIme());
            System.out.println("   Prezime: " + dto.getPrezime());
            System.out.println("   JMBG: " + dto.getJmbg());
            System.out.println("   Indeks: " + dto.getIndeksFormatirano());
            System.out.println("   Program: " + dto.getStudProgramNaziv());
            System.out.println("   Prosek: " + dto.getProsecnaOcena());
            System.out.println("   ESPB: " + dto.getOstvarenoEspb());
            System.out.println("   Datum izdavanja: " + dto.getDatumIzdavanja());

            reportService.generateAndOpenUverenjeOStudiranju(dto);
            AlertHelper.showInfo("Uspeh", "Uverenje je uspešno generisano!");

        } catch (Exception e) {
            e.printStackTrace(); // vidi stack trace u konzoli
            AlertHelper.showException("Greška pri generisanju uverenja", e);
        }
    }

    @FXML
    public void handlePolozeniIspiti() {
        if (currentProfile == null || currentStudentIndeksId == null) {
            AlertHelper.showWarning("Greška", "Nema učitanog profila studenta");
            return;
        }

        polozeniIspitiBtn.setDisable(true);
        polozeniIspitiBtn.setText("Generisanje...");

        // Učitaj SVE položene ispite (ne samo prvu stranicu)
        studentService.getPolozeniIspiti(currentStudentIndeksId, 0, 1000)
                .subscribe(
                        page -> Platform.runLater(() -> {
                            try {
                                List<IspitZDTO> ispiti = page.getContent().stream()
                                        .map(p -> new IspitZDTO(
                                                p.getPredmetNaziv(),
                                                p.getOcena(),
                                                p.getEspb(),
                                                p.getDatumPolaganja() != null ?
                                                        p.getDatumPolaganja().format(DATE_FORMATTER) : "",
                                                p.getSemestar()
                                        ))
                                        .collect(java.util.stream.Collectors.toList());

                                if (!ispiti.isEmpty()) {
                                    IspitZDTO prvi = ispiti.get(0);
                                    System.out.println("   Prvi ispit u listi:");
                                    System.out.println("   Predmet: " + prvi.getPredmetNaziv());
                                    System.out.println("   Ocena: " + prvi.getOcena());
                                    System.out.println("   ESPB: " + prvi.getEspb());
                                    System.out.println("   Datum: " + prvi.getDatumPolaganja());
                                    System.out.println("   Semestar: " + prvi.getSemestar());
                                }

                                PolozeniIspitiDTO dto = new PolozeniIspitiDTO(
                                        currentProfile.getIme(),
                                        currentProfile.getPrezime(),
                                        currentProfile.getIndeksFormatirano(),
                                        currentProfile.getProsecnaOcena(),
                                        currentProfile.getOstvarenoEspb(),
                                        ispiti
                                );

                                System.out.println("  Ukupno ispita za izveštaj: " + ispiti.size());

                                reportService.generateAndOpenPolozeniIspiti(dto);
                                AlertHelper.showInfo("Uspeh", "Izveštaj je uspešno generisan!");

                            } catch (Exception e) {
                                e.printStackTrace();
                                AlertHelper.showException("Greška pri generisanju izveštaja", e);
                            } finally {
                                polozeniIspitiBtn.setDisable(false);
                                polozeniIspitiBtn.setText("Položeni ispiti");
                            }
                        }),
                        error -> Platform.runLater(() -> {
                            error.printStackTrace();
                            AlertHelper.showException("Greška pri učitavanju ispita", (Exception) error);
                            polozeniIspitiBtn.setDisable(false);
                            polozeniIspitiBtn.setText("Položeni ispiti");
                        })
                );
    }

    /**
     * Helper metoda za izračunavanje godine studija
     */
    private Integer calculateGodinaStudija(Integer espb) {
        if (espb == null || espb == 0) return 1;

        // Pretpostavka: 60 ESPB po godini
        int godina = (espb / 60) + 1;
        return Math.min(godina, 4); // max 4. godina
    }
}