package org.raflab.studsluzba.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity @Data
public class TokStudija {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private StudentIndeks studentIndeks;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<UpisGodine> upisi;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<ObnovaGodine> obnove;
}
