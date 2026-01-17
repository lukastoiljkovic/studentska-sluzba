package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.entities.IspitPrijava;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IspitPrijavaRepository extends JpaRepository<IspitPrijava, Long> {

    // sve prijave za dati ispit (možeš dodati i ORDER BY studentIndeks.godina, broj)
    @Query("SELECT ip FROM IspitPrijava ip " +
            "JOIN FETCH ip.studentIndeks si " +
            "JOIN FETCH si.student s " +
            "WHERE ip.ispit.id = :ispitId")
    List<IspitPrijava> findAllByIspitId(@Param("ispitId") Long ispitId);

    boolean existsByStudentIndeksIdAndIspitId(Long studentIndeksId, Long ispitId);

}
