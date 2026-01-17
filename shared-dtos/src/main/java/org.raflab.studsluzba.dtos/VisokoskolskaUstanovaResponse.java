package org.raflab.studsluzba.dtos;

import lombok.Data;
import org.raflab.studsluzba.dtos.enums.VrstaVisokoskolskeUstanove;

@Data
public class VisokoskolskaUstanovaResponse {
    private Long id;
    private String naziv;
    private String mesto;
    private VrstaVisokoskolskeUstanove vrsta;
}
