package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.model.entities.StudentIndeks;
import org.raflab.studsluzba.model.dtos.StudentProfileDTO;
import org.raflab.studsluzba.model.dtos.StudentWebProfileDTO;
import org.raflab.studsluzba.repositories.SlusaPredmetRepository;
import org.raflab.studsluzba.repositories.StudentIndeksRepository;
import org.raflab.studsluzba.repositories.StudentPodaciRepository;
import org.raflab.studsluzba.utils.ParseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Service
public class StudentProfileService {

	final StudentIndeksRepository studentIndeksRepo;
	final StudentPodaciRepository studentPodaciRepo;
	final SlusaPredmetRepository slusaPredmetRepo;

    final StudentIndeksService studentIndeksService;

	public StudentProfileDTO getStudentProfile(Long indeksId) {
		StudentProfileDTO retVal = new StudentProfileDTO();
		StudentIndeks studIndeks = studentIndeksRepo.findById(indeksId).get();
		retVal.setIndeks(studIndeks);		
		retVal.setSlusaPredmete(slusaPredmetRepo.getSlusaPredmetForIndeksAktivnaGodina(indeksId));
		return retVal;
	}

    public StudentWebProfileDTO getStudentWebProfile(Long indeksId) {
        StudentWebProfileDTO retVal = new StudentWebProfileDTO();
        StudentIndeks studIndeks = studentIndeksRepo.findById(indeksId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Indeks ne postoji"));

        Long studPodaciId = studIndeks.getStudent().getId();

        List<StudentIndeks> aktivniIndeksi = Collections.singletonList(studentPodaciRepo.getAktivanIndeks(studPodaciId));
        if (aktivniIndeksi.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nema aktivnih indeksa");
        }

        retVal.setAktivanIndeks(aktivniIndeksi.get(0));
        retVal.setSlusaPredmete(slusaPredmetRepo.getSlusaPredmetForIndeksAktivnaGodina(indeksId));
        return retVal;
    }

    public StudentWebProfileDTO getStudentWebProfileForEmail(@RequestParam String studEmail) {
        String[] parsedData = ParseUtils.parseEmail(studEmail);
        if(parsedData!=null) {
            StudentIndeks si = studentIndeksService.findStudentIndeks(parsedData[0], 2000+Integer.parseInt(parsedData[1]),Integer.parseInt(parsedData[2]));
            if(si!=null)
                return getStudentWebProfile(si.getId());
        }
        return null;
    }

}
