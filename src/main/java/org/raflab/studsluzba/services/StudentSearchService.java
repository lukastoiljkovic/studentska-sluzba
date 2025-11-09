package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.controllers.response.StudentIndeksResponse;
import org.raflab.studsluzba.model.dtos.StudentDTO;
import org.raflab.studsluzba.model.entities.StudentIndeks;
import org.raflab.studsluzba.model.entities.StudentPodaci;
import org.raflab.studsluzba.utils.EntityMappers;
import org.raflab.studsluzba.utils.ParseUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service @AllArgsConstructor
public class StudentSearchService {

    private final StudentPodaciService studentPodaciService;
    private final StudentIndeksService studentIndeksService;
    private final EntityMappers entityMappers;

    public Page<StudentDTO> search(@RequestParam(required = false) String ime,
                                   @RequestParam (required = false) String prezime,
                                   @RequestParam (required = false) String studProgram,
                                   @RequestParam (required = false) Integer godina,
                                   @RequestParam (required = false) Integer broj,
                                   @RequestParam(defaultValue = "0") Integer page,
                                   @RequestParam(defaultValue = "10") Integer size) {

        if(studProgram==null && godina == null && broj==null) { // pretrazivanje studenata bez indeksa
            Page<StudentPodaci> spList = studentPodaciService.findStudent(ime, prezime, PageRequest.of(page, size, Sort.by("id").descending()));
            return spList.map(EntityMappers::fromStudentPodaciToDTO);
        }
        Page<StudentIndeks> siList = studentIndeksService.findStudentIndeks(ime, prezime, studProgram, godina, broj, PageRequest.of(page, size, Sort.by("id").descending()));
        return siList.map(EntityMappers::fromStudentIndeksToDTO);
    }


    public StudentIndeksResponse fastSearch(String indeksShort) {
        String[] parsedData = ParseUtils.parseIndeks(indeksShort);
        if(parsedData!=null) {
            StudentIndeks si = studentIndeksService.findStudentIndeks(parsedData[0], 2000+Integer.parseInt(parsedData[1]),Integer.parseInt(parsedData[2]));
            return entityMappers.fromStudentIndexToResponse(si);
        }else return null;
    }

    public StudentIndeksResponse emailSearch(String studEmail) {
        String[] parsedData = ParseUtils.parseEmail(studEmail);
        if(parsedData!=null) {
            StudentIndeks si = studentIndeksService.findStudentIndeks(parsedData[0], 2000+Integer.parseInt(parsedData[1]),Integer.parseInt(parsedData[2]));
            return entityMappers.fromStudentIndexToResponse(si);
        }else return null;
    }

}
