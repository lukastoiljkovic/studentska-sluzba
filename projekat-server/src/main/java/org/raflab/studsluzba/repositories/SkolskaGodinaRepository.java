package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.entities.SkolskaGodina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SkolskaGodinaRepository extends JpaRepository<SkolskaGodina, Long> {

    Optional<SkolskaGodina> findByAktivnaTrue();
    Optional<SkolskaGodina> findByNaziv(String naziv); // npr. "2025/202
}