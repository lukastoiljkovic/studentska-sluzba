package org.raflab.studsluzbadesktopclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentSearchResultDTO {
    private Long idIndeks;
    private Long idStudentPodaci;
    private String ime;
    private String prezime;
    private Integer godinaUpisa;
    private String studProgramOznaka;
    private Integer broj;
    private boolean aktivanIndeks;

    public String getPunoIme() {
        return ime + " " + prezime;
    }

    public String getIndeksFormatirano() {
        return studProgramOznaka + "/" + (godinaUpisa % 100) + "/" + broj;
    }
}