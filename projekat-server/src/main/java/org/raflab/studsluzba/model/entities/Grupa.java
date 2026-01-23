package org.raflab.studsluzba.model.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Set;

import javax.persistence.*;

@Entity @Data @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Grupa {
    // ova klasa nije navedena u specifikaciji ali postoji u pocetnom projektu

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY) @EqualsAndHashCode.Include
	private Long id;

    private String naziv; // npr 311 312 313
	
	@ManyToOne
	private StudijskiProgram studijskiProgram;

    @OneToMany(mappedBy = "grupa")
    private Set<SlusaPredmet> studenti;

    @ManyToOne
    private SkolskaGodina skolskaGodina; // menjaju se svake godine

}
