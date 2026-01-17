package org.raflab.studsluzba.model.entities;

import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Entity
@Data
public class StudijskiProgram {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

    @Column(nullable = false) private String oznaka;  // RN, RI, SI itd.
    @Column(nullable = false) private String naziv;
    @Column(nullable = false) private Integer godinaAkreditacije;
    @Column(nullable = false) private String zvanje;
    @Column(nullable = false) private Integer trajanjeGodina;
    @Column(nullable = false) private Integer trajanjeSemestara;
    @Column(nullable = false) private String vrstaStudija;  // OAS, MAS, OSS, DAS
    @Column(nullable = false) private Integer ukupnoEspb;
	
	@JsonIgnore
	@OneToMany(mappedBy = "studProgram")
	private List<Predmet> predmeti;

    /*public enum Smer{
        RN, RI, SI, RM, IS, IT, MD
    }*/

}
