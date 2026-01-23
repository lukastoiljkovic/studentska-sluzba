package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzba.model.entities.PredispitnaIzlazak;
import org.raflab.studsluzba.model.entities.PredispitnaObaveza;
import org.raflab.studsluzba.model.entities.Predmet;
import org.raflab.studsluzba.repositories.PredispitnaIzlazakRepository;
import org.raflab.studsluzba.repositories.PredispitnaObavezaRepository;
import org.raflab.studsluzba.repositories.PredmetRepository;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class PredispitnaObavezaService {

    private final PredispitnaObavezaRepository predispitnaObavezaRepository;
    private final PredmetRepository predmetRepository;
    private final PredispitnaIzlazakRepository predispitnaIzlazakRepository;

    @Transactional
    public Long addPredispitnaObaveza(PredispitnaObavezaRequest req) {
        Predmet predmet = predmetRepository.findById(req.getPredmetId()).orElseThrow();
        PredispitnaObaveza p = Converters.toPredispitnaObaveza(req, predmet);
        return predispitnaObavezaRepository.save(p).getId();
    }

    @Transactional
    public Optional<PredispitnaObaveza> findById(Long id) {
        return predispitnaObavezaRepository.findById(id);
    }

    @Transactional
    public List<PredispitnaObaveza> findAll() {
        return (List<PredispitnaObaveza>) predispitnaObavezaRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        if (!predispitnaObavezaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Predispitna obaveza sa ID " + id + " ne postoji.");
        }

        try {
            List<PredispitnaIzlazak> izlasci = StreamSupport
                    .stream(predispitnaIzlazakRepository.findAll().spliterator(), false)
                    .filter(pi -> pi.getPredispitnaObaveza() != null && pi.getPredispitnaObaveza().getId().equals(id))
                    .collect(Collectors.toList());

            izlasci.forEach(pi -> predispitnaIzlazakRepository.deleteById(pi.getId()));

            predispitnaObavezaRepository.deleteById(id);

        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ne može se obrisati predispitna obaveza jer postoje povezani zapisi: " + e.getMessage());
        }
    }
}
