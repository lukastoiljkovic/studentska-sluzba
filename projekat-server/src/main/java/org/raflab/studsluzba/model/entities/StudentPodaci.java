package org.raflab.studsluzba.model.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

import javax.persistence.*;

@Data
@ToString(exclude = {
        "srednjaSkola",
        "prethodnaUstanova"
})
@EqualsAndHashCode(exclude = {
        "srednjaSkola",
        "prethodnaUstanova"
})
@Entity
public class StudentPodaci {


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @Column(nullable = false) private String ime;
    @Column(nullable = false) private String prezime;
    @Column(nullable = false) private String srednjeIme;

    @Column(unique = true) private String jmbg;
    @Column(nullable = false) private Character pol;

    @Column(nullable = false) private LocalDate datumRodjenja;
    @Column(nullable = false) private String drzavaRodjenja;
    @Column(nullable = false) private String mestoRodjenja;

    @Column(nullable = false) private String drzavljanstvo;
    private String nacionalnost;

    @Column(nullable = false) private String mestoPrebivalista;
    @Column(nullable = false) private String adresaPrebivalista;

    private String brojTelefonaMobilni;
    private String brojTelefonaFiksni;

    @Column(nullable = false) private String emailFakultetski;
    @Column(nullable = false) private String emailPrivatni;

    @Column(nullable = false) private String brojLicneKarte;
    @Column(nullable = false) private String licnuKartuIzdao;

    @ManyToOne
    private SrednjaSkola srednjaSkola; // iz šifarnika srednjih škola

    private Double uspehSrednjaSkola;
    private Double uspehPrijemni;

    @ManyToOne
    private VisokoskolskaUstanova prethodnaUstanova; // ako je prelazio
}

	/*
	ORIGINALNI KOD:
	 @Id
	 @GeneratedValue(strategy=GenerationType.IDENTITY)
	 private Long id;
	 private String ime;	  // not null
	 private String prezime;  // not null
	 private String srednjeIme;   // not null 
	 private String jmbg;    
	 private LocalDate datumRodjenja;  // not null
	 private String mestoRodjenja;
	 private String mestoPrebivalista;  // not null
	 private String drzavaRodjenja;   
	 private String drzavljanstvo;   // not null
	 private String nacionalnost;   // samoizjasnjavanje, moze bilo sta  
	 private Character pol;    // not null
	 private String adresa;  // not null
	 private String brojTelefonaMobilni;  
	 private String brojTelefonaFiksni;
	 private String email;  // not null
	 private String brojLicneKarte; 
	 private String licnuKartuIzdao;
	 private String mestoStanovanja;
	 private String adresaStanovanja;   // u toku studija
*/
