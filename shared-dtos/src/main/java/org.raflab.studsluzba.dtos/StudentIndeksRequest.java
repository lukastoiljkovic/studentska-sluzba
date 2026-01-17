package org.raflab.studsluzba.dtos;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class StudentIndeksRequest {

    @NotNull private Integer godina; // moze null ako preskače godinu
    @NotNull private String studProgramOznaka;
    private String nacinFinansiranja;
    private boolean aktivan = true;  // default true
    private LocalDate vaziOd;         // setovati u servisu ako null
    private Long studentId;
}
