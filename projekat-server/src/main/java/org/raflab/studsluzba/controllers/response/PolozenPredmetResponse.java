package org.raflab.studsluzba.controllers.response;

import lombok.Data;

@Data
public class PolozenPredmetResponse {

    private Long id;
    private Integer ocena;
    private boolean priznat;

    private Long studentIndeksId;
    private Long predmetId;
    private Long ispitIzlazakId;
}