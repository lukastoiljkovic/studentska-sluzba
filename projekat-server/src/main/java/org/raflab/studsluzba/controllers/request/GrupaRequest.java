package org.raflab.studsluzba.controllers.request;

import lombok.Data;

@Data
public class GrupaRequest {
    private String naziv;
    private Long studijskiProgramId;
    private Long skolskaGodinaId;
}
