package org.raflab.studsluzba.controllers.response;

import lombok.Data;
import org.raflab.studsluzba.model.entities.VisokoskolskaUstanova;

@Data
public class VisokoskolskaUstanovaResponse {
    private Long id;
    private String naziv;
    private String mesto;
    private VisokoskolskaUstanova.Vrsta vrsta;
}
