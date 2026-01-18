package org.raflab.studsluzbadesktopclient.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import lombok.RequiredArgsConstructor;

import org.raflab.studsluzba.dtos.PageResponse;
import org.raflab.studsluzbadesktopclient.app.MainView;
import org.raflab.studsluzbadesktopclient.coder.CoderFactory;
import org.raflab.studsluzbadesktopclient.coder.CoderType;
import org.raflab.studsluzbadesktopclient.coder.SimpleCode;
import org.raflab.studsluzbadesktopclient.dtos.StudentDTO;
import org.raflab.studsluzbadesktopclient.services.StudentService;
import org.raflab.studsluzbadesktopclient.utils.AlertHelper;
import org.raflab.studsluzbadesktopclient.utils.ValidationHelper;
import org.raflab.studsluzbadesktopclient.app.GlobalExceptionHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class StudentSearchController {

    private final CoderFactory coderFactory;
    private final StudentService studentService;
    private final MainView mainView;

    @FXML private TextField imeTf;
    @FXML private TextField prezimeTf;
    @FXML private TextField fastSearchTf;
    @FXML private ComboBox<SimpleCode> srednjaSkolaCb;

    @FXML private TableView<StudentDTO> resultTable;
    @FXML private TableColumn<StudentDTO, String> imeCol;
    @FXML private TableColumn<StudentDTO, String> prezimeCol;
    @FXML private TableColumn<StudentDTO, String> indeksCol;
    @FXML private TableColumn<StudentDTO, Integer> godinaCol;
    @FXML private TableColumn<StudentDTO, Boolean> aktivanCol;

    @FXML private Label statusLabel;
    @FXML private Pagination pagination;

    private int currentPage = 0;
    private static final int PAGE_SIZE = 10;
    private SearchType lastSearchType = SearchType.NONE;

    private enum SearchType {
        NONE, BY_NAME, BY_SCHOOL
    }

    @FXML
    public void initialize() {
        setupTable();
        setupFastSearch();
        setupDoubleClick();
        setupPagination();
        loadSrednjeSkole();
        pagination.setVisible(false);
    }

    private void setupTable() {
        imeCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getIme()));
        prezimeCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getPrezime()));
        indeksCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getStudProgramOznaka()
                                + d.getValue().getBroj()
                                + "/" + (d.getValue().getGodinaUpisa() % 100)
                )
        );
        godinaCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleObjectProperty<>(d.getValue().getGodinaUpisa()));
        aktivanCol.setCellValueFactory(d ->
                new javafx.beans.property.SimpleBooleanProperty(d.getValue().isAktivanIndeks()));
        aktivanCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : (item ? "Da" : "Ne"));
            }
        });
    }

    private void setupPagination() {
        pagination.setPageFactory(pageIndex -> {
            currentPage = pageIndex;
            performCurrentSearch(pageIndex);
            return new javafx.scene.layout.VBox();
        });
    }

    private void setupFastSearch() {
        fastSearchTf.setOnAction(e -> handleFastSearch());
    }

    private void setupDoubleClick() {
        resultTable.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                StudentDTO s = resultTable.getSelectionModel().getSelectedItem();
                if (s != null) openStudentProfile(s.getIdIndeks());
            }
        });
    }

    private void loadSrednjeSkole() {
        var coder = coderFactory.getSimpleCoder(CoderType.SREDNJA_SKOLA);
        if (coder != null) {
            srednjaSkolaCb.setItems(FXCollections.observableArrayList(coder.getCodes()));
        }
    }

    @FXML
    public void handleSearchByImePrezime() {
        String ime = ValidationHelper.getTextOrNull(imeTf);
        String prezime = ValidationHelper.getTextOrNull(prezimeTf);

        if (ime == null && prezime == null) {
            AlertHelper.showWarning("Upozorenje", "Unesite ime ili prezime.");
            return;
        }

        lastSearchType = SearchType.BY_NAME;
        currentPage = 0;
        performCurrentSearch(0);
    }

    @FXML
    public void handleSearchBySrednjaSkola() {
        SimpleCode skola = srednjaSkolaCb.getValue();
        if (skola == null) {
            AlertHelper.showWarning("Upozorenje", "Izaberite srednju školu.");
            return;
        }

        lastSearchType = SearchType.BY_SCHOOL;
        currentPage = 0;
        performCurrentSearch(0);
    }

    private void performCurrentSearch(int page) {
        if (lastSearchType == SearchType.BY_NAME) {
            performSearchByName(page);
        } else if (lastSearchType == SearchType.BY_SCHOOL) {
            performSearchBySchool(page);
        }
    }

    private void performSearchByName(int page) {
        String ime = ValidationHelper.getTextOrNull(imeTf);
        String prezime = ValidationHelper.getTextOrNull(prezimeTf);

        statusLabel.setText("Pretraga...");

        GlobalExceptionHandler.wrapWithErrorHandling(
                studentService.searchPage(ime, prezime, null, null, null, null, page, PAGE_SIZE)
        ).subscribe(
                pageResponse -> Platform.runLater(() -> {
                    displayResults(pageResponse);
                }),
                err -> Platform.runLater(() -> {
                    statusLabel.setText("");
                })
        );
    }

    private void performSearchBySchool(int page) {
        SimpleCode skola = srednjaSkolaCb.getValue();
        if (skola == null) return;

        String nazivSkole = skola.getCode();
        statusLabel.setText("Pretraga po srednjoj školi...");

        GlobalExceptionHandler.wrapWithErrorHandling(
                studentService.searchPage(null, null, null, null, null, nazivSkole, page, PAGE_SIZE)
        ).subscribe(
                pageResponse -> Platform.runLater(() -> {
                    displayResults(pageResponse);
                }),
                err -> Platform.runLater(() -> {
                    statusLabel.setText("");
                })
        );
    }

    private void displayResults(PageResponse<org.raflab.studsluzba.dtos.StudentDTO> pageResponse) {
        var clientList = pageResponse.getContent().stream().map(s -> {
            StudentDTO dto = new StudentDTO();
            dto.setIdIndeks(s.getIdIndeks());
            dto.setIdStudentPodaci(s.getIdStudentPodaci());
            dto.setIme(s.getIme());
            dto.setPrezime(s.getPrezime());
            dto.setGodinaUpisa(s.getGodinaUpisa());
            dto.setStudProgramOznaka(s.getStudProgramOznaka());
            dto.setBroj(s.getBroj());
            dto.setAktivanIndeks(s.isAktivanIndeks());
            return dto;
        }).toList();

        resultTable.setItems(FXCollections.observableArrayList(clientList));
        statusLabel.setText(String.format("Pronađeno: %d (stranica %d od %d)",
                pageResponse.getTotalElements(),
                pageResponse.getPage() + 1,
                pageResponse.getTotalPages()));

        if (pageResponse.getTotalPages() > 1) {
            pagination.setPageCount(pageResponse.getTotalPages());
            pagination.setCurrentPageIndex(pageResponse.getPage());
            pagination.setVisible(true);
        } else {
            pagination.setVisible(false);
        }
    }

    @FXML
    public void handleFastSearch() {
        String indeksShort = fastSearchTf.getText().trim();
        if (indeksShort.isEmpty()) {
            AlertHelper.showWarning("Upozorenje", "Unesite broj indeksa!");
            return;
        }

        statusLabel.setText("Pretraga...");
        lastSearchType = SearchType.NONE;
        pagination.setVisible(false);

        GlobalExceptionHandler.wrapWithErrorHandling(
                studentService.fastSearch(indeksShort)
                        .flatMap(indeks -> {
                            if (indeks == null || indeks.getId() == null || indeks.getStudent() == null) {
                                return Mono.empty();
                            }

                            Long indeksId = indeks.getId();
                            Long studentPodaciId = indeks.getStudent().getId();

                            return studentService.getStudentPodaciById(studentPodaciId)
                                    .map(podaci -> {
                                        StudentDTO dto = new StudentDTO();
                                        dto.setIdIndeks(indeksId);
                                        dto.setIme(podaci.getIme());
                                        dto.setPrezime(podaci.getPrezime());
                                        dto.setStudProgramOznaka(indeks.getStudProgramOznaka());
                                        dto.setBroj(indeks.getBroj());
                                        dto.setGodinaUpisa(indeks.getGodina());
                                        dto.setAktivanIndeks(indeks.isAktivan());
                                        return dto;
                                    });
                        })
                        .switchIfEmpty(Mono.fromRunnable(() -> Platform.runLater(() -> {
                            AlertHelper.showInfo("Rezultat", "Student nije pronađen");
                            resultTable.getItems().clear();
                            statusLabel.setText("");
                        })).then(Mono.empty()))
        ).subscribe(
                studentDto -> Platform.runLater(() -> {
                    resultTable.setItems(FXCollections.observableArrayList(studentDto));
                    resultTable.getSelectionModel().selectFirst();
                    statusLabel.setText("Pronađeno: 1");
                }),
                err -> Platform.runLater(() -> {
                    statusLabel.setText("");
                })
        );
    }

    @FXML
    public void handleOpenProfile() {
        StudentDTO selected = resultTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showWarning("Upozorenje", "Izaberite studenta iz tabele!");
            return;
        }
        openStudentProfile(selected.getIdIndeks());
    }

    @FXML
    public void handleNewStudent() {
        mainView.changeRoot("studentForm");
    }

    @FXML
    public void handleClear() {
        imeTf.clear();
        prezimeTf.clear();
        fastSearchTf.clear();
        srednjaSkolaCb.getSelectionModel().clearSelection();
        resultTable.getItems().clear();
        statusLabel.setText("");
        currentPage = 0;
        lastSearchType = SearchType.NONE;
        pagination.setVisible(false);
    }

    private void openStudentProfile(Long indeksId) {
        StudentProfileController c = mainView.openModalWithController(
                "studentProfile", "Profil studenta", 1000, 700
        );
        if (c != null) c.setStudentIndeksId(indeksId);
    }
}
