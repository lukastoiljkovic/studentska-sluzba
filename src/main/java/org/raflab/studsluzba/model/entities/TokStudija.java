package org.raflab.studsluzba.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity @Data
public class TokStudija {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private StudentIndeks studentIndeks;

    @OneToMany
    private Set<UpisGodine> upisi;

    @OneToMany
    private Set<ObnovaGodine> obnove;

}
