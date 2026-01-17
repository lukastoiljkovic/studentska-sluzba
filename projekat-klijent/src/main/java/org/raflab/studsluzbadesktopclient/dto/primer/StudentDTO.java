package org.raflab.studsluzbadesktopclient.dto.primer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {
    private int godinaUpisa;
    private String pol;
    private String ime;
    private String prezime;
    private String srednjeIme;
    private String jmbg;
    private Date datumRodjenja;
    private String mestoRodjenja;
    private String drzavaRodjenja;
    private String drzavljanstvo;
    private String nacionalnost;
    private String adresa;
    private String mestoStanovanja;
    private String brojTelefona;
    private String emailFakultet;
    private String emailPrivatni;
    private String licnaKarta;
}
