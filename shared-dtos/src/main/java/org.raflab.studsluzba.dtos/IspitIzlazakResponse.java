package org.raflab.studsluzba.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class IspitIzlazakResponse {
    private Long id;
    private Integer brojPoena;
    private String napomena;
    private boolean ponistava;

    // Student/indeks (kratki rezime)
    private Long studentIndeksId;
    private Integer indeksBroj;
    private Integer indeksGodina;
    private String studProgramOznaka;

    // Prijava/ispit (kratki rezime)
    private Long ispitPrijavaId;
    private LocalDate datumPrijave;

    private Long ispitId;
    private LocalDateTime datumIspita;
    private String predmetSifra;
    private String predmetNaziv;
}
