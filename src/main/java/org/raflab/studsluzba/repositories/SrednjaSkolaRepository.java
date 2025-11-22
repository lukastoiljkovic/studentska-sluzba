package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.entities.DrziPredmet;
import org.raflab.studsluzba.model.entities.SrednjaSkola;
import org.springframework.data.repository.CrudRepository;

public interface SrednjaSkolaRepository extends CrudRepository<SrednjaSkola, Long> {
}
