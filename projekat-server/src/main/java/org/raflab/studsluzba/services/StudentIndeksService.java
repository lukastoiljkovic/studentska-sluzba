package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.controllers.request.StudentIndeksRequest;
import org.raflab.studsluzba.controllers.response.StudentIndeksResponse;
import org.raflab.studsluzba.model.entities.StudentIndeks;
import org.raflab.studsluzba.model.entities.StudentPodaci;
import org.raflab.studsluzba.model.entities.StudijskiProgram;
import org.raflab.studsluzba.repositories.StudentIndeksRepository;
import org.raflab.studsluzba.repositories.StudentPodaciRepository;
import org.raflab.studsluzba.utils.Converters;
import org.raflab.studsluzba.utils.EntityMappers;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class StudentIndeksService {

    private final StudentIndeksRepository studentIndeksRepository;
    private final StudentPodaciRepository studentPodaciRepository;
    private final StudijskiProgramiService studijskiProgramiService;
    private final EntityMappers entityMappers;


    @Transactional(readOnly = true)
    public int findBroj(int godina, String studProgramOznaka) {
        List<Integer> brojeviList = studentIndeksRepository.
                findBrojeviByGodinaAndStudProgramOznaka(godina, studProgramOznaka);

        return findNextAvailableNumber(brojeviList);
    }

    private int findNextAvailableNumber(List<Integer> brojeviList) {
        if (brojeviList == null || brojeviList.isEmpty()) return 1;

        List<Integer> sorted = brojeviList.stream()
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(java.util.stream.Collectors.toList()); // <= works on JDK 8+

        int expected = 1;
        for (int num : sorted) {
            if (num != expected) return expected;
            expected++;
        }
        return expected;
    }

    public StudentIndeks findByStudentIdAndAktivan(Long studentPodaciId) {
        return studentIndeksRepository.findAktivanStudentIndeksiByStudentPodaciId(studentPodaciId);
    }

    public StudentIndeks findStudentIndeks(String studProgramOznaka, int godina, int broj){
        return studentIndeksRepository.findStudentIndeks(studProgramOznaka,godina,broj);
    }

    public Page<StudentIndeks> findStudentIndeks(String ime, String prezime, String studProgramOznaka, Integer godina, Integer broj, Pageable pageable) {
        return studentIndeksRepository.findStudentIndeks(ime, prezime, studProgramOznaka, godina, broj, pageable);
    }

    public List<StudentIndeks> findStudentIndeksiForStudentPodaciId(Long idStudentPodaci) {
        return studentIndeksRepository.findStudentIndeksiForStudentPodaciId(idStudentPodaci);
    }

    public StudentIndeks addNewStudentIndeks(StudentIndeks studentIndeks) {
        return studentIndeksRepository.save(studentIndeks);
    }

    @Transactional
    // optional znaci da moze da vrati NULL a da se ne javi exception
    public Optional<StudentIndeks> getStudentIndeksById(Long id) {
        return studentIndeksRepository.findById(id);
    }

    @Transactional
    public StudentIndeksResponse getStudentIndeks(Long id){
        Optional<StudentIndeks> rez = getStudentIndeksById(id);
        if(rez.isEmpty()) return null;
        else {
            StudentIndeks retVal = rez.get();
            return entityMappers.fromStudentIndexToResponse(retVal);
        }
    }

    public List<StudentIndeksResponse> getIndeksiForStudentPodaciId(Long idStudentPodaci){
        return findStudentIndeksiForStudentPodaciId(idStudentPodaci)
                .stream()
                .map(entityMappers::fromStudentIndexToResponse) // map each entity to response
                .collect(Collectors.toList());
    }

    @Transactional
    public Long saveStudentIndeks(StudentIndeksRequest request) {
        // 1. Validacija i učitavanje StudentPodaci iz baze
        StudentPodaci student = studentPodaciRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "StudentPodaci sa ID " + request.getStudentId() + " ne postoji."));

        // 2. Validacija StudijskiProgram
        List<StudijskiProgram> studijskiProgrami = studijskiProgramiService.findByOznaka(request.getStudProgramOznaka());
        if (studijskiProgrami.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "StudijskiProgram sa oznakom " + request.getStudProgramOznaka() + " ne postoji.");
        }

        // 3. Kreiranje StudentIndeks
        StudentIndeks studentIndeks = new StudentIndeks();
        studentIndeks.setGodina(request.getGodina());
        studentIndeks.setStudProgramOznaka(request.getStudProgramOznaka());
        studentIndeks.setNacinFinansiranja(request.getNacinFinansiranja());
        studentIndeks.setAktivan(request.isAktivan());
        studentIndeks.setVaziOd(request.getVaziOd() != null ? request.getVaziOd() : LocalDate.now());
        studentIndeks.setOstvarenoEspb(0);

        // 4. Dodeli sledeci broj indeksa
        int nextBroj = findBroj(request.getGodina(), request.getStudProgramOznaka());
        studentIndeks.setBroj(nextBroj);

        // 5. Poveži sa studentom i programom
        studentIndeks.setStudent(student);
        studentIndeks.setStudijskiProgram(studijskiProgrami.get(0));

        // 6. Sačuvaj
        try {
            StudentIndeks saved = studentIndeksRepository.save(studentIndeks);
            return saved.getId();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Indeks sa ovim podacima već postoji.");
        }
    }


    @Transactional
    public void deleteStudentIndeks(Long id) {
        Optional<StudentIndeks> indeksOpt = studentIndeksRepository.findById(id);
        if (indeksOpt.isEmpty()) return;

        StudentIndeks indeks = indeksOpt.get();

        // ako ima zavisnih entiteta (npr. ispiti), obriši ih ovde
        if (indeks.getStudent() != null) {
            // npr. getStudent().getNeaktivniIndeksi() ili ispiti
        }

        studentIndeksRepository.deleteById(id);
    }


}
