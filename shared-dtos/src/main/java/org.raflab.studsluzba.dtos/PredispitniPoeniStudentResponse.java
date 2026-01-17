package org.raflab.studsluzba.dtos;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredispitniPoeniStudentResponse {

    private Long studentIndeksId;
    private Long predmetId;
    private Long skolskaGodinaId;

    private Integer ukupno;                  // suma poena

    private List<Stavka> stavke;             // detaljni prikaz

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Stavka {
        private Long izlazakId;
        private Long slusaPredmetId;
        private Long predispitnaObavezaId;
        private String vrsta;
        private Integer maxPoena;
        private Integer poeni;
        private LocalDate datum;
    }
}
