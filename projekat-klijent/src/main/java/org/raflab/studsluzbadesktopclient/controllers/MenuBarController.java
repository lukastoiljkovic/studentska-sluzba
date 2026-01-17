// MenuBarController.java
package org.raflab.studsluzbadesktopclient.controllers;

import javafx.fxml.FXML;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzbadesktopclient.app.MainView;
import org.raflab.studsluzbadesktopclient.utils.AlertHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuBarController {

    private final MainView mainView;
    private final MainWindowController mainWindowController;

    // ========== STUDENTI ==========

    @FXML
    public void openStudentSearch() {
        mainWindowController.show("studentSearch");
    }

    @FXML
    public void openNewStudent() {
        mainWindowController.show("studentForm");
    }

    @FXML
    public void openIspitPrijava() {
        mainWindowController.show("ispitPrijava");
    }

    @FXML
    public void openUplate() {
        mainWindowController.show("uplate");
    }

    // ========== NASTAVNICI ==========

    @FXML
    public void openNastavnikSearch() {
        mainWindowController.show("nastavnikSearch");
    }

    @FXML
    public void openNewNastavnik() {
        mainWindowController.show("nastavnikForm");
    }

    // ========== PREDMETI ==========

    @FXML
    public void openPredmetList() {
        mainWindowController.show("studijskiProgramList");
    }

    @FXML
    public void openNewPredmet() {
        mainWindowController.show("predmetForm");
    }
    // ========== ISPITI ==========

    @FXML
    public void openNewIspit() {
        mainWindowController.show("ispitForm");
    }

    @FXML
    public void openUnosRezultata() {
        mainWindowController.show("ispitRezultati");
    }

    // ========== ADMINISTRACIJA ==========

    @FXML
    public void openSkolskeGodine() {
        mainWindowController.show("skolskeGodine");
    }

    @FXML
    public void openGrupe() {
        mainWindowController.show("grupe");
    }

    @FXML
    public void openSrednjeSkole() {
        mainWindowController.show("srednjeSkole");
    }

    @FXML
    public void openVisokoskolskeUstanove() {
        mainWindowController.show("visokoskolskeUstanove");
    }

    // ========== IZVEŠTAJI ==========

    @FXML
    public void reportStudentiPoProgramu() {
        mainView.openModal("reports/studentiPoProgramu", "Izveštaj - Studenti po programu", 800, 600);
    }

    @FXML
    public void reportRezultatiIspita() {
        mainView.openModal("reports/rezultatiIspita", "Izveštaj - Rezultati ispita", 800, 600);
    }

    @FXML
    public void reportProsecneOcene() {
        mainView.openModal("reports/prosecneOcene", "Izveštaj - Prosečne ocene", 800, 600);
    }

    @FXML
    public void openIspitniRokovi() {
        mainWindowController.show("ispitniRokUpravljanje");
    }

    @FXML
    public void openIspitList() {
        mainWindowController.show("ispitniRokList");
    }

    @FXML
    public void openStudijskiProgrami() {
        mainWindowController.show("studijskiProgramList");
    }



}