package org.raflab.studsluzba.model.entities;

import java.time.LocalDate;
import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(exclude = {
        "student",
        "studijskiProgram"
})
@EqualsAndHashCode(exclude = {
        "student",
        "studijskiProgram"
})
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"broj", "godina", "studProgramOznaka", "aktivan"}))
public class StudentIndeks {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

    private int broj;
    @Column(nullable = false) private int godina;
    @Column(nullable = false) private String studProgramOznaka;

	private String nacinFinansiranja;
	private boolean aktivan; 
	private LocalDate vaziOd;
    private Integer ostvarenoEspb;

	@ManyToOne
	private StudentPodaci student;
	
	@ManyToOne
	private StudijskiProgram studijskiProgram;   // na koji studijski program je upisan

}
