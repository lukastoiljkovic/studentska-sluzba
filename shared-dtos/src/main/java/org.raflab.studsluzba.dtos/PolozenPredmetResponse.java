package org.raflab.studsluzba.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PolozenPredmetResponse {
    private Long id;
    private Integer ocena;
    private boolean priznat;

    private Long studentIndeksId;
    private Long predmetId;
    private Long ispitIzlazakId;
    
    private String predmetSifra;
    private String predmetNaziv;
    private Integer espb;
    private Integer semestar;
    private LocalDate datumPolaganja; 
}
