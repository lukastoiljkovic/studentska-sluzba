package org.raflab.studsluzba.controllers.impl;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzba.model.entities.StudijskiProgram;
import org.raflab.studsluzba.services.PredmetService;
import org.raflab.studsluzba.services.StudijskiProgramiService;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping(path="/api/studprogram")
public class StudijskiProgramiController {

    final StudijskiProgramiService studijskiProgramiService;
	private final PredmetService predmetService;

    @PostMapping("/add")
    public StudijskiProgramResponse addStudijskiProgram(@RequestBody @Valid StudijskiProgramRequest request) {
        return studijskiProgramiService.addStudijskiProgram(request);
    }

	@GetMapping(path = "/all/sorted")
	public List<StudijskiProgramResponse> getAllStudProgramiSortedDesc(){
		return studijskiProgramiService.getAllStudProgramiSortedDesc()
				.stream()
				.map(Converters::toStudijskiProgramResponse)
				.collect(Collectors.toList());
	}

	@GetMapping(path="/oznaka/all")
	public Iterable<String> getAllStudProgramOznaka() {
		return studijskiProgramiService.getAllStudProgramOznaka();
	}

	@GetMapping("/{id}/predmeti")
	public List<PredmetResponse> getPredmetiZaProgram(@PathVariable Long id) {
		return predmetService.getPredmetiNaStudijskomProgramu(id);
	}
}
