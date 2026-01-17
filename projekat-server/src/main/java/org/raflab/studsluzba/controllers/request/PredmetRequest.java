package org.raflab.studsluzba.controllers.request;

import lombok.Data;

@Data
public class PredmetRequest {
    private String sifra;
    private String naziv;
    private String opis;
    private Integer espb;
    private boolean obavezan;
    private Integer semestar;
    private Integer fondPredavanja;
    private Integer fondVezbi;
    private Long studProgramId; // referenca na StudijskiProgram
}
