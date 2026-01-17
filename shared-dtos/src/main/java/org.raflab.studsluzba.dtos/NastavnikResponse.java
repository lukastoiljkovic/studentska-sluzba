package org.raflab.studsluzba.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
public class NastavnikResponse {
    private Long id;
    private String ime;
    private String prezime;
    private String srednjeIme;
    private String email;
    private String brojTelefona;
    private String adresa;
    private Set<NastavnikZvanjeResponse> zvanja;

    private LocalDate datumRodjenja;
    private Character pol;
    private String jmbg;
}

