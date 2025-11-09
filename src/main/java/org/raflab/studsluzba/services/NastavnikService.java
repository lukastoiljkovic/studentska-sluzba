package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.model.entities.Nastavnik;
import org.raflab.studsluzba.repositories.NastavnikRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class NastavnikService {

    final NastavnikRepository nastavnikRepository;

    public Long addNastavnik(Nastavnik nastavnik) {
        return nastavnikRepository.save(nastavnik).getId();
    }

    public Iterable<Nastavnik> findAll() {
        return nastavnikRepository.findAll();
    }

    public Optional<Nastavnik> findById(Long id) {
        return nastavnikRepository.findById(id);
    }

    public List<Nastavnik> findByImeAndPrezime(String ime, String prezime) {
        return nastavnikRepository.findByImeAndPrezime(ime, prezime);
    }
}
