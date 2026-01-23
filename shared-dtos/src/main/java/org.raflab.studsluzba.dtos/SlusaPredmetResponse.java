package org.raflab.studsluzba.dtos;

import lombok.Data;

@Data
public class SlusaPredmetResponse {
    private Long id;

    // Student info
    private Long studentIndeksId;
    private String studentIme;
    private String studentPrezime;
    private String indeks; // "RI/24/102"

    // Predmet info
    private Long predmetId;
    private String predmetSifra;
    private String predmetNaziv;
    private Integer predmetEspb;

    // Nastavnik info
    private Long nastavnikId;
    private String nastavnikIme;

    // Grupa info
    private Long grupaId;
    private String grupaNaziv;

    // Skolska godina
    private Long skolskaGodinaId;
    private String skolskaGodinaNaziv;
}