package org.raflab.studsluzba.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.IspitPrijavaRequest;
import org.raflab.studsluzba.model.entities.Ispit;
import org.raflab.studsluzba.model.entities.IspitPrijava;
import org.raflab.studsluzba.model.entities.StudentIndeks;
import org.raflab.studsluzba.repositories.IspitPrijavaRepository;
import org.raflab.studsluzba.repositories.IspitRepository;
import org.raflab.studsluzba.repositories.StudentIndeksRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.raflab.studsluzba.dtos.*;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IspitPrijavaService {

    private final IspitPrijavaRepository ispitPrijavaRepository;
    private final StudentIndeksRepository studentIndeksRepository;
    private final IspitRepository ispitRepository;

    public Long add(IspitPrijavaRequest req) {
        StudentIndeks si = studentIndeksRepository.findById(req.getStudentIndeksId()).orElseThrow();
        Ispit ispit = ispitRepository.findById(req.getIspitId()).orElseThrow();

        IspitPrijava p = new IspitPrijava();
        p.setDatum(req.getDatum());
        p.setStudentIndeks(si);
        p.setIspit(ispit);

        return ispitPrijavaRepository.save(p).getId();
    }

    public Optional<IspitPrijava> findById(Long id) {
        return ispitPrijavaRepository.findById(id);
    }

    public List<IspitPrijava> findAll() {
        return (List<IspitPrijava>) ispitPrijavaRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        if (!ispitPrijavaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Entitet sa ID " + id + " ne postoji.");
        }
        try {
            ispitPrijavaRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ne moze se obrisati entitet jer postoje povezani zapisi.");
        }
    }
}
