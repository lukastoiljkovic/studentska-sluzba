package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzba.model.entities.SrednjaSkola;
import org.raflab.studsluzba.model.entities.StudentPodaci;
import org.raflab.studsluzba.repositories.SrednjaSkolaRepository;
import org.raflab.studsluzba.repositories.StudentPodaciRepository;
import org.raflab.studsluzba.utils.EntityMappers;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.raflab.studsluzba.dtos.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SrednjaSkolaService {

    private final SrednjaSkolaRepository srednjaSkolaRepository;
    private final EntityMappers entityMappers;
    private final StudentPodaciRepository studentPodaciRepository;

    @Transactional
    public SrednjaSkola addSrednjaSkola(SrednjaSkola ss) {
        return srednjaSkolaRepository.save(ss);
    }

    @Transactional
    public Optional<SrednjaSkola> findById(Long id) {
        return srednjaSkolaRepository.findById(id);
    }

    @Transactional
    public Optional<SrednjaSkola> findByNaziv(String naziv) {
        return srednjaSkolaRepository.findByNaziv(naziv);
    }

    @Transactional
    public List<SrednjaSkola> findAll() {
        return srednjaSkolaRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        if (!srednjaSkolaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Entitet sa ID " + id + " ne postoji.");
        }
        try {
            srednjaSkolaRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ne može se obrisati entitet jer postoje povezani zapisi.");
        }
    }

    @Transactional
    public List<StudentPodaciResponse> getStudentiPoSrednjojSkoli(SrednjaSkola skola) {
        List<StudentPodaci> studenti = studentPodaciRepository.findBySrednjaSkola(skola);
        return studenti.stream()
                .map(entityMappers::fromStudentPodaciToResponse)
                .collect(Collectors.toList());

    }
}
