package org.raflab.studsluzba.dtos;

import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentPodaciResponse {

    private Long id;
    private String ime;
    private String prezime;
    private String srednjeIme;

    private String jmbg;
    private Character pol;

    private LocalDate datumRodjenja;
    private String drzavaRodjenja;
    private String mestoRodjenja;

    private String drzavljanstvo;
    private String nacionalnost;

    private String mestoPrebivalista;
    private String adresaPrebivalista;

    private String brojTelefonaMobilni;
    private String brojTelefonaFiksni;

    private String emailFakultetski;
    private String emailPrivatni;

    private String brojLicneKarte;
    private String licnuKartuIzdao;

    private String srednjaSkola; // naziv skole
    private Double uspehSrednjaSkola;
    private Double uspehPrijemni;

    private String prethodnaUstanova; // naziv ustanove
}