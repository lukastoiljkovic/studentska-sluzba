package org.raflab.studsluzbadesktopclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentPodaciDTO {
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

    private String srednjaSkola;
    private Double uspehSrednjaSkola;
    private Double uspehPrijemni;

    private String prethodnaUstanova;

    public String getPunoIme() {
        return ime + " " + (srednjeIme != null ? srednjeIme + " " : "") + prezime;
    }
}