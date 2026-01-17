package org.raflab.studsluzba.model.entities;

import lombok.Data;

import javax.persistence.*;

@Data @Entity
public class PolozenPredmet {

    @Id @GeneratedValue
    private Long id;

    private Integer ocena;
    private boolean priznat;

    @ManyToOne
    private StudentIndeks studentIndeks;

    @ManyToOne
    private Predmet predmet;

    @ManyToOne
    private IspitIzlazak ispitIzlazak;
}
