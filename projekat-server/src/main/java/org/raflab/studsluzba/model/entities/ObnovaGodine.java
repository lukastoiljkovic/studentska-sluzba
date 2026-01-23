package org.raflab.studsluzba.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"studentIndeks", "skolskaGodina", "predmetiKojeObnavlja"})
public class ObnovaGodine {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @EqualsAndHashCode.Include
    private Long id;

    private Integer godinaStudija;
    private LocalDate datum;
    private String napomena;

    @ManyToOne(fetch = FetchType.LAZY)
    //@JsonIgnoreProperties({"obnove", "upisi", "student"})
    private StudentIndeks studentIndeks;

    @ManyToOne(fetch = FetchType.EAGER)
    //@JsonIgnoreProperties({"drziPredmetList", "slusaPredmetList", "ispitniRokovi"})
    private SkolskaGodina skolskaGodina;

    @ManyToMany(fetch = FetchType.LAZY)
    //@JsonIgnoreProperties({"drziPredmet", "studentIndeks", "skolskaGodina"})
    private Set<SlusaPredmet> predmetiKojeObnavlja;
}
