package org.raflab.studsluzba.controllers.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.controllers.request.StudentIndeksRequest;
import org.raflab.studsluzba.controllers.request.StudentPodaciRequest;
import org.raflab.studsluzba.controllers.response.StudentIndeksResponse;
import org.raflab.studsluzba.controllers.response.StudentPodaciResponse;
import org.raflab.studsluzba.model.dtos.StudentDTO;
import org.raflab.studsluzba.model.dtos.StudentProfileDTO;
import org.raflab.studsluzba.model.dtos.StudentWebProfileDTO;
import org.raflab.studsluzba.services.*;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/student")
public class StudentController {

    final StudentPodaciService studentPodaciService;
	final StudentProfileService studentProfileService;
	final StudentIndeksService studentIndeksService;
    final StudentSearchService studentSearchService;

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

}