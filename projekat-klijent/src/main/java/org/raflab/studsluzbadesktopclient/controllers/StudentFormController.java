package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzbadesktopclient.app.MainView;
import org.raflab.studsluzbadesktopclient.services.SifarniciService;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.raflab.studsluzbadesktopclient.utils.AlertHelper;
import org.raflab.studsluzbadesktopclient.utils.ValidationHelper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class StudentFormController {

    private final StudentService studentService;
    private final SifarniciService sifarniciService;
    private final MainView mainView;

    // LIČNI PODACI
    @FXML private TextField imeTf;
    @FXML private TextField prezimeTf;
    @FXML private TextField srednjeImeTf;
    @FXML private RadioButton muskiRb;
    @FXML private RadioButton zenskiRb;
    @FXML private ToggleGroup polGroup;

    @FXML private TextField jmbgTf;
    @FXML private DatePicker datumRodjenjaDp;
    @FXML private TextField mestoRodjenjaTf;
    @FXML private TextField drzavaRodjenjaTf;
    @FXML private TextField drzavljanstvoTf;
    @FXML private TextField nacionalnostTf;

    // KONTAKT
    @FXML private TextField mestoPrebivalistaTf;
    @FXML private TextField adresaPrebivalistaTf;
    @FXML private TextField brojTelefonaMobilniTf;
    @FXML private TextField brojTelefonaFiksniTf;
    @FXML private TextField emailFakultetskiTf;
    @FXML private TextField emailPrivatniTf;

    // DOKUMENTI
    @FXML private TextField brojLicneKarteTf;
    @FXML private TextField licnuKartuIzdaoTf;

    // OBRAZOVANJE
    @FXML private ComboBox<SrednjaSkolaResponse> srednjaSkolaCb;
    @FXML private TextField uspehSrednjaSkolaTf;
    @FXML private TextField uspehPrijemniTf;

    // INDEKS
    @FXML private ComboBox<StudijskiProgramResponse> studProgramCb;
    @FXML private TextField godinaUpisaTf;
    @FXML private ComboBox<String> nacinFinansiranjaCb;

    @FXML private Label errorLabel;
    @FXML private Button saveBtn;

    @FXML
    public void initialize() {
        setupValidation();
        loadSifarnici();
        setupDefaultValues();
    }

    private void setupValidation() {
        jmbgTf.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.matches("\\d*")) {
                jmbgTf.setText(oldVal);
            }
            if (newVal != null && newVal.length() > 13) {
                jmbgTf.setText(newVal.substring(0, 13));
            }
        });

        emailFakultetskiTf.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && !ValidationHelper.isEmpty(emailFakultetskiTf)) {
                if (!ValidationHelper.isValidEmail(emailFakultetskiTf.getText())) {
                    errorLabel.setText("Email fakulteta nije validan!");
                } else {
                    errorLabel.setText("");
                }
            }
        });
    }

    private void loadSifarnici() {
        sifarniciService.getAllSrednjeSkole()
                .collectList()
                .subscribe(
                        skole -> Platform.runLater(() ->
                                srednjaSkolaCb.setItems(FXCollections.observableArrayList(skole))
                        ),
                        error -> AlertHelper.showException("Greška pri učitavanju srednjih škola", (Exception) error)
                );

        sifarniciService.getAllStudijskiProgrami()
                .collectList()
                .subscribe(
                        programi -> Platform.runLater(() ->
                                studProgramCb.setItems(FXCollections.observableArrayList(programi))
                        ),
                        error -> AlertHelper.showException("Greška pri učitavanju studijskih programa", (Exception) error)
                );

        nacinFinansiranjaCb.setItems(FXCollections.observableArrayList(
                "Budžet", "Samofinansiranje"
        ));
    }

    private void setupDefaultValues() {
        drzavaRodjenjaTf.setText("Srbija");
        drzavljanstvoTf.setText("Srbija");
        godinaUpisaTf.setText(String.valueOf(LocalDate.now().getYear()));
        nacinFinansiranjaCb.getSelectionModel().select("Budžet");
        muskiRb.setSelected(true);
    }

    @FXML
    public void handleSave() {
        String validationError = validateForm();
        if (validationError != null) {
            errorLabel.setText(validationError);
            AlertHelper.showWarning("Validacija", validationError);
            return;
        }

        // ✅ KORISTI NOVI REQUEST DTO
        StudentPodaciCreateRequest podaciReq = createStudentPodaciRequest();

        errorLabel.setText("Čuvanje u toku...");
        saveBtn.setDisable(true);

        studentService.saveStudentPodaci(podaciReq)
                .subscribe(
                        studentId -> {
                            StudentIndeksRequest indeksReq = createStudentIndeksDTO(studentId);
                            studentService.saveIndeks(indeksReq)
                                    .subscribe(
                                            indeksId -> Platform.runLater(() -> {
                                                AlertHelper.showInfo("Uspeh",
                                                        "Student je uspešno sačuvan!\nID: " + studentId +
                                                                "\nIndeks ID: " + indeksId);
                                                handleClear();
                                                errorLabel.setText("");
                                                saveBtn.setDisable(false);
                                            }),
                                            error -> Platform.runLater(() -> {
                                                AlertHelper.showException("Greška pri čuvanju indeksa", (Exception) error);
                                                errorLabel.setText("Greška pri čuvanju indeksa");
                                                saveBtn.setDisable(false);
                                            })
                                    );
                        },
                        error -> Platform.runLater(() -> {
                            AlertHelper.showException("Greška pri čuvanju", (Exception) error);
                            errorLabel.setText("Greška pri čuvanju");
                            saveBtn.setDisable(false);
                        })
                );
    }

    private String validateForm() {
        String error = ValidationHelper.validateStudentForm(
                imeTf, prezimeTf, jmbgTf, emailFakultetskiTf
        );
        if (error != null) return error;

        if (!ValidationHelper.isSelected(datumRodjenjaDp)) {
            return "Datum rođenja je obavezan!";
        }
        if (!ValidationHelper.isSelected(studProgramCb)) {
            return "Izaberite studijski program!";
        }
        if (!ValidationHelper.isValidInteger(godinaUpisaTf)) {
            return "Godina upisa mora biti broj!";
        }

        return null;
    }

    // ✅ NOVA METODA - koristi StudentPodaciCreateRequest
    private StudentPodaciCreateRequest createStudentPodaciRequest() {
        StudentPodaciCreateRequest req = new StudentPodaciCreateRequest();

        req.setIme(imeTf.getText().trim());
        req.setPrezime(prezimeTf.getText().trim());
        req.setSrednjeIme(ValidationHelper.getTextOrNull(srednjeImeTf));
        req.setPol(muskiRb.isSelected() ? 'M' : 'Ž');

        req.setJmbg(jmbgTf.getText().trim());
        req.setDatumRodjenja(datumRodjenjaDp.getValue());
        req.setMestoRodjenja(ValidationHelper.getTextOrNull(mestoRodjenjaTf));
        req.setDrzavaRodjenja(ValidationHelper.getTextOrNull(drzavaRodjenjaTf));
        req.setDrzavljanstvo(ValidationHelper.getTextOrNull(drzavljanstvoTf));
        req.setNacionalnost(ValidationHelper.getTextOrNull(nacionalnostTf));

        req.setMestoPrebivalista(ValidationHelper.getTextOrNull(mestoPrebivalistaTf));
        req.setAdresaPrebivalista(ValidationHelper.getTextOrNull(adresaPrebivalistaTf));
        req.setBrojTelefonaMobilni(ValidationHelper.getTextOrNull(brojTelefonaMobilniTf));
        req.setBrojTelefonaFiksni(ValidationHelper.getTextOrNull(brojTelefonaFiksniTf));

        req.setEmailFakultetski(emailFakultetskiTf.getText().trim());
        req.setEmailPrivatni(ValidationHelper.getTextOrNull(emailPrivatniTf));

        req.setBrojLicneKarte(ValidationHelper.getTextOrNull(brojLicneKarteTf));
        req.setLicnuKartuIzdao(ValidationHelper.getTextOrNull(licnuKartuIzdaoTf));

        SrednjaSkolaResponse skola = ValidationHelper.getSelectedOrNull(srednjaSkolaCb);
        if (skola != null) {
            req.setSrednjaSkola(skola.getNaziv());  // ✅ samo String
        }
        req.setUspehSrednjaSkola(ValidationHelper.getDoubleOrNull(uspehSrednjaSkolaTf));
        req.setUspehPrijemni(ValidationHelper.getDoubleOrNull(uspehPrijemniTf));

        return req;
    }

    private StudentIndeksRequest createStudentIndeksDTO(Long studentId) {
        StudentIndeksRequest dto = new StudentIndeksRequest();

        StudijskiProgramResponse program = studProgramCb.getSelectionModel().getSelectedItem();
        dto.setStudProgramOznaka(program.getOznaka());
        dto.setGodina(Integer.parseInt(godinaUpisaTf.getText()));
        dto.setNacinFinansiranja(nacinFinansiranjaCb.getSelectionModel().getSelectedItem());
        dto.setAktivan(true);
        dto.setVaziOd(LocalDate.now());
        dto.setStudentId(studentId);

        return dto;
    }

    @FXML
    public void handleClear() {
        imeTf.clear();
        prezimeTf.clear();
        srednjeImeTf.clear();
        jmbgTf.clear();
        datumRodjenjaDp.setValue(null);
        mestoRodjenjaTf.clear();
        drzavaRodjenjaTf.setText("Srbija");
        drzavljanstvoTf.setText("Srbija");
        nacionalnostTf.clear();

        mestoPrebivalistaTf.clear();
        adresaPrebivalistaTf.clear();
        brojTelefonaMobilniTf.clear();
        brojTelefonaFiksniTf.clear();
        emailFakultetskiTf.clear();
        emailPrivatniTf.clear();

        brojLicneKarteTf.clear();
        licnuKartuIzdaoTf.clear();

        srednjaSkolaCb.getSelectionModel().clearSelection();
        uspehSrednjaSkolaTf.clear();
        uspehPrijemniTf.clear();

        studProgramCb.getSelectionModel().clearSelection();
        godinaUpisaTf.setText(String.valueOf(LocalDate.now().getYear()));
        nacinFinansiranjaCb.getSelectionModel().select("Budžet");

        errorLabel.setText("");
        muskiRb.setSelected(true);
    }

    @FXML
    public void handleCancel() {
        mainView.changeRoot("studentSearch");
    }

    @FXML
    public void handleAddSrednjaSkola() {
        mainView.openModal("common/srednjaSkolaForm", "Dodaj srednju školu", 500, 400);
        loadSifarnici();
    }
}