package org.raflab.studsluzba.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

@Data
@ToString(exclude = {
        "studentIndeks",
        "drziPredmet",
        "predispitneObaveze",
        "grupa",
        "skolskaGodina",
        "obnove"
})
@EqualsAndHashCode(exclude = {
        "studentIndeks",
        "drziPredmet",
        "skolskaGodina",
        "obnove"
})
@Entity
public class SlusaPredmet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private StudentIndeks studentIndeks;

    @ManyToOne
    private DrziPredmet drziPredmet;

    @ManyToOne
    private SkolskaGodina skolskaGodina;

    @OneToMany(mappedBy = "slusaPredmet")
    private Set<PredispitnaIzlazak> predispitneObaveze;

    @ManyToOne
    private Grupa grupa;

    @ManyToMany(mappedBy = "predmetiKojeObnavlja")
    @JsonIgnore // ovo je ključno da se ne pravi ciklus
    private Set<ObnovaGodine> obnove;
}
