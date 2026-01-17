package org.raflab.studsluzba.dtos;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PredispitnaIzlazakResponse {

    private Long id;
    private Long slusaPredmetId;
    private Long predispitnaObavezaId;
    private Integer poeni;
    private LocalDate datum;

}