package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.controllers.request.PredispitnaIzlazakRequest;
import org.raflab.studsluzba.model.entities.PredispitnaIzlazak;
import org.raflab.studsluzba.model.entities.PredispitnaObaveza;
import org.raflab.studsluzba.model.entities.SlusaPredmet;
import org.raflab.studsluzba.repositories.PredispitnaIzlazakRepository;
import org.raflab.studsluzba.repositories.PredispitnaObavezaRepository;
import org.raflab.studsluzba.repositories.SlusaPredmetRepository;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PredispitnaIzlazakService {

    private final PredispitnaIzlazakRepository izlazakRepository;
    private final SlusaPredmetRepository slusaPredmetRepository;
    private final PredispitnaObavezaRepository predispitnaObavezaRepository;

    public Long addPredispitnaIzlazak(PredispitnaIzlazakRequest req) {
        SlusaPredmet sp = slusaPredmetRepository.findById(req.getSlusaPredmetId()).orElseThrow();
        PredispitnaObaveza po = predispitnaObavezaRepository.findById(req.getPredispitnaObavezaId()).orElseThrow();
        PredispitnaIzlazak izlazak = Converters.toPredispitnaIzlazak(req, sp, po);
        return izlazakRepository.save(izlazak).getId();
    }

    public PredispitnaIzlazak findById(Long id) {
        return izlazakRepository.findById(id).orElse(null);
    }

    public List<PredispitnaIzlazak> findAll() {
        return (List<PredispitnaIzlazak>) izlazakRepository.findAll();
    }

    public void deleteById(Long id) {
        izlazakRepository.deleteById(id);
    }
}