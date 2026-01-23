package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.entities.DrziPredmet;
import org.raflab.studsluzba.model.entities.IspitIzlazak;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IspitIzlazakRepository extends CrudRepository<IspitIzlazak, Long> {
    long countByStudentIndeks_IdAndIspitPrijava_Ispit_Predmet_Id(Long studentIndeksId, Long predmetId);

    // poslednji neponisten izlazak za datu prijavu
    Optional<IspitIzlazak> findTopByIspitPrijava_IdAndPonistavaFalseOrderByIdDesc(Long ispitPrijavaId);
}
