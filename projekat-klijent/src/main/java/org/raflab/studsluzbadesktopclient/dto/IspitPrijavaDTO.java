package org.raflab.studsluzbadesktopclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IspitPrijavaDTO {
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

    private Long ispitIzlazakId;
}