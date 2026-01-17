package org.raflab.studsluzba.dtos;

import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentIndeksResponse {
    private Long id;
    private int broj;
    private int godina;
    private String studProgramOznaka;
    private String nacinFinansiranja;
    private boolean aktivan;
    private LocalDate vaziOd;
    private StudentPodaciResponse student; // promenjeno u response ?
    private StudijskiProgramResponse studijskiProgram; // promenjeno u response ?
    private Integer ostvarenoEspb;
}
