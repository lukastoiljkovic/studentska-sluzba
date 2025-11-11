package org.raflab.studsluzba.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data @Entity
public class IspitPrijava {

    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private LocalDate datum;

    @ManyToOne
    private StudentIndeks studentIndeks;

    @ManyToOne
    private Ispit ispit;

    @OneToOne(mappedBy = "ispitPrijava") // opciono, ne mora da se izadje
    private IspitIzlazak ispitIzlazak;

}
