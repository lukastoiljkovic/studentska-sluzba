package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzbadesktopclient.services.IspitService;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.raflab.studsluzbadesktopclient.utils.AlertHelper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class IspitPrijavljeniController {

    private final IspitService ispitService;
    private final StudentService studentService;

    private IspitResponse ispit;

    // Cache za imena studenata da izbegnemo beskonačne pozive
    private final Map<Long, String> studentImenaCache = new HashMap<>();

    @FXML private Label ispitLabel;
    @FXML private TableView<PrijavaDTO> prijaveTable;
    @FXML private TableColumn<PrijavaDTO, String> indeksCol;
    @FXML private TableColumn<PrijavaDTO, String> imeCol;
    @FXML private TableColumn<PrijavaDTO, String> datumPrijaveCol;
    @FXML private TableColumn<PrijavaDTO, String> izlazakCol;

    @FXML private Label ukupnoLabel;
    @FXML private Button refreshBtn;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public void setIspit(IspitResponse ispit) {
        this.ispit = ispit;
        initializeData();
    }

    @FXML
    public void initialize() {
        setupTable();
    }

    private void setupTable() {
        indeksCol.setCellValueFactory(new PropertyValueFactory<>("indeks"));
        imeCol.setCellValueFactory(new PropertyValueFactory<>("imePrezime"));
        datumPrijaveCol.setCellValueFactory(new PropertyValueFactory<>("datumPrijave"));
        izlazakCol.setCellValueFactory(new PropertyValueFactory<>("izlazak"));
    }

    private void initializeData() {
        if (ispit == null) return;

        ispitLabel.setText(String.format("%s - %s",
                ispit.getPredmetNaziv(),
                ispit.getIspitniRokNaziv()));

        loadPrijavljeni();
    }

    private void loadPrijavljeni() {
        studentImenaCache.clear(); // Očisti cache

        ispitService.getPrijavljeni(ispit.getId())
                .collectList()
                .flatMap(prijave -> {
                    // Prvo učitaj sve studente odjednom
                    List<Mono<StudentIndeksResponse>> studentMonos = prijave.stream()
                            .map(p -> studentService.getStudentIndeksById(p.getStudentIndeksId()))
                            .toList();

                    return Flux.fromIterable(studentMonos)
                            .flatMap(mono -> mono.onErrorResume(e -> Mono.empty()))
                            .collectList()
                            .map(studenti -> {
                                // Napravi mapu studentIndeksId -> ime
                                Map<Long, String> imenaMap = new HashMap<>();
                                for (StudentIndeksResponse s : studenti) {
                                    if (s != null && s.getStudent() != null) {
                                        String ime = s.getStudent().getIme() + " " + s.getStudent().getPrezime();
                                        imenaMap.put(s.getId(), ime);
                                    }
                                }

                                // Konvertuj u DTO
                                return prijave.stream()
                                        .map(p -> {
                                            PrijavaDTO dto = new PrijavaDTO();
                                            dto.setIndeks(String.format("%s%d/%02d",
                                                    p.getStudProgramOznaka(),
                                                    p.getIndeksBroj(),
                                                    p.getIndeksGodina() % 100));
                                            dto.setImePrezime(imenaMap.getOrDefault(p.getStudentIndeksId(), "N/A"));
                                            dto.setDatumPrijave(p.getDatum() != null ?
                                                    p.getDatum().format(DATE_FORMATTER) : "");
                                            dto.setIzlazak(p.getIspitIzlazakId() != null ? "Da" : "Ne");
                                            return dto;
                                        })
                                        .toList();
                            });
                })
                .subscribe(
                        dtos -> Platform.runLater(() -> {
                            prijaveTable.setItems(FXCollections.observableArrayList(dtos));
                            ukupnoLabel.setText("Ukupno: " + dtos.size());
                        }),
                        error -> Platform.runLater(() ->
                                AlertHelper.showException("Greška pri učitavanju", (Exception) error))
                );
    }

    @FXML
    public void handleRefresh() {
        loadPrijavljeni();
    }

    // Inner class za TableView
    public static class PrijavaDTO {
        private String indeks;
        private String imePrezime;
        private String datumPrijave;
        private String izlazak;

        public String getIndeks() { return indeks; }
        public void setIndeks(String indeks) { this.indeks = indeks; }

        public String getImePrezime() { return imePrezime; }
        public void setImePrezime(String imePrezime) { this.imePrezime = imePrezime; }

        public String getDatumPrijave() { return datumPrijave; }
        public void setDatumPrijave(String datumPrijave) { this.datumPrijave = datumPrijave; }

        public String getIzlazak() { return izlazak; }
        public void setIzlazak(String izlazak) { this.izlazak = izlazak; }
    }
}