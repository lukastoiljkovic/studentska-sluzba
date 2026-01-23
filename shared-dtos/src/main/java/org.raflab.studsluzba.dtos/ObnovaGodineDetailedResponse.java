package org.raflab.studsluzba.dtos;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ObnovaGodineDetailedResponse {
    private Long id;
    private Integer godinaStudija;
    private LocalDate datum;
    private String napomena;

    // Student - osnovni podaci
    private Long studentIndeksId;
    private String studentIme;
    private String studentPrezime;
    private String indeks; // "RI/24/102"

    // Skolska godina
    private Long skolskaGodinaId;
    private String skolskaGodinaNaziv;

    // Predmeti koje obnavlja - DETALJAN prikaz
    private List<PredmetKojiSeObnavljaDTO> predmetiKojeObnavlja;

    @Data
    public static class PredmetKojiSeObnavljaDTO {
        private Long slusaPredmetId;
        private Long predmetId;
        private String predmetSifra;
        private String predmetNaziv;
        private Integer espb;
        private String nastavnikIme;
    }
}