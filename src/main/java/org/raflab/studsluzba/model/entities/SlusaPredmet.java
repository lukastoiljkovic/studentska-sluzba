package org.raflab.studsluzba.model.entities;

import javax.persistence.*;

import lombok.Data;

import java.util.Set;

@Entity
@Data
public class SlusaPredmet {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@ManyToOne	
	private StudentIndeks studentIndeks;
	
	@ManyToOne
	private DrziPredmet drziPredmet;

    @ManyToOne
    private SkolskaGodina skolskaGodina; // dodato

    @OneToMany(mappedBy = "slusaPredmet")
    private Set<PredispitnaIzlazak> predispitneObaveze;

    @ManyToOne
    private Grupa grupa; // dodato, grupa u kojoj student slusa predmet
    // grupa se menja svake godine, zato je ovde a ne u StudentIndeks
}
