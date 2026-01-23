package org.raflab.studsluzba.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.IspitPrijavaRequest;
import org.raflab.studsluzba.model.entities.Ispit;
import org.raflab.studsluzba.model.entities.IspitPrijava;
import org.raflab.studsluzba.model.entities.PolozenPredmet;
import org.raflab.studsluzba.model.entities.StudentIndeks;
import org.raflab.studsluzba.repositories.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.raflab.studsluzba.dtos.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class IspitPrijavaService {

    private final IspitPrijavaRepository ispitPrijavaRepository;
    private final StudentIndeksRepository studentIndeksRepository;
    private final IspitRepository ispitRepository;
    private final PolozenPredmetRepository polozenPredmetRepository;
    private final IspitIzlazakRepository ispitIzlazakRepository;

    @Transactional
    public Long add(IspitPrijavaRequest req) {
        StudentIndeks si = studentIndeksRepository.findById(req.getStudentIndeksId()).orElseThrow();
        Ispit ispit = ispitRepository.findById(req.getIspitId()).orElseThrow();

        IspitPrijava p = new IspitPrijava();
        p.setDatum(req.getDatum());
        p.setStudentIndeks(si);
        p.setIspit(ispit);

        return ispitPrijavaRepository.save(p).getId();
    }

    @Transactional
    public Optional<IspitPrijava> findById(Long id) {
        return ispitPrijavaRepository.findById(id);
    }

    @Transactional
    public List<IspitPrijava> findAll() {
        return (List<IspitPrijava>) ispitPrijavaRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        if (!ispitPrijavaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Prijava sa ID " + id + " ne postoji.");
        }

        try {
            IspitPrijava prijava = ispitPrijavaRepository.findById(id).get();

            if (prijava.getIspitIzlazak() != null) {
                Long izlazakId = prijava.getIspitIzlazak().getId();

                List<PolozenPredmet> polozeni = StreamSupport
                        .stream(polozenPredmetRepository.findAll().spliterator(), false)
                        .filter(pp -> pp.getIspitIzlazak() != null && pp.getIspitIzlazak().getId().equals(izlazakId))
                        .collect(Collectors.toList());

                polozeni.forEach(pp -> polozenPredmetRepository.deleteById(pp.getId()));

                ispitIzlazakRepository.deleteById(izlazakId);
            }

            ispitPrijavaRepository.deleteById(id);

        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ne može se obrisati prijava jer postoje povezani zapisi.");
        }
    }
}
