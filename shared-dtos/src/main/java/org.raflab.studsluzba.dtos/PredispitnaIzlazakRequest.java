package org.raflab.studsluzba.dtos;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PredispitnaIzlazakRequest {

    private Long slusaPredmetId;
    private Long predispitnaObavezaId;
    private Integer poeni;
    private LocalDate datum;

}
