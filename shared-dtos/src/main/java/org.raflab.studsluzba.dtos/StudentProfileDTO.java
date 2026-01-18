package org.raflab.studsluzba.dtos;

import java.util.List;
import org.raflab.studsluzba.dtos.UplataResponse;

import lombok.Data;

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
    private List<UplataResponse> uplate;
}

