package org.raflab.studsluzba.dtos;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * DTO specifično za kreiranje novog studenta kroz REST API.
 * Sadrži samo primitive/String tipove, bez entity referenci.
 */
@Data
public class StudentPodaciCreateRequest {

    @NotNull private String ime;
    @NotNull private String prezime;
    private String srednjeIme;

    @NotNull private String jmbg;
    @NotNull private Character pol;

    @NotNull private LocalDate datumRodjenja;
    private String drzavaRodjenja;
    private String mestoRodjenja;

    private String drzavljanstvo;
    private String nacionalnost;

    private String mestoPrebivalista;
    private String adresaPrebivalista;

    private String brojTelefonaMobilni;
    private String brojTelefonaFiksni;

    @NotNull private String emailFakultetski;
    private String emailPrivatni;

    private String brojLicneKarte;
    private String licnuKartuIzdao;

    private String srednjaSkola;
    private Double uspehSrednjaSkola;
    private Double uspehPrijemni;

    private String prethodnaUstanova;
}