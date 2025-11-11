package org.raflab.studsluzba.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity @Data
public class PredispitnaIzlazak {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer poeni;
    private LocalDate datum;

    @ManyToOne
    private PredispitnaObaveza predispitnaObaveza; // npr. kolokvijum, domaci itd.

    @ManyToOne
    private SlusaPredmet slusaPredmet;
}
