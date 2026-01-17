package org.raflab.studsluzba.controllers.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class TokStudijaRequest {
    @NotNull
    private Long studentIndeksId;

    private Set<Long> upisGodineIds;
    private Set<Long> obnovaGodineIds;
}
