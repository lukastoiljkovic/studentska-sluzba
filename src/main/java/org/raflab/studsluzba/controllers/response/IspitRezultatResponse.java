// src/main/java/org/raflab/studsluzba/controllers/response/IspitRezultatResponse.java
package org.raflab.studsluzba.controllers.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class IspitRezultatResponse {
    private Long studentId;
    private Long studentIndeksId;

    private String ime;
    private String prezime;

    private String studProgramOznaka;
    private Integer godinaUpisa;
    private Integer brojIndeksa;

    private Integer predispitni; // zbir svih predispitnih bodova
    private Integer ispitni;     // bodovi sa ispita (iz IspitIzlazak)
    private Integer ukupno;      // predispitni + ispitni
}
