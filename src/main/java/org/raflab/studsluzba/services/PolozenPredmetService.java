package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.controllers.request.PolozenPredmetRequest;
import org.raflab.studsluzba.controllers.response.PolozenPredmetResponse;
import org.raflab.studsluzba.model.entities.IspitIzlazak;
import org.raflab.studsluzba.model.entities.Predmet;
import org.raflab.studsluzba.model.entities.PolozenPredmet;
import org.raflab.studsluzba.model.entities.StudentIndeks;
import org.raflab.studsluzba.repositories.IspitIzlazakRepository;
import org.raflab.studsluzba.repositories.PredmetRepository;
import org.raflab.studsluzba.repositories.PolozenPredmetRepository;
import org.raflab.studsluzba.repositories.StudentIndeksRepository;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PolozenPredmetService {

    private final PolozenPredmetRepository polozenPredmetRepository;
    private final StudentIndeksRepository studentIndeksRepository;
    private final PredmetRepository predmetRepository;
    private final IspitIzlazakRepository ispitIzlazakRepository;

    public Page<PolozenPredmetResponse> getPolozeniIspiti(Long studentIndeksId, Pageable pageable) {
        return polozenPredmetRepository
                .findByStudentIndeksIdAndOcenaIsNotNull(studentIndeksId, pageable)
                .map(Converters::toPolozenPredmetResponse);  // << ovde promenjeno
    }

    public Page<PolozenPredmetResponse> getNepolozeniIspiti(Long studentIndeksId, Pageable pageable) {
        return polozenPredmetRepository
                .findByStudentIndeksIdAndOcenaIsNull(studentIndeksId, pageable)
                .map(Converters::toPolozenPredmetResponse);  // << ovde promenjeno
    }


    public Long addPolozenPredmet(PolozenPredmetRequest req) {

        StudentIndeks si = studentIndeksRepository.findById(req.getStudentIndeksId()).orElseThrow();
        Predmet p = predmetRepository.findById(req.getPredmetId()).orElseThrow();
        IspitIzlazak izlazak = null;
        if(req.getIspitIzlazakId() != null)
            izlazak = ispitIzlazakRepository.findById(req.getIspitIzlazakId()).orElse(null);

        PolozenPredmet pp = Converters.toPolozenPredmet(req, si, p, izlazak);
        return polozenPredmetRepository.save(pp).getId();
    }

    public Optional<PolozenPredmet> findById(Long id) {
        return polozenPredmetRepository.findById(id);
    }

    public List<PolozenPredmet> findAll() {
        return (List<PolozenPredmet>) polozenPredmetRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        if (!polozenPredmetRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Entitet sa ID " + id + " ne postoji.");
        }
        try {
            polozenPredmetRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ne moze se obrisati entitet jer postoje povezani zapisi.");
        }
    }
}
