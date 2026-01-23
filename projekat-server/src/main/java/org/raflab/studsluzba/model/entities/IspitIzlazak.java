package org.raflab.studsluzba.model.entities;

import lombok.Data;

import javax.persistence.*;

@Data @Entity
public class IspitIzlazak {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer brojPoena;
    private String napomena;
    private boolean ponistava;

    @OneToOne
    private IspitPrijava ispitPrijava;

    @ManyToOne
    private StudentIndeks studentIndeks;

}
