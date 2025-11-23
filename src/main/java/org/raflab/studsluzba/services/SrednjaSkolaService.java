package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.controllers.response.StudentPodaciResponse;
import org.raflab.studsluzba.model.entities.SrednjaSkola;
import org.raflab.studsluzba.model.entities.StudentPodaci;
import org.raflab.studsluzba.repositories.SrednjaSkolaRepository;
import org.raflab.studsluzba.repositories.StudentPodaciRepository;
import org.raflab.studsluzba.utils.EntityMappers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SrednjaSkolaService {

    private final SrednjaSkolaRepository srednjaSkolaRepository;
    private final EntityMappers entityMappers;
    private final StudentPodaciRepository studentPodaciRepository;

    public SrednjaSkola addSrednjaSkola(SrednjaSkola ss) {
        return srednjaSkolaRepository.save(ss);
    }

    public Optional<SrednjaSkola> findById(Long id) {
        return srednjaSkolaRepository.findById(id);
    }

    public List<SrednjaSkola> findAll() {
        return srednjaSkolaRepository.findAll();
    }

    public void deleteById(Long id) {
        srednjaSkolaRepository.deleteById(id);
    }

    public List<StudentPodaciResponse> getStudentiPoSrednjojSkoli(SrednjaSkola skola) {
        List<StudentPodaci> studenti = studentPodaciRepository.findBySrednjaSkola(skola);
        return studenti.stream()
                .map(entityMappers::fromStudentPodaciToResponse)
                .collect(Collectors.toList());

    }
}
