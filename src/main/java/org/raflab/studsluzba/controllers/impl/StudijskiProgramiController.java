package org.raflab.studsluzba.controllers.impl;

import java.util.List;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.model.entities.StudijskiProgram;
import org.raflab.studsluzba.services.StudijskiProgramiService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping(path="/api/studprogram")
public class StudijskiProgramiController {

    final StudijskiProgramiService studijskiProgramiService;
	
	@GetMapping(path = "/all/sorted")
	public List<StudijskiProgram> getAllStudProgramiSortedDesc(){
		return studijskiProgramiService.getAllStudProgramiSortedDesc();
	}

	@GetMapping(path="/oznaka/all")
	public Iterable<String> getAllStudProgramOznaka() {
		return studijskiProgramiService.getAllStudProgramOznaka();
	}
}
