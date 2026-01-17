package org.raflab.studsluzba.model.entities;

import lombok.Data;

import javax.persistence.*;

@Entity @Data
public class VisokoskolskaUstanova {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String naziv;
    private String mesto;
    @Enumerated(EnumType.STRING) private VisokoskolskaUstanova.Vrsta vrsta;

    public enum Vrsta {
        FAKULTET,
        VISOKA_SKOLA
    }

}
