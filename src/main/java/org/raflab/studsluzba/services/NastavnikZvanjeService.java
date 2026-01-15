package org.raflab.studsluzba.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.controllers.request.NastavnikZvanjeRequest;
import org.raflab.studsluzba.model.entities.Nastavnik;
import org.raflab.studsluzba.model.entities.NastavnikZvanje;
import org.raflab.studsluzba.repositories.NastavnikRepository;
import org.raflab.studsluzba.repositories.NastavnikZvanjeRepository;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NastavnikZvanjeService {

    private final NastavnikZvanjeRepository nastavnikZvanjeRepository;
    private final NastavnikRepository nastavnikRepository;

    public Long add(NastavnikZvanjeRequest req) {
        Nastavnik n = nastavnikRepository.findById(req.getNastavnikId()).orElseThrow();

        NastavnikZvanje nz = Converters.toNastavnikZvanje(req, n);
        return nastavnikZvanjeRepository.save(nz).getId();
    }

    public Optional<NastavnikZvanje> findById(Long id) {
        return nastavnikZvanjeRepository.findById(id);
    }

    public List<NastavnikZvanje> findAll() {
        return nastavnikZvanjeRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        if (!nastavnikZvanjeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Entitet sa ID " + id + " ne postoji.");
        }
        try {
            nastavnikZvanjeRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ne moze se obrisati entitet jer postoje povezani zapisi.");
        }
    }
}
