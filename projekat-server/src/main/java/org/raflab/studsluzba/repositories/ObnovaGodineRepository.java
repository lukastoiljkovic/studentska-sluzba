package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.entities.DrziPredmet;
import org.raflab.studsluzba.model.entities.ObnovaGodine;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface ObnovaGodineRepository extends CrudRepository<ObnovaGodine, Long> {

    List<ObnovaGodine> findByStudentIndeksId(Long studentIndeksId);

    List<ObnovaGodine> findByIdIn(Set<Long> ids);

    @Query("SELECT o FROM ObnovaGodine o LEFT JOIN FETCH o.predmetiKojeObnavlja")
    List<ObnovaGodine> findAllWithPredmeti();


}
