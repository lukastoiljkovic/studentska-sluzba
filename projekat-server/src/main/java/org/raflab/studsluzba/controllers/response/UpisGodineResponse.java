// controllers/response/UpisGodineResponse.java
package org.raflab.studsluzba.controllers.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UpisGodineResponse {
    private Long id;

    private Integer godinaStudija;
    private LocalDate datum;
    private String napomena;

    private Long studentIndeksId;
    private Integer indeksBroj;
    private Integer indeksGodina;
    private String studProgramOznaka;

    private Long skolskaGodinaId;
    private String skolskaGodinaNaziv;

    private Set<Long> predmetiKojePrenosiIds;
}
