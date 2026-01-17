package org.raflab.studsluzbadesktopclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentIndeksDTO {
    private Long id;
    private Integer broj;
    private Integer godina;
    private String studProgramOznaka;
    private String nacinFinansiranja;
    private boolean aktivan;
    private LocalDate vaziOd;
    private Integer ostvarenoEspb;

    private StudentPodaciDTO student;
    private StudijskiProgramDTO studijskiProgram;

    public String getIndeksFormatirano() {
        return studProgramOznaka + "/" + (godina % 100) + "/" + broj;
    }

    public String getStudentIme() {
        return student != null ? student.getPunoIme() : "";
    }
}