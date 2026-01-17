package org.raflab.studsluzba.controllers.request;

import lombok.Data;
import org.raflab.studsluzba.model.entities.SrednjaSkola;

@Data
public class SrednjaSkolaRequest {
    private String naziv;
    private String mesto;
    private SrednjaSkola.VrstaSkole vrsta;
}
