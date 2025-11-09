package org.raflab.studsluzba.model.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Set;

import javax.persistence.*;

@Entity
@Data
//Hibernate pokušava da pristupi kolekciji dok ona još nije u stabilnom stanju
// (ili se poredi preko proksi objekata)
//Lombok više ne dira kolekciju zvanja dok pravi toString() ili proverava equals()/hashCode()
@ToString(exclude = "zvanja")
@EqualsAndHashCode(exclude = "zvanja")
public class Nastavnik {
	 
	 @Id
	 @GeneratedValue(strategy=GenerationType.IDENTITY)
	 private Long id;

	 private String ime;
	 private String prezime;
	 private String srednjeIme;
	 private String email;
	 private String brojTelefona;
	 private String adresa;

	 @OneToMany(mappedBy = "nastavnik", fetch = FetchType.EAGER)
	 private Set<NastavnikZvanje> zvanja;
	 
	 private LocalDate datumRodjenja;
	 private Character pol;
	 private String jmbg;

}
