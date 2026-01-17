package org.raflab.studsluzba.repositories;

import org.raflab.studsluzba.model.entities.SkolskaGodina;
import org.raflab.studsluzba.model.entities.StudentIndeks;
import org.raflab.studsluzba.model.entities.UpisGodine;
import org.raflab.studsluzba.model.entities.Uplata;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UpisGodineRepository extends CrudRepository<UpisGodine, Long> {
    List<UpisGodine> findByIdIn(Set<Long> ids);

    List<UpisGodine> findByStudentIndeksStudProgramOznakaAndStudentIndeksGodinaAndStudentIndeksBroj(String studProgramOznaka, int godina, int broj);

    // metoda koju hocemo pozvati iz servisa
    Optional<UpisGodine> findByStudentIndeksAndSkolskaGodina(StudentIndeks studentIndeks, SkolskaGodina skolskaGodina);

    // mozes imati i listu, ako je moguce vise upisa (recimo ako dodjes na master)
    List<UpisGodine> findAllByStudentIndeksAndSkolskaGodina(StudentIndeks studentIndeks, SkolskaGodina skolskaGodina);

}
