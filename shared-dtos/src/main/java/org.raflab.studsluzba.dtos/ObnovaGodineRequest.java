package org.raflab.studsluzba.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class ObnovaGodineRequest {

    private Integer godinaStudija;
    private LocalDate datum;
    private String napomena;

    private Long studentIndeksId;
    private Long skolskaGodinaId;

    private Set<Long> predmetiKojeObnavljaIds;
}