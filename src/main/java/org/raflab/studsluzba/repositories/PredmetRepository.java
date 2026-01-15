package org.raflab.studsluzba.repositories;

import java.util.List;

import org.raflab.studsluzba.model.entities.Predmet;
import org.raflab.studsluzba.model.entities.SlusaPredmet;
import org.raflab.studsluzba.model.entities.StudijskiProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PredmetRepository extends JpaRepository<Predmet, Long> {

    @Query("select p from Predmet p where p.studProgram.godinaAkreditacije = :godinaAkreditacije")
    List<Predmet> getPredmetForGodinaAkreditacije(Integer godinaAkreditacije);

    List<Predmet> getPredmetsByStudProgramAndObavezan(StudijskiProgram studProgram, boolean obavezan);

    List<Predmet> findByIdIn(List<Long> ids);
    List<Predmet> findByNazivIn(List<String> nazivi);
    List<Predmet> findByStudProgramIdOrderBySemestarAscNazivAsc(Long studProgramId);
    boolean existsBySifraIgnoreCase(String sifra);

    @Query("select sp from SlusaPredmet sp where sp.studentIndeks.id = :indeksId")
    List<SlusaPredmet> getSlusaPredmetForIndeksAktivnaGodina(@Param("indeksId") Long indeksId);
}