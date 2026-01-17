package org.raflab.studsluzba.dtos;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

@Data
public class NastavnikRequest {
    @NotNull private String ime;
    @NotNull private String prezime;
    @NotNull
    private String srednjeIme;
    @NotNull private String email;
    private String brojTelefona;
    private String adresa;
    private Set<NastavnikZvanjeRequest> zvanja;

    private LocalDate datumRodjenja;
    private Character pol;
    private String jmbg;
}
