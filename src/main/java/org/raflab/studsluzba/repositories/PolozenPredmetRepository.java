package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.entities.DrziPredmet;
import org.raflab.studsluzba.model.entities.PolozenPredmet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PolozenPredmetRepository extends CrudRepository<PolozenPredmet, Long> {

    // polozeni predmeti sa ocenom
    Page<PolozenPredmet> findByStudentIndeksIdAndOcenaIsNotNull(Long studentIndeksId, Pageable pageable);

    // helper metoda - da li je student polozio neki predmet
    boolean existsByStudentIndeksIdAndPredmetId(Long studentIndeksId, Long predmetId);

    // svi polozeni predmeti za studenta bez paginacije
    List<PolozenPredmet> findByStudentIndeksIdAndOcenaIsNotNull(Long studentIndeksId);

    @Query("SELECT AVG(pp.ocena) " +
            "FROM PolozenPredmet pp " +
            "JOIN pp.ispitIzlazak ie " +
            "JOIN ie.ispitPrijava ip " +
            "JOIN ip.ispit i " +
            "JOIN i.ispitniRok ir " +
            "JOIN ir.skolskaGodina sg " +
            "WHERE pp.predmet.id = :predmetId " +
            "AND pp.ocena IS NOT NULL " +
            "AND SUBSTRING(sg.naziv, 1, 4) BETWEEN :fromYear AND :toYear")
    Double findAverageGradeForPredmetAndYearRange(
            @Param("predmetId") Long predmetId,
            @Param("fromYear") String fromYear,
            @Param("toYear") String toYear
    );

    @Query("SELECT AVG(pp.ocena) " +
            "FROM PolozenPredmet pp " +
            "JOIN pp.ispitIzlazak ie " +
            "JOIN ie.ispitPrijava ip " +
            "WHERE ip.ispit.id = :ispitId " +
            "AND pp.ocena IS NOT NULL " +
            "AND ie.ponistava = false")
    Double avgOcenaZaIspit(@Param("ispitId") Long ispitId);

    @Query("SELECT pp FROM PolozenPredmet pp " +
            "WHERE pp.studentIndeks.id = :siId AND pp.predmet.id = :predmetId")
    Optional<PolozenPredmet> findByStudentIndeksAndPredmet(
            @Param("siId") Long siId,
            @Param("predmetId") Long predmetId
    );

    Page<PolozenPredmet> findByStudentIndeksIdAndOcenaIsNull(Long studentIndeksId, Pageable pageable);


}
