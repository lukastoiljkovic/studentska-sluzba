package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.entities.Uplata;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UplataRepository extends CrudRepository<Uplata, Integer> {
    List<Uplata> findByUpisGodineId(Long upisGodineId);
}
