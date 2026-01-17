package org.raflab.studsluzba.dtos;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class IspitPrijavaResponse {

    private Long id;
    private LocalDate datum;

    private Long studentIndeksId;
    private Integer indeksBroj;
    private Integer indeksGodina;
    private String studProgramOznaka;

    private Long ispitId;
    private LocalDateTime datumIspita;
    private String predmetSifra;
    private String predmetNaziv;

    private Long ispitIzlazakId; // može biti null ako nije izašao
}
