package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzba.model.entities.*;
import org.raflab.studsluzba.repositories.*;
import org.raflab.studsluzba.utils.EntityMappers;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service @AllArgsConstructor
public class StudentPodaciService {

    private final StudentPodaciRepository studentPodaciRepository;
    private final StudentIndeksRepository studentIndeksRepository;
    private final EntityMappers entityMappers;
    private final PolozenPredmetRepository polozenPredmetRepository;
    private final IspitPrijavaRepository ispitPrijavaRepository;
    private final SlusaPredmetRepository slusaPredmetRepository;
    private final PredispitnaIzlazakRepository predispitnaIzlazakRepository;
    private final IspitIzlazakRepository ispitIzlazakRepository;

    @Transactional
    public Optional<StudentPodaci> findById(Long id) {
        return studentPodaciRepository.findById(id);
    }

    @Transactional
    public List<StudentPodaciResponse> getAllStudentPodaci() {
        return studentPodaciRepository.findAll()
                .stream()
                .map(entityMappers::fromStudentPodaciToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public Page<StudentPodaciResponse> getAllStudentPodaciPaginated(int page, int size) {
        return studentPodaciRepository.findAll(
                PageRequest.of(page, size, Sort.by("id").descending())
        ).map(entityMappers::fromStudentPodaciToResponse);
    }

    @Transactional
    public StudentPodaciResponse getStudentPodaciById(Long id) {
        return studentPodaciRepository.findById(id)
                .map(entityMappers::fromStudentPodaciToResponse)
                .orElse(null);
    }

    @Transactional
    public Long addStudentPodaci(StudentPodaci studentPodaci) {
        return studentPodaciRepository.save(studentPodaci).getId();
    }

    @Transactional
    public Page<StudentPodaci> findStudent(String ime, String prezime, Pageable pageable) {
        return studentPodaciRepository.findStudent(ime, prezime, pageable);
    }

    @Transactional
    public StudentIndeks getAktivanIndeks(Long studPodaciId){
        return studentPodaciRepository.getAktivanIndeks(studPodaciId);
    }

    @Transactional
    public List<StudentIndeks> getNeaktivniIndeksi(Long studPodaciId){
        return studentPodaciRepository.getNeaktivniIndeksi(studPodaciId);
    }

    @Transactional
    public StudentPodaciResponse getStudentPodaci(Long id){
        Optional<StudentPodaci> rez = findById(id);
        return rez.map(entityMappers::fromStudentPodaciToResponse).orElse(null);
    }

    @Transactional
    public void deleteStudentPodaci(Long id) {

    }

    @Transactional
    public void deleteById(Long id) {
        if (!studentPodaciRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Student sa ID " + id + " ne postoji.");
        }

        try {
            // 1. Nađi sve indekse
            List<StudentIndeks> indeksi = studentIndeksRepository.findStudentIndeksiForStudentPodaciId(id);

            for (StudentIndeks indeks : indeksi) {
                // Obriši sve vezano za ovaj indeks (koristimo postojeću logiku)
                deleteIndeksWithCascade(indeks.getId());
            }

            // 2. Obriši studenta
            studentPodaciRepository.deleteById(id);

        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ne može se obrisati student jer postoje povezani zapisi.");
        }
    }

    private void deleteIndeksWithCascade(Long indeksId) {
        List<SlusaPredmet> slusaPredmeti = slusaPredmetRepository.getSlusaPredmetForIndeksAktivnaGodina(indeksId);
        for (SlusaPredmet sp : slusaPredmeti) {
            List<PredispitnaIzlazak> izlasci = StreamSupport
                    .stream(predispitnaIzlazakRepository.findAll().spliterator(), false)
                    .filter(pi -> pi.getSlusaPredmet() != null && pi.getSlusaPredmet().getId().equals(sp.getId()))
                    .collect(Collectors.toList());
            izlasci.forEach(pi -> predispitnaIzlazakRepository.deleteById(pi.getId()));

            slusaPredmetRepository.deleteById(sp.getId());
        }

        List<IspitPrijava> prijave = StreamSupport
                .stream(ispitPrijavaRepository.findAll().spliterator(), false)
                .filter(ip -> ip.getStudentIndeks() != null && ip.getStudentIndeks().getId().equals(indeksId))
                .collect(Collectors.toList());

        for (IspitPrijava prijava : prijave) {
            if (prijava.getIspitIzlazak() != null) {
                ispitIzlazakRepository.deleteById(prijava.getIspitIzlazak().getId());
            }
            ispitPrijavaRepository.deleteById(prijava.getId());
        }

        List<PolozenPredmet> polozeni = StreamSupport
                .stream(polozenPredmetRepository.findAll().spliterator(), false)
                .filter(pp -> pp.getStudentIndeks() != null && pp.getStudentIndeks().getId().equals(indeksId))
                .collect(Collectors.toList());
        polozeni.forEach(pp -> polozenPredmetRepository.deleteById(pp.getId()));

        // Obriši UpisGodine, ObnovaGodine, TokStudija TODO
        // (dodaj ove repozitorijume kao dependency)

        studentIndeksRepository.deleteById(indeksId);
    }


}
