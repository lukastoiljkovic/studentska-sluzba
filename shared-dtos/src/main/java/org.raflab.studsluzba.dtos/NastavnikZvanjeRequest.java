package org.raflab.studsluzba.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class NastavnikZvanjeRequest {

    @NotNull
    private LocalDate datumIzbora;
    private String naucnaOblast;
    private String uzaNaucnaOblast;
    @NotNull
    private String zvanje;
    private boolean aktivno;

    @NotNull
    private Long nastavnikId;   // veza ka nastavniku
}
