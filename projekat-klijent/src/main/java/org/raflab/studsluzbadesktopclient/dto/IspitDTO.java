package org.raflab.studsluzbadesktopclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IspitDTO {
    private Long id;
    private LocalDateTime datumVremePocetka;
    private boolean zakljucen;

    private Long ispitniRokId;
    private String ispitniRokNaziv;

    private Long nastavnikId;
    private String nastavnikIme;

    private Long predmetId;
    private String predmetNaziv;

    @Override
    public String toString() {
        return predmetNaziv + " - " + ispitniRokNaziv;
    }
}