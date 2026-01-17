package org.raflab.studsluzba.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class NastavnikZvanjeResponse {

    private Long id;
    private LocalDate datumIzbora;
    private String naucnaOblast;
    private String uzaNaucnaOblast;
    private String zvanje;
    private boolean aktivno;

    private Long nastavnikId;
    private String nastavnikIme; // opcionalno za prikaz
    private String nastavnikPrezime;
}
