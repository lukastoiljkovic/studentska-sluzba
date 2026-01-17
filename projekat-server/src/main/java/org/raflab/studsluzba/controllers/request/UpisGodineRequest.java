package org.raflab.studsluzba.controllers.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

@Data
public class UpisGodineRequest {
    @NotNull private Integer godinaStudija;
    @NotNull private LocalDate datum;
    private String napomena;

    @NotNull private Long studentIndeksId;
    @NotNull private Long skolskaGodinaId;

    // opc, ako nam treba ovo
    private Set<Long> predmetiKojePrenosiIds;
}
