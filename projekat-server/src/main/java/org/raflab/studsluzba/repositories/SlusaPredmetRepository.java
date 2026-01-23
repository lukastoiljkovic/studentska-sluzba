package org.raflab.studsluzba.repositories;

import java.util.List;
import java.util.Set;

import org.raflab.studsluzba.model.entities.SlusaPredmet;
import org.raflab.studsluzba.model.entities.StudentIndeks;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SlusaPredmetRepository extends CrudRepository<SlusaPredmet, Long> {

    @Query("select sp from SlusaPredmet sp where sp.studentIndeks.id = :indeksId")
    List<SlusaPredmet> getSlusaPredmetForIndeksAktivnaGodina(Long indeksId);

    @Query("select sp.studentIndeks from SlusaPredmet sp where sp.drziPredmet.predmet.id = :idPredmeta "
            + "and sp.drziPredmet.nastavnik.id = :idNastavnika  ")
    List<StudentIndeks> getStudentiSlusaPredmetAktivnaGodina(Long idPredmeta, Long idNastavnika);

    @Query("select sp.studentIndeks from SlusaPredmet sp where sp.drziPredmet.id = :idDrziPredmet")
    List<StudentIndeks> getStudentiSlusaPredmetZaDrziPredmet(Long idDrziPredmet);

    boolean existsByStudentIndeksIdAndDrziPredmet_Predmet_IdAndSkolskaGodina_Id(
            Long studentIndeksId, Long predmetId, Long skolskaGodinaId);

    @Query("select si from StudentIndeks si where not exists "
            + "(select sp from SlusaPredmet sp where sp.studentIndeks=si and sp.drziPredmet.id = :idDrziPredmet) ")
    List<StudentIndeks> getStudentiNeSlusajuDrziPredmet(Long idDrziPredmet);

    List<SlusaPredmet> findByIdIn(Set<Long> ids);

    // NOVA METODA - sve što student sluša
    @Query("SELECT sp FROM SlusaPredmet sp " +
            "LEFT JOIN FETCH sp.drziPredmet dp " +
            "LEFT JOIN FETCH dp.predmet " +
            "WHERE sp.studentIndeks.id = :studentIndeksId")
    List<SlusaPredmet> findAllByStudentIndeksIdWithPredmet(@Param("studentIndeksId") Long studentIndeksId);


    @Query("SELECT sp FROM SlusaPredmet sp " +
            "LEFT JOIN FETCH sp.drziPredmet dp " +
            "LEFT JOIN FETCH dp.nastavnik " +
            "LEFT JOIN FETCH dp.predmet " +
            "WHERE sp.studentIndeks.id = :indeksId")
    List<SlusaPredmet> getSlusaPredmetForIndeksAktivnaGodinaWithDetails(@Param("indeksId") Long indeksId);

}
