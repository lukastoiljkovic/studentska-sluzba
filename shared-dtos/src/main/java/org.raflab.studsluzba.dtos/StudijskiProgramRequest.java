package org.raflab.studsluzba.dtos;

import lombok.Data;

@Data
public class StudijskiProgramRequest {

    private Long id;

    private String oznaka;  // RN, RI, SI, RM, IS, IT, MD
    private String naziv;
    private Integer godinaAkreditacije;
    private String zvanje;
    private Integer trajanjeGodina;
    private Integer trajanjeSemestara;
    private String vrstaStudija;  // OAS, OSS, MAS, MSS, DAS
    private Integer ukupnoEspb;

}
