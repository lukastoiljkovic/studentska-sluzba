package org.raflab.studsluzba.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity @Data
public class SkolskaGodina {

    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String naziv; // npr. 2025/2026.
    private boolean aktivna;

    @OneToMany(mappedBy = "skolskaGodina")
    private Set<DrziPredmet> drziPredmetList;

    @OneToMany(mappedBy = "skolskaGodina")
    private Set<SlusaPredmet> slusaPredmetList;

    @OneToMany(mappedBy = "skolskaGodina", fetch = FetchType.EAGER)
    private Set<IspitniRok> ispitniRokovi;

}
