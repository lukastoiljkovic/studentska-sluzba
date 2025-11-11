package org.raflab.studsluzba.model.entities;

import javax.persistence.*;

import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Set;

@Entity @Data @ToString(exclude = {"studProgram"})
public class Predmet {
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

    @Column(nullable = false) private String sifra;
    @Column(nullable = false) private String naziv;
	private String opis;
    @Column(nullable = false) private Integer espb;
    @Column(nullable = false) private boolean obavezan;
    @Column(nullable = false) private Integer semestar;
    private Integer fondPredavanja;
    private Integer fondVezbi;

	@ManyToOne
	private StudijskiProgram studProgram;

    @OneToMany(mappedBy = "predmet", fetch = FetchType.EAGER)
    private Set<PredispitnaObaveza> predispitneObaveze;






    ///
    ///



////////////////////////////////////

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sifra == null) ? 0 : sifra.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Predmet other = (Predmet) obj;
		if (sifra == null) {
			if (other.sifra != null)
				return false;
		} else if (!sifra.equals(other.sifra))
			return false;
		return true;
	}

}
