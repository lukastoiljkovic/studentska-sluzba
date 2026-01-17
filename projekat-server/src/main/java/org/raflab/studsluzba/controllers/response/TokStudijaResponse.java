package org.raflab.studsluzba.controllers.response;

import lombok.Data;

import java.util.Set;

@Data
public class TokStudijaResponse {
    private Long id;

    private Long studentIndeksId;
    private Integer indeksBroj;
    private Integer indeksGodina;
    private String studProgramOznaka;

    private Set<Long> upisGodineIds;
    private Set<Long> obnovaGodineIds;
}
