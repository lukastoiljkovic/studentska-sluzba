package org.raflab.studsluzba.model.dtos;

import java.util.List;

import lombok.Data;
import org.raflab.studsluzba.model.entities.Predmet;
import org.raflab.studsluzba.model.entities.SlusaPredmet;
import org.raflab.studsluzba.model.entities.StudentIndeks;

/*
 * objekat ove kalse sadrzi sve podatke o studentu koji
 * se prikazuju u njegovom profilu  
 * 
 * - polozeni predmeti
 * - tok studija (upisi, obnove godina)
 * - predmete koje slusa
 * - prijavljeni ispiti
 * - uplate
 * 
 * - selektujemo preko indeksa, potrebno prikupiti podatke i o drugim indeksima
 * 
 */

@Data
public class StudentProfileDTO {
    private StudentIndeksDTO indeks;
    private List<SlusaPredmetDTO> slusaPredmete;
    private List<String> nepolozeniPredmeti; // samo nazivi predmeta
}

