package org.raflab.studsluzbadesktopclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NastavnikDTO {
    private Long id;
    private String ime;
    private String prezime;
    private String srednjeIme;
    private String email;
    private String brojTelefona;
    private String adresa;
    private Set<NastavnikZvanjeDTO> zvanja;

    private LocalDate datumRodjenja;
    private Character pol;
    private String jmbg;

    public String getPunoIme() {
        return ime + " " + (srednjeIme != null ? srednjeIme + " " : "") + prezime;
    }

    @Override
    public String toString() {
        return getPunoIme();
    }
}
