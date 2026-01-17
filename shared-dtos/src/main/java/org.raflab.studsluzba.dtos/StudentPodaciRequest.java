package org.raflab.studsluzba.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class StudentPodaciRequest {

    @NotNull private Long id;
    @NotNull private String ime;
    @NotNull private String prezime;
    @NotNull private String srednjeIme;

    @NotNull private String jmbg;
    @NotNull private Character pol;

    @NotNull private LocalDate datumRodjenja;
    @NotNull private String drzavaRodjenja;
    @NotNull private String mestoRodjenja;

    @NotNull private String drzavljanstvo;
    private String nacionalnost;

    @NotNull private String mestoPrebivalista;
    @NotNull private String adresaPrebivalista;

    private String brojTelefonaMobilni;
    private String brojTelefonaFiksni;

    @NotNull private String emailFakultetski;
    @NotNull private String emailPrivatni;

    @NotNull private String brojLicneKarte;
    @NotNull private String licnuKartuIzdao;

    private String srednjaSkola; // naziv skole
    private Double uspehSrednjaSkola;
    private Double uspehPrijemni;

    private String prethodnaUstanova; // naziv ustanove
}
