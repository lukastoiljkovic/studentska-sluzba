package org.raflab.studsluzba.controllers.response;

import lombok.Data;
import org.raflab.studsluzba.model.entities.SrednjaSkola;

@Data
public class SrednjaSkolaResponse {
    private Long id;
    private String naziv;
    private String mesto;
    private SrednjaSkola.VrstaSkole vrsta;
}
