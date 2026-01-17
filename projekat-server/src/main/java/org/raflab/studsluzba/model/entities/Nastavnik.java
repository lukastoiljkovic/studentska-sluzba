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

     @Column(nullable = false) private String ime;
     @Column(nullable = false) private String prezime;
     @Column(nullable = false) private String srednjeIme;
     @Column(nullable = false) private String email;
	 private String brojTelefona;
	 private String adresa;

	 @OneToMany(mappedBy = "nastavnik", fetch = FetchType.EAGER)
	 private Set<NastavnikZvanje> zvanja;

     @Column(nullable = false) private LocalDate datumRodjenja;
     @Column(nullable = false) private Character pol;
     @Column(nullable = false) private String jmbg;

     @ManyToMany
     private Set<VisokoskolskaUstanova> zavrseneUstanove;


}
