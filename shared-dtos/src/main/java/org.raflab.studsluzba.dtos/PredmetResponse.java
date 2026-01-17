package org.raflab.studsluzba.dtos;

import lombok.Data;

@Data
public class PredmetResponse {
    private Long id;
    private String sifra;
    private String naziv;
    private String opis;
    private Integer espb;
    private boolean obavezan;
    private Integer semestar;
    private Integer fondPredavanja;
    private Integer fondVezbi;

    private String studProgramNaziv;          // za jednostavne prikaze
    private StudijskiProgramResponse studijskiProgram; // za detaljan prikaz
}
