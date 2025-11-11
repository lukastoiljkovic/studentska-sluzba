package org.raflab.studsluzba.model.entities;

import lombok.Data;

import javax.persistence.*;

@Data @Entity
public class PolozenPredmet {

    @Id @GeneratedValue
    private Long id;

    private Integer ocena;
    private boolean priznat; // ako je priznat sa drugog fakulteta

    @ManyToOne
    private StudentIndeks studentIndeks;

    @ManyToOne
    private Predmet predmet;

    @ManyToOne // nije OneToOne zato sto student moze poloziti vise puta, ako ponistava
    private IspitIzlazak ispitIzlazak;

}
