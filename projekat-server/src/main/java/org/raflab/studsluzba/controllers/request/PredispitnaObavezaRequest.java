package org.raflab.studsluzba.controllers.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PredispitnaObavezaRequest {

    @NotNull
    private String vrsta;

    @NotNull
    private Integer maxPoena;

    @NotNull
    private Long predmetId;
}
