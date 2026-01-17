package org.raflab.studsluzba.controllers.response;

import lombok.Data;
import org.raflab.studsluzba.controllers.request.StudentPodaciRequest;
import org.raflab.studsluzba.model.entities.StudentPodaci;
import org.raflab.studsluzba.model.entities.StudijskiProgram;
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
