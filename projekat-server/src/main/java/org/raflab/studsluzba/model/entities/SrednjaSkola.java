package org.raflab.studsluzba.model.entities;

import lombok.Data;

import javax.persistence.*;

@Entity @Data
public class SrednjaSkola {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String naziv;
    private String mesto;
    @Enumerated(EnumType.STRING) private VrstaSkole vrsta;

    public enum VrstaSkole {
        GIMNAZIJA,
        STRUCNA,
        UMETNICKA,
        SPECIJALNA
    }


}
