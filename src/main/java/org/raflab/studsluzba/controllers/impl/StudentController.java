package org.raflab.studsluzba.controllers.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.controllers.request.SrednjaSkolaRequest;
import org.raflab.studsluzba.controllers.request.StudentIndeksRequest;
import org.raflab.studsluzba.controllers.request.StudentPodaciRequest;
import org.raflab.studsluzba.controllers.response.StudentIndeksResponse;
import org.raflab.studsluzba.controllers.response.StudentPodaciResponse;
import org.raflab.studsluzba.controllers.response.UpisGodineResponse;
import org.raflab.studsluzba.model.dtos.StudentDTO;
import org.raflab.studsluzba.model.dtos.StudentProfileDTO;
import org.raflab.studsluzba.model.dtos.StudentWebProfileDTO;
import org.raflab.studsluzba.model.entities.SrednjaSkola;
import org.raflab.studsluzba.model.entities.StudentIndeks;
import org.raflab.studsluzba.model.entities.StudentPodaci;
import org.raflab.studsluzba.model.entities.Uplata;
import org.raflab.studsluzba.services.*;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

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

    /// - selekcija studenta (njegovih ličnih podataka) preko broja indeksa
    @GetMapping(path="/podaci/byIndeks")
    public StudentPodaciResponse getStudentPodaciByIndeks(@RequestParam String studProgramOznaka,
                                                          @RequestParam int godina,
                                                          @RequestParam int broj) {
        // dohvat StudentIndeks
        StudentIndeks studentIndeks = studentIndeksService.findStudentIndeks(studProgramOznaka, godina, broj);
        if (studentIndeks == null) return null; // ili throw Exception

        // dohvat StudentPodaci iz indeksa
        StudentPodaci studentPodaci = studentIndeks.getStudent();
        return studentPodaciService.getStudentPodaci(studentPodaci.getId());
    }

    /// selekcija studenata na osnovu imena i/ili prezimena (može samo ime, ili samo prezime ili oba da se unesu), paginirano
    @GetMapping(path="/search")  // pretraga po imenu, prezimenu i elementima indeksa
    public Page<StudentDTO> search(@RequestParam (required = false) String ime,
                                   @RequestParam (required = false) String prezime,
                                   @RequestParam (required = false) String studProgram,
                                   @RequestParam (required = false) Integer godina,
                                   @RequestParam (required = false) Integer broj,
                                   @RequestParam(defaultValue = "0") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size) {

        return studentSearchService.search(ime, prezime, studProgram, godina, broj, page, size);
    }

    ///  pregled svih upisanih godina za broj indeksa
    @GetMapping("/upisane-godine")
    public List<UpisGodineResponse> getUpisaneGodine(
            @RequestParam String studProgramOznaka,
            @RequestParam int godina,
            @RequestParam int broj) {

        return upisGodineService.findUpisaneGodine(studProgramOznaka, godina, broj);
    }

    /// selekcija svih upisanih studenata koji su završili određenu srednju školu
    @PostMapping("/po-srednjoj-skoli")
    public List<StudentPodaciResponse> getStudentiPoSrednjojSkoli(@RequestBody SrednjaSkolaRequest request) {
        SrednjaSkola skola = Converters.toSrednjaSkola(request);

        return srednjaSkolaService.getStudentiPoSrednjojSkoli(skola);
    }

    ///dodavanje nove uplate (čuva se datum uplate, iznos u dinarima i srednji kurs).
    /// Trenutni srednji kurs ne treba da ima predefinisanu vrednost, nego ga treba
    /// dohvatiti api pozivom
    @PostMapping("/{upisGodineId}/uplata")
    public Uplata dodajUplatu(@PathVariable Long upisGodineId,
                              @RequestParam Double iznosEUR) {
        return uplataService.dodajUplatu(upisGodineId, iznosEUR);
    }

    ///selekcija preostalog iznosa za uplatu u evrima i dinarima. Iznos školarine je predefinisana vrednost od 3000e
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

	@GetMapping(path="/fastsearch")  // salje se string oblika rn1923 - smer godina broj
	public StudentIndeksResponse fastSearch(@RequestParam String indeksShort) {
        return studentSearchService.fastSearch(indeksShort);
	}

	@GetMapping(path="/emailsearch")  // salje se email studenta
	public StudentIndeksResponse emailSearch(@RequestParam String studEmail) {
        return studentSearchService.emailSearch(studEmail);
	}

	@GetMapping(path="/profile/{studentIndeksId}")
	public StudentProfileDTO getStudentProfile(@PathVariable  Long studentIndeksId) {
		return studentProfileService.getStudentProfile(studentIndeksId);
	}

	@GetMapping(path="/webprofile/{studentIndeksId}")
	public StudentWebProfileDTO getStudentWebProfile(@PathVariable  Long studentIndeksId) {
		return studentProfileService.getStudentWebProfile(studentIndeksId);
	}

	@GetMapping(path="/webprofile/email")
	public StudentWebProfileDTO getStudentWebProfileForEmail(@RequestParam String studEmail) {
        return studentProfileService.getStudentWebProfileForEmail(studEmail);
	}

    @DeleteMapping(path="/{id}")
    public void deleteStudentPodaci(@PathVariable Long id) {
        studentPodaciService.deleteStudentPodaci(id);
    }

}