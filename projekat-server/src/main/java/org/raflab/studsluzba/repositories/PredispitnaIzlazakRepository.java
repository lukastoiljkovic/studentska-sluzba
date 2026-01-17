package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.entities.DrziPredmet;
import org.raflab.studsluzba.model.entities.PredispitnaIzlazak;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PredispitnaIzlazakRepository extends CrudRepository<PredispitnaIzlazak, Long> {
    @Query("SELECT COALESCE(SUM(pi.poeni), 0) " +
            "FROM PredispitnaIzlazak pi " +
            "JOIN pi.slusaPredmet sp " +
            "WHERE sp.studentIndeks.id = :siId " +
            "AND sp.drziPredmet.predmet.id = :predmetId " +
            "AND sp.skolskaGodina.id = :skGodId")
    Integer sumPoeniZaStudentaPredmetGodinu(@Param("siId") Long studentIndeksId,
                                            @Param("predmetId") Long predmetId,
                                            @Param("skGodId") Long skolskaGodinaId);
    List<PredispitnaIzlazak> findAllBySlusaPredmet_StudentIndeks_IdAndSlusaPredmet_DrziPredmet_Predmet_IdAndSlusaPredmet_SkolskaGodina_Id(
            Long studentIndeksId,
            Long predmetId,
            Long skolskaGodinaId
    );
}
