package org.raflab.studsluzbadesktopclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NastavnikZvanjeDTO {
    private Long id;
    private LocalDate datumIzbora;
    private String naucnaOblast;
    private String uzaNaucnaOblast;
    private String zvanje;
    private boolean aktivno;

    private Long nastavnikId;
    private String nastavnikIme;
    private String nastavnikPrezime;
}