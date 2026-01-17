package org.raflab.studsluzbadesktopclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudijskiProgramDTO {
    private Long id;
    private String oznaka;
    private String naziv;
    private Integer godinaAkreditacije;
    private String zvanje;
    private Integer trajanjeGodina;
    private Integer trajanjeSemestara;
    private String vrstaStudija;
    private Integer ukupnoEspb;

    @Override
    public String toString() {
        return oznaka + " - " + naziv;
    }
}