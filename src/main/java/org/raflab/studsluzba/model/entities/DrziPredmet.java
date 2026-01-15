package org.raflab.studsluzba.model.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Data
@ToString(exclude = {
        "nastavnik",
        "predmet",
        "skolskaGodina"
})
@EqualsAndHashCode(exclude = {
        "nastavnik",
        "predmet",
        "skolskaGodina"
})
@Entity
public class DrziPredmet {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Nastavnik nastavnik;

    @ManyToOne(fetch = FetchType.LAZY)
    private Predmet predmet;

    @ManyToOne(fetch = FetchType.LAZY)
    private SkolskaGodina skolskaGodina;
}
