package org.raflab.studsluzba.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class IspitResponse {

    private Long id;
    private LocalDateTime datumVremePocetka;
    private boolean zakljucen;

    private Long ispitniRokId;
    private String ispitniRokNaziv;

    private Long nastavnikId;
    private String nastavnikIme;

    private Long predmetId;
    private String predmetNaziv;
}
