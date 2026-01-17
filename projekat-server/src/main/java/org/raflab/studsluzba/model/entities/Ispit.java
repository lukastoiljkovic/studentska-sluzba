package org.raflab.studsluzba.model.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Data @EqualsAndHashCode(onlyExplicitlyIncluded = true)
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
