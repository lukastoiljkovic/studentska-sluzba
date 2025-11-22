package org.raflab.studsluzba.controllers.response;

import lombok.Data;

@Data
public class GrupaResponse {
    private Long id;
    private String naziv;
    private String studijskiProgramNaziv;
    private String skolskaGodinaNaziv;
}
