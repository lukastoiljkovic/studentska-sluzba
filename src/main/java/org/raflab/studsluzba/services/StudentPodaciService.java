package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.controllers.response.StudentPodaciResponse;
import org.raflab.studsluzba.model.entities.StudentIndeks;
import org.raflab.studsluzba.model.entities.StudentPodaci;
import org.raflab.studsluzba.repositories.StudentPodaciRepository;
import org.raflab.studsluzba.utils.EntityMappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service @AllArgsConstructor
public class StudentPodaciService {

    private final StudentPodaciRepository studentPodaciRepository;
    private final EntityMappers entityMappers;

    @Transactional
    public Optional<StudentPodaci> findById(Long id) {
        return studentPodaciRepository.findById(id);
    }

    public List<StudentPodaciResponse> getAllStudentPodaci() {
        return studentPodaciRepository.findAll()
                .stream()
                .map(entityMappers::fromStudentPodaciToResponse)
                .collect(Collectors.toList());
    }

    // vraća stranicu studenata
    public Page<StudentPodaciResponse> getAllStudentPodaciPaginated(int page, int size) {
        return studentPodaciRepository.findAll(
                PageRequest.of(page, size, Sort.by("id").descending())
        ).map(entityMappers::fromStudentPodaciToResponse);
    }

    // po id
    public StudentPodaciResponse getStudentPodaciById(Long id) {
        return studentPodaciRepository.findById(id)
                .map(entityMappers::fromStudentPodaciToResponse)
                .orElse(null);
    }

    // dodavanje novog studenta
    public Long addStudentPodaci(StudentPodaci studentPodaci) {
        return studentPodaciRepository.save(studentPodaci).getId();
    }

    public Page<StudentPodaci> findStudent(String ime, String prezime, Pageable pageable) {
        return studentPodaciRepository.findStudent(ime, prezime, pageable);
    }

    public StudentIndeks getAktivanIndeks(Long studPodaciId){
        return studentPodaciRepository.getAktivanIndeks(studPodaciId);
    }

    public List<StudentIndeks> getNeaktivniIndeksi(Long studPodaciId){
        return studentPodaciRepository.getNeaktivniIndeksi(studPodaciId);
    }

    @Transactional
    public StudentPodaciResponse getStudentPodaci(Long id){
        Optional<StudentPodaci> rez = findById(id);
        return rez.map(entityMappers::fromStudentPodaciToResponse).orElse(null);
    }



}
