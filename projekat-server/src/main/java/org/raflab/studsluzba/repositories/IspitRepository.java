package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.entities.DrziPredmet;
import org.raflab.studsluzba.model.entities.Ispit;
import org.springframework.data.repository.CrudRepository;

public interface IspitRepository extends CrudRepository<Ispit, Long> {
}
