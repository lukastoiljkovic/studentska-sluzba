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
    private SrednjaSkola srednjaSkola;

    private Double uspehSrednjaSkola;
    private Double uspehPrijemni;

    @ManyToOne
    private VisokoskolskaUstanova prethodnaUstanova; // ako je prelazio
}
