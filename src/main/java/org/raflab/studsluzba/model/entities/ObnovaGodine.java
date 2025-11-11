package org.raflab.studsluzba.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity @Data
public class ObnovaGodine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer godinaStudija; // 1,2,3,4
    private LocalDate datum;
    private String napomena;

    @ManyToOne
    private StudentIndeks studentIndeks;

    @ManyToOne // trenutna skolska godina npr 2025/26
    private SkolskaGodina skolskaGodina;

    @OneToMany
    private Set<SlusaPredmet> predmetiKojeObnavlja; // iz prethodne godine
}
