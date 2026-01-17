package org.raflab.studsluzba.controllers.impl;

import java.util.*;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzba.model.entities.SkolskaGodina;
import org.raflab.studsluzba.model.entities.SrednjaSkola;
import org.raflab.studsluzba.model.entities.StudentIndeks;
import org.raflab.studsluzba.model.entities.StudentPodaci;
import org.raflab.studsluzba.model.entities.UpisGodine;
import org.raflab.studsluzba.repositories.SkolskaGodinaRepository;
import org.raflab.studsluzba.repositories.UpisGodineRepository;
import org.raflab.studsluzba.services.*;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/student")
public class StudentController {

    private final StudentPodaciService studentPodaciService;
    private final StudentProfileService studentProfileService;
    private final StudentIndeksService studentIndeksService;
    private final StudentSearchService studentSearchService;
    private final UpisGodineService upisGodineService;
    private final SrednjaSkolaService srednjaSkolaService;
    private final UplataService uplataService;

    // DODAJ OVE DEPENDENCY-E:
    private final SkolskaGodinaRepository skolskaGodinaRepository;
    private final UpisGodineRepository upisGodineRepository;

    @GetMapping(path="/podaci/byIndeks")
    public ResponseEntity<StudentPodaciResponse> getStudentPodaciByIndeks(
            @RequestParam String studProgramOznaka,
            @RequestParam int godina,
            @RequestParam int broj) {

        StudentIndeks studentIndeks = studentIndeksService.findStudentIndeks(studProgramOznaka, godina, broj);
        if (studentIndeks == null)
            return ResponseEntity.notFound().build();

        StudentPodaci studentPodaci = studentIndeks.getStudent();
        StudentPodaciResponse resp = studentPodaciService.getStudentPodaci(studentPodaci.getId());
        return ResponseEntity.ok(resp);
    }

    @GetMapping(path="/search")
    public Page<StudentDTO> search(@RequestParam (required = false) String ime,
                                   @RequestParam (required = false) String prezime,
                                   @RequestParam (required = false) String studProgram,
                                   @RequestParam (required = false) Integer godina,
                                   @RequestParam (required = false) Integer broj,
                                   @RequestParam(defaultValue = "0") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size) {

        return studentSearchService.search(ime, prezime, studProgram, godina, broj, page, size);
    }

    @GetMapping("/upisane-godine")
    public List<UpisGodineResponse> getUpisaneGodine(
            @RequestParam String studProgramOznaka,
            @RequestParam int godina,
            @RequestParam int broj) {

        return upisGodineService.findUpisaneGodine(studProgramOznaka, godina, broj);
    }

    @GetMapping("/po-srednjoj-skoli")
    public List<StudentPodaciResponse> getStudentiPoSrednjojSkoli(@RequestParam String naziv) {
        Optional<SrednjaSkola> skolaOpt = srednjaSkolaService.findByNaziv(naziv);

        if (skolaOpt.isEmpty()) {
            return Collections.emptyList();
        }

        return srednjaSkolaService.getStudentiPoSrednjojSkoli(skolaOpt.get());
    }

    // ============ ISPRAVLJENI ENDPOINT ZA UPLATU ============
    @PostMapping("/{studentId}/uplata")
    public void dodajUplatu(
            @PathVariable Long studentId,
            @RequestParam Double iznosEUR) {

        // Nađi aktivan indeks studenta
        StudentIndeks aktivanIndeks = studentIndeksService.findByStudentIdAndAktivan(studentId);
        if (aktivanIndeks == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Aktivan indeks za studenta " + studentId + " ne postoji");
        }

        // Nađi trenutnu aktivnu školsku godinu
        SkolskaGodina aktivnaSkolskaGodina = skolskaGodinaRepository.findByAktivnaTrue()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Aktivna školska godina ne postoji"));

        // Nađi UpisGodine za ovaj indeks i aktivnu školsku godinu
        UpisGodine upisGodine = upisGodineRepository
                .findByStudentIndeksAndSkolskaGodina(aktivanIndeks, aktivnaSkolskaGodina)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Upis godine za studenta u aktivnoj školskoj godini ne postoji"));

        uplataService.dodajUplatu(upisGodine.getId(), iznosEUR);
    }

    @GetMapping("/{upisGodineId}/preostalo")
    public Map<String, Double> preostaliIznos(@PathVariable Long upisGodineId) {
        Map<String, Double> mapa = new HashMap<>();
        mapa.put("eur", uplataService.preostaliIznosEUR(upisGodineId));
        mapa.put("rsd", uplataService.preostaliIznosRSD(upisGodineId));
        return mapa;
    }

    @PostMapping(path="/add")
    public Long addNewStudentPodaci(@RequestBody StudentPodaciRequest studentPodaci) {
        return studentPodaciService.addStudentPodaci(Converters.toStudentPodaci(studentPodaci));
    }

    @GetMapping(path="/all")
    public Iterable<StudentPodaciResponse> getAllStudentPodaci() {
        return studentPodaciService.getAllStudentPodaci();
    }

    @GetMapping(path="/svi")
    public Page<StudentPodaciResponse> getAllStudentPodaciPaginated(@RequestParam(defaultValue = "0") Integer page,
                                                                    @RequestParam(defaultValue = "10") Integer size) {
        return studentPodaciService.getAllStudentPodaciPaginated(page, size);
    }

    @GetMapping(path="/podaci/{id}")
    public StudentPodaciResponse getStudentPodaci(@PathVariable Long id){
        return studentPodaciService.getStudentPodaci(id);
    }

    @PostMapping(path="/saveindeks")
    public Long saveIndeks(@RequestBody StudentIndeksRequest request) {
        return studentIndeksService.saveStudentIndeks(request);
    }

    @GetMapping(path="/indeks/{id}")
    public StudentIndeksResponse getStudentIndeks(@PathVariable Long id){
        return studentIndeksService.getStudentIndeks(id);
    }

    @GetMapping(path="/indeksi/{idStudentPodaci}")
    public List<StudentIndeksResponse> getIndeksiForStudentPodaciId(@PathVariable Long idStudentPodaci){
        return studentIndeksService.getIndeksiForStudentPodaciId(idStudentPodaci);
    }

    @GetMapping(path="/fastsearch")
    public StudentIndeksResponse fastSearch(@RequestParam String indeksShort) {
        return studentSearchService.fastSearch(indeksShort);
    }

    @GetMapping(path="/emailsearch")
    public StudentIndeksResponse emailSearch(@RequestParam String studEmail) {
        return studentSearchService.emailSearch(studEmail);
    }

    @GetMapping(path="/profile/{studentIndeksId}")
    public StudentProfileDTO getStudentProfile(@PathVariable Long studentIndeksId) {
        return studentProfileService.getStudentProfile(studentIndeksId);
    }

    @GetMapping(path="/webprofile/{studentIndeksId}")
    public StudentWebProfileDTO getStudentWebProfile(@PathVariable Long studentIndeksId) {
        return studentProfileService.getStudentWebProfile(studentIndeksId);
    }

    @GetMapping(path="/webprofile/email")
    public StudentWebProfileDTO getStudentWebProfileForEmail(@RequestParam String studEmail) {
        return studentProfileService.getStudentWebProfileForEmail(studEmail);
    }

    @DeleteMapping(path="/{id}")
    public void deleteStudentPodaci(@PathVariable Long id) {
        studentPodaciService.deleteById(id);
    }
}