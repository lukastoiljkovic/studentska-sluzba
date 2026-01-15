package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.entities.DrziPredmet;
import org.raflab.studsluzba.model.entities.SrednjaSkola;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SrednjaSkolaRepository extends JpaRepository<SrednjaSkola, Long> {

    Optional<SrednjaSkola> findByNaziv(String naziv);
}
