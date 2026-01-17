package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.model.entities.Nastavnik;
import org.raflab.studsluzba.model.entities.NastavnikZvanje;
import org.raflab.studsluzba.repositories.NastavnikRepository;
import org.raflab.studsluzba.repositories.NastavnikZvanjeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class NastavnikService {

    final NastavnikRepository nastavnikRepository;
    final NastavnikZvanjeRepository nastavnikZvanjeRepository;

    public Long addNastavnik(Nastavnik nastavnik) {
        return nastavnikRepository.save(nastavnik).getId();
    }

    public Iterable<Nastavnik> findAll() {
        return nastavnikRepository.findAll();
    }

    public Optional<Nastavnik> findById(Long id) {
        return nastavnikRepository.findById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!nastavnikRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Nastavnik sa ID " + id + " ne postoji.");
        }

        try {
            // PRVO OBRIŠI SVA ZVANJA (child records)
            Nastavnik nastavnik = nastavnikRepository.findById(id).get();
            if (nastavnik.getZvanja() != null && !nastavnik.getZvanja().isEmpty()) {
                for (NastavnikZvanje zvanje : nastavnik.getZvanja()) {
                    nastavnikZvanjeRepository.deleteById(zvanje.getId());
                }
            }

            // ZATIM OBRIŠI NASTAVNIKA
            nastavnikRepository.deleteById(id);

        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ne može se obrisati nastavnik jer postoje povezani zapisi " +
                            "(predmeti koje drži, ispiti koje je držao). " +
                            "Prvo uklonite te reference.");
        }
    }

    public List<Nastavnik> findByImeAndPrezime(String ime, String prezime) {
        return nastavnikRepository.findByImeAndPrezime(ime, prezime);
    }
}