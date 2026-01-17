package org.raflab.studsluzba.dtos;

import lombok.Data;

@Data
public class NepolozenPredmetResponse {
    private Long slusaPredmetId;
    private Long predmetId;
    private String predmetSifra;
    private String predmetNaziv;
    private Integer espb;
    private Integer semestar;
    private String nastavnikIme;
    private Integer brojIzlazaka; // koliko puta je izlazio
}