package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.entities.DrziPredmet;
import org.raflab.studsluzba.model.entities.TokStudija;
import org.springframework.data.repository.CrudRepository;

public interface TokStudijaRepository extends CrudRepository<TokStudija, Long> {
}
