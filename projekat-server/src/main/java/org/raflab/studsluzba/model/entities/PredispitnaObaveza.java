package org.raflab.studsluzba.model.entities;

import lombok.Data;

import javax.persistence.*;

@Entity @Data
public class PredispitnaObaveza {

    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String vrsta; // kolokvijum ili test
    private Integer maxPoena;

    @ManyToOne
    private Predmet predmet;
}
