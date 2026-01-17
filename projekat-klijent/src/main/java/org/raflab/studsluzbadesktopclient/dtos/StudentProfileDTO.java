package org.raflab.studsluzbadesktopclient.dtos;

import lombok.Data;
import org.raflab.studsluzba.dtos.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Proširena verzija StudentProfileDTO za potrebe klijentske aplikacije.
 * Sadrži sve podatke potrebne za prikaz studentskog profila.
 */
@Data
public class StudentProfileDTO {

    // ID-jevi
    private Long studentId;
    private Long studentIndeksId;
    private Long studProgramId;

    // Osnovni podaci
    private String ime;
    private String prezime;
    private String srednjeIme;
    private String jmbg;
    private Character pol;

    // Datum i mesto rođenja
    private LocalDate datumRodjenja;
    private String mestoRodjenja;
    private String drzavaRodjenja;
    private String drzavljanstvo;
    private String nacionalnost;

    // Kontakt podaci
    private String mestoPrebivalista;
    private String adresaPrebivalista;
    private String brojTelefonaMobilni;
    private String brojTelefonaFiksni;
    private String emailFakultetski;
    private String emailPrivatni;

    // Dokumenti
    private String brojLicneKarte;
    private String licnuKartuIzdao;

    // Obrazovanje
    private String srednjaSkola;
    private Double uspehSrednjaSkola;
    private Double uspehPrijemni;
    private String prethodnaUstanova;

    // Indeks podaci
    private Integer broj;
    private Integer godina;
    private String studProgramOznaka;
    private String studProgramNaziv;
    private String nacinFinansiranja;
    private boolean aktivanIndeks;
    private LocalDate vaziOd;

    // Akademski podaci
    private Integer ostvarenoEspb;
    private Double prosecnaOcena;

    // Liste
    private List<PolozenPredmetResponse> polozeniPredmeti;
    private List<NepolozenPredmetResponse> nepolozeniPredmeti;
    private List<UpisGodineResponse> upisaneGodine;
    private List<ObnovaGodineResponse> obnoveGodine;
    private List<UplataResponse> uplate;

    /**
     * Vraća formatiran indeks u obliku RN123/24
     */
    public String getIndeksFormatirano() {
        if (studProgramOznaka != null && broj != null && godina != null) {
            return String.format("%s%d/%02d", studProgramOznaka, broj, godina % 100);
        }
        return "";
    }
}