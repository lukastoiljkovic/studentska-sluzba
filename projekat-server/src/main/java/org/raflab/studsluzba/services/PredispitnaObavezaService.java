package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzba.model.entities.PredispitnaObaveza;
import org.raflab.studsluzba.model.entities.Predmet;
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

@Service
@AllArgsConstructor
public class PredispitnaObavezaService {

    private final PredispitnaObavezaRepository predispitnaObavezaRepository;
    private final PredmetRepository predmetRepository;

    public Long addPredispitnaObaveza(PredispitnaObavezaRequest req) {
        Predmet predmet = predmetRepository.findById(req.getPredmetId()).orElseThrow();
        PredispitnaObaveza p = Converters.toPredispitnaObaveza(req, predmet);
        return predispitnaObavezaRepository.save(p).getId();
    }

    public Optional<PredispitnaObaveza> findById(Long id) {
        return predispitnaObavezaRepository.findById(id);
    }

    public List<PredispitnaObaveza> findAll() {
        return (List<PredispitnaObaveza>) predispitnaObavezaRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        if (!predispitnaObavezaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Entitet sa ID " + id + " ne postoji.");
        }
        try {
            predispitnaObavezaRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ne moze se obrisati entitet jer postoje povezani zapisi.");
        }
    }
}
