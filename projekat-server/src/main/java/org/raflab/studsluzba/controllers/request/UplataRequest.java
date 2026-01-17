package org.raflab.studsluzba.controllers.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class UplataRequest {
    @NotNull private Long studentIndeksId;
    @NotNull private Long skolskaGodinaId;
    @NotNull private Double iznosEUR;
    private LocalDate datum; // opcionalno, ako se ne prosledi koristi se today
}
