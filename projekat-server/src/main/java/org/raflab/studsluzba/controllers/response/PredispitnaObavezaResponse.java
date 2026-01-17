package org.raflab.studsluzba.controllers.response;

import lombok.Data;

@Data
public class PredispitnaObavezaResponse {

    private Long id;
    private String vrsta;
    private Integer maxPoena;
    private Long predmetId;
}
