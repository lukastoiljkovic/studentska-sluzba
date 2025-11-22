package org.raflab.studsluzba.controllers.request;

import lombok.Data;

@Data
public class IspitIzlazakRequest {
    private Integer brojPoena;
    private String napomena;
    private Boolean ponistava;

    private Long ispitPrijavaId;   // obavezno
    private Long studentIndeksId;  // obavezno
}
