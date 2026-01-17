package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.entities.NastavnikZvanje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NastavnikZvanjeRepository extends JpaRepository<NastavnikZvanje, Long> {
}
