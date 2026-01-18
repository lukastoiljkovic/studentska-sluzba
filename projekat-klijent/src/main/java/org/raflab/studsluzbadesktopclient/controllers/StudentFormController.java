package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzbadesktopclient.app.MainView;
import org.raflab.studsluzbadesktopclient.coder.CoderFactory;
import org.raflab.studsluzbadesktopclient.coder.CoderType;
import org.raflab.studsluzbadesktopclient.coder.SimpleCode;
import org.raflab.studsluzbadesktopclient.services.SifarniciService;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.raflab.studsluzbadesktopclient.utils.AlertHelper;
import org.raflab.studsluzbadesktopclient.utils.ValidationHelper;
import org.raflab.studsluzbadesktopclient.app.GlobalExceptionHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class StudentFormController {

    private final StudentService studentService;
    private final SifarniciService sifarniciService;
    private final MainView mainView;
    private final CoderFactory coderFactory;

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
    @FXML private ComboBox<SimpleCode> srednjaSkolaCb;
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
        System.out.println("✅ StudentFormController initialized!");
        setupValidation();
        loadSifarnici();
        setupDefaultValues();
        setupComboBoxConverters();
    }

    private void setupValidation() {
        // JMBG - samo cifre, max 13
        jmbgTf.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.matches("\\d*")) {
                jmbgTf.setText(oldVal);
            }
            if (newVal != null && newVal.length() > 13) {
                jmbgTf.setText(newVal.substring(0, 13));
            }
        });

        // Email validacija
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

    private void setupComboBoxConverters() {
        // StringConverter za StudijskiProgram
        studProgramCb.setConverter(new StringConverter<StudijskiProgramResponse>() {
            @Override
            public String toString(StudijskiProgramResponse program) {
                if (program == null) return "";
                return program.getOznaka() + " - " + program.getNaziv();
            }

            @Override
            public StudijskiProgramResponse fromString(String string) {
                return null;
            }
        });
    }

    private void loadSifarnici() {
        System.out.println("📚 Loading šifarnici...");

        // Srednje škole iz CODER-a
        var srednjeSkoleCoder = coderFactory.getSimpleCoder(CoderType.SREDNJA_SKOLA);
        if (srednjeSkoleCoder != null) {
            srednjaSkolaCb.setItems(FXCollections.observableArrayList(srednjeSkoleCoder.getCodes()));
            System.out.println("✅ Loaded " + srednjeSkoleCoder.getCodes().size() + " srednjih škola");
        }

        // Studijski programi sa servera
        GlobalExceptionHandler.wrapWithErrorHandling(
                sifarniciService.getAllStudijskiProgrami().collectList()
        ).subscribe(
                programi -> Platform.runLater(() -> {
                    studProgramCb.setItems(FXCollections.observableArrayList(programi));
                    System.out.println("✅ Loaded " + programi.size() + " studijskih programa");
                }),
                error -> {
                    System.err.println("❌ Failed to load studijski programi: " + error.getMessage());
                    AlertHelper.showException("Greška pri učitavanju studijskih programa", (Exception) error);
                }
        );

        // Načini finansiranja
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
        System.out.println("💾 Saving student...");

        String validationError = validateForm();
        if (validationError != null) {
            errorLabel.setText(validationError);
            AlertHelper.showWarning("Validacija", validationError);
            return;
        }

        StudentPodaciResponse podaci = createStudentPodaciDTO();

        errorLabel.setText("Čuvanje u toku...");
        saveBtn.setDisable(true);

        GlobalExceptionHandler.wrapWithErrorHandling(
                studentService.saveStudentPodaci(podaci)
                        .flatMap(studentId -> {
                            System.out.println("✅ Student saved with ID: " + studentId);
                            StudentIndeksRequest indeks = createStudentIndeksDTO(studentId);
                            return studentService.saveIndeks(indeks)
                                    .map(indeksId -> new SaveResult(studentId, indeksId));
                        })
        ).subscribe(
                result -> Platform.runLater(() -> {
                    System.out.println("✅ Complete! StudentID=" + result.studentId + ", IndeksID=" + result.indeksId);
                    AlertHelper.showInfo("Uspeh",
                            "Student je uspešno sačuvan!\n\n" +
                                    "Student ID: " + result.studentId + "\n" +
                                    "Indeks ID: " + result.indeksId);
                    handleClear();
                    errorLabel.setText("");
                    saveBtn.setDisable(false);
                }),
                error -> Platform.runLater(() -> {
                    System.err.println("❌ Save failed: " + error.getMessage());
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

    private StudentPodaciResponse createStudentPodaciDTO() {
        StudentPodaciResponse dto = new StudentPodaciResponse();

        dto.setIme(imeTf.getText().trim());
        dto.setPrezime(prezimeTf.getText().trim());
        dto.setSrednjeIme(ValidationHelper.getTextOrNull(srednjeImeTf));
        dto.setPol(muskiRb.isSelected() ? 'M' : 'Ž');

        dto.setJmbg(jmbgTf.getText().trim());
        dto.setDatumRodjenja(datumRodjenjaDp.getValue());
        dto.setMestoRodjenja(ValidationHelper.getTextOrNull(mestoRodjenjaTf));
        dto.setDrzavaRodjenja(ValidationHelper.getTextOrNull(drzavaRodjenjaTf));
        dto.setDrzavljanstvo(ValidationHelper.getTextOrNull(drzavljanstvoTf));
        dto.setNacionalnost(ValidationHelper.getTextOrNull(nacionalnostTf));

        dto.setMestoPrebivalista(ValidationHelper.getTextOrNull(mestoPrebivalistaTf));
        dto.setAdresaPrebivalista(ValidationHelper.getTextOrNull(adresaPrebivalistaTf));
        dto.setBrojTelefonaMobilni(ValidationHelper.getTextOrNull(brojTelefonaMobilniTf));
        dto.setBrojTelefonaFiksni(ValidationHelper.getTextOrNull(brojTelefonaFiksniTf));

        dto.setEmailFakultetski(emailFakultetskiTf.getText().trim());
        dto.setEmailPrivatni(ValidationHelper.getTextOrNull(emailPrivatniTf));

        dto.setBrojLicneKarte(ValidationHelper.getTextOrNull(brojLicneKarteTf));
        dto.setLicnuKartuIzdao(ValidationHelper.getTextOrNull(licnuKartuIzdaoTf));

        SimpleCode skola = ValidationHelper.getSelectedOrNull(srednjaSkolaCb);
        if (skola != null) {
            dto.setSrednjaSkola(skola.getCode());
        }
        dto.setUspehSrednjaSkola(ValidationHelper.getDoubleOrNull(uspehSrednjaSkolaTf));
        dto.setUspehPrijemni(ValidationHelper.getDoubleOrNull(uspehPrijemniTf));

        return dto;
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
        System.out.println("❌ Cancelled - returning to search");
        mainView.changeRoot("studentSearch");
    }

    private static class SaveResult {
        final Long studentId;
        final Long indeksId;
        SaveResult(Long studentId, Long indeksId) {
            this.studentId = studentId;
            this.indeksId = indeksId;
        }
    }
}
