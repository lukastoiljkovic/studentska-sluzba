package org.raflab.studsluzbadesktopclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredmetDTO {
    private Long id;
    private String sifra;
    private String naziv;
    private String opis;
    private Integer espb;
    private boolean obavezan;
    private Integer semestar;
    private Integer fondPredavanja;
    private Integer fondVezbi;

    private String studProgramNaziv;
    private StudijskiProgramDTO studijskiProgram;

    @Override
    public String toString() {
        return sifra + " - " + naziv + " (" + espb + " ESPB)";
    }
}