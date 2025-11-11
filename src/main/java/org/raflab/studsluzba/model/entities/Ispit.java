package org.raflab.studsluzba.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Data
public class Ispit {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private LocalDateTime datumVremePocetka;
    boolean zakljucen;

    @ManyToOne
    private IspitniRok ispitniRok;

    @ManyToOne
    private Nastavnik nastavnik;

    @ManyToOne
    private Predmet predmet;

}
