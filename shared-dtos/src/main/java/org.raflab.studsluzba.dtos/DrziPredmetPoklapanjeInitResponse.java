package org.raflab.studsluzba.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DrziPredmetPoklapanjeInitResponse {
    private Long predmetId;
    private String predmetNaziv;

    private String fajlPredmetNaziv;
    private String fajlNastavnikEmail;
}