package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.model.entities.DrziPredmet;
import org.raflab.studsluzba.model.entities.Ispit;
import org.raflab.studsluzba.model.entities.Nastavnik;
import org.raflab.studsluzba.model.entities.NastavnikZvanje;
import org.raflab.studsluzba.repositories.DrziPredmetRepository;
import org.raflab.studsluzba.repositories.IspitRepository;
import org.raflab.studsluzba.repositories.NastavnikRepository;
import org.raflab.studsluzba.repositories.NastavnikZvanjeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@AllArgsConstructor
@Service
public class NastavnikService {

    final NastavnikRepository nastavnikRepository;
    final NastavnikZvanjeRepository nastavnikZvanjeRepository;
    final DrziPredmetRepository drziPredmetRepository;
    final IspitRepository ispitRepository;

    @Transactional
    public Long addNastavnik(Nastavnik nastavnik) {
        return nastavnikRepository.save(nastavnik).getId();
    }

    @Transactional
    public Iterable<Nastavnik> findAll() {
        return nastavnikRepository.findAll();
    }

    @Transactional
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
            Nastavnik nastavnik = nastavnikRepository.findById(id).get();

            if (nastavnik.getZvanja() != null && !nastavnik.getZvanja().isEmpty()) {
                for (NastavnikZvanje zvanje : nastavnik.getZvanja()) {
                    nastavnikZvanjeRepository.deleteById(zvanje.getId());
                }
            }

            List<DrziPredmet> drziPredmeti = StreamSupport
                    .stream(drziPredmetRepository.findAll().spliterator(), false)
                    .filter(dp -> dp.getNastavnik() != null && dp.getNastavnik().getId().equals(id))
                    .collect(Collectors.toList());

            if (!drziPredmeti.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Ne može se obrisati nastavnik jer drži " + drziPredmeti.size() + " predmet(a). Prvo uklonite te veze.");
            }

            List<Ispit> ispiti = StreamSupport
                    .stream(ispitRepository.findAll().spliterator(), false)
                    .filter(i -> i.getNastavnik() != null && i.getNastavnik().getId().equals(id))
                    .collect(Collectors.toList());

            if (!ispiti.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Ne može se obrisati nastavnik jer ima " + ispiti.size() + " zakazan(ih) ispit(a).");
            }

            nastavnikRepository.deleteById(id);

        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ne može se obrisati nastavnik jer postoje povezani zapisi.");
        }
    }

    @Transactional
    public List<Nastavnik> findByImeAndPrezime(String ime, String prezime) {
        return nastavnikRepository.findByImeAndPrezime(ime, prezime);
    }
}