package org.raflab.studsluzba.controllers.request;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class IspitPrijavaRequest {

    @NotNull
    private LocalDate datum;

    @NotNull
    private Long studentIndeksId;

    @NotNull
    private Long ispitId;
}
