package org.raflab.studsluzba.controllers.impl;

import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.controllers.request.NastavnikRequest;
import org.raflab.studsluzba.controllers.response.NastavnikResponse;
import org.raflab.studsluzba.model.entities.Nastavnik;
import org.raflab.studsluzba.services.NastavnikService;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@CrossOrigin
@RestController
@AllArgsConstructor //Spring automatski injektuje kroz konstruktor
@RequestMapping(path = "/api/nastavnik")
public class NastavnikController {

	final NastavnikService nastavnikService;

    @PostMapping(path="/add")
    public Long addNewNastavnik(@RequestBody @Valid NastavnikRequest nastavnikRequest) {
        return nastavnikService.addNastavnik(Converters.toNastavnik(nastavnikRequest));
    }

	
	@GetMapping(path = "/all")
	public List<NastavnikResponse> getAllNastavnik() {
		return Converters.toNastavnikResponseList(nastavnikService.findAll());
	}

	@GetMapping(path = "/{id}")
	public NastavnikResponse getNastavnikById(@PathVariable Long id) {
		Optional<Nastavnik> rez = nastavnikService.findById(id);
		return rez.map(Converters::toNastavnikResponse).orElse(null);
	}
	
	@GetMapping(path = "/search")
	public List<NastavnikResponse> search(
			@RequestParam(required = false) String ime,
			@RequestParam(required = false) String prezime){
        return Converters.toNastavnikResponseList(nastavnikService.findByImeAndPrezime(ime, prezime));
	}
	
}
