package org.raflab.studsluzba.model.entities;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity @Data
public class IspitniRok {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String naziv;
    @Column(nullable = false) private LocalDateTime datumPocetka;
    @Column(nullable = false) private LocalDateTime datumZavrsetka;

    @OneToMany(mappedBy = "ispitniRok")
    private Set<Ispit> ispiti;

    @ManyToOne
    private SkolskaGodina skolskaGodina;

}
