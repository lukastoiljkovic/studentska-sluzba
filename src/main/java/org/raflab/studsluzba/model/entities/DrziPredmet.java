package org.raflab.studsluzba.model.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class DrziPredmet {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Nastavnik nastavnik;

	@ManyToOne
	private Predmet predmet;

	@ManyToOne
	private SkolskaGodina skolskaGodina;
}
