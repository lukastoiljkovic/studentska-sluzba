package org.raflab.studsluzba.controllers.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.raflab.studsluzba.model.entities.VisokoskolskaUstanova;

@Data
public class VisokoskolskaUstanovaRequest {
    @NotBlank
    private String naziv;

    @NotBlank
    private String mesto;

    @NotNull
    private VisokoskolskaUstanova.Vrsta vrsta; // FAKULTET ili VISOKA_SKOLA
}
