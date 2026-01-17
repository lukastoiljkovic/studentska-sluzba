package org.raflab.studsluzbadesktopclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IspitRezultatDTO {
    private Long studentId;
    private Long studentIndeksId;

    private String ime;
    private String prezime;

    private String studProgramOznaka;
    private Integer godinaUpisa;
    private Integer brojIndeksa;

    private Integer predispitni;
    private Integer ispitni;
    private Integer ukupno;

    public String getPunoIme() {
        return ime + " " + prezime;
    }

    public String getIndeksFormatirano() {
        return studProgramOznaka + "/" + (godinaUpisa % 100) + "/" + brojIndeksa;
    }
}
