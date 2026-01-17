package org.raflab.studsluzba.dtos;

import lombok.Data;

@Data
public class PolozenPredmetRequest {

    private Integer ocena;
    private boolean priznat;

    private Long studentIndeksId;
    private Long predmetId;
    private Long ispitIzlazakId; // opcionalno, može biti null
}