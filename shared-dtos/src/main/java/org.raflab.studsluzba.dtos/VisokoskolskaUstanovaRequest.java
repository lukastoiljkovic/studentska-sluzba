package org.raflab.studsluzba.dtos;

import lombok.Data;
import org.raflab.studsluzba.dtos.enums.VrstaVisokoskolskeUstanove;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class VisokoskolskaUstanovaRequest {
    @NotBlank
    private String naziv;

    @NotBlank
    private String mesto;

    @NotNull
    private VrstaVisokoskolskeUstanove vrsta;
}