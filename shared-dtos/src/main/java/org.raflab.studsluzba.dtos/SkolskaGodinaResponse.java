package org.raflab.studsluzba.dtos;

import lombok.Data;

@Data
public class SkolskaGodinaResponse {
    private Long id;
    private String naziv;
    private boolean aktivna;
}
