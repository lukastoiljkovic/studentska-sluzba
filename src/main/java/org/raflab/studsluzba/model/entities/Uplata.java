package org.raflab.studsluzba.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity @Data
public class Uplata {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDate datum;
    private Double iznosRSD;
    private Double iznosEUR;
    private Double kurs;

    @ManyToOne
    private UpisGodine upisGodine;
}
