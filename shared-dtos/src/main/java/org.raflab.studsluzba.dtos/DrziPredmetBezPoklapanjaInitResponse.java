package org.raflab.studsluzba.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DrziPredmetBezPoklapanjaInitResponse {

    private String fajlPredmetNazivBezPoklapanja;
    private String fajlNastavnikEmailBezPoklapanja;
}