package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.entities.TokStudija;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokStudijaRepository extends CrudRepository<TokStudija, Long> {
    List<TokStudija> findAllByStudentIndeksId(Long studentIndeksId);

    @Query("SELECT t FROM TokStudija t " +
            "LEFT JOIN FETCH t.upisi " +
            "LEFT JOIN FETCH t.obnove " +
            "WHERE t.id = :id")
    Optional<TokStudija> findByIdWithCollections(@Param("id") Long id);
}
