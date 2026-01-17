package org.raflab.studsluzba.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class IspitniRokRequest {
    @NotNull
    private String naziv;

    @NotNull
    private LocalDateTime datumPocetka;

    @NotNull
    private LocalDateTime datumZavrsetka;

    @NotNull
    private Long skolskaGodinaId;
}
