package org.raflab.studsluzba.controllers.request;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class IspitRequest {

    @NotNull
    private LocalDateTime datumVremePocetka;

    private boolean zakljucen;

    @NotNull
    private Long ispitniRokId;

    @NotNull
    private Long nastavnikId;

    @NotNull
    private Long predmetId;
}
