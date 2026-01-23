package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzba.model.entities.StudentIndeks;
import org.raflab.studsluzba.model.entities.StudentPodaci;
import org.raflab.studsluzba.utils.EntityMappers;
import org.raflab.studsluzba.utils.ParseUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@Service @AllArgsConstructor
public class StudentSearchService {

    private final StudentPodaciService studentPodaciService;
    private final StudentIndeksService studentIndeksService;
    private final EntityMappers entityMappers;

    @Transactional
    public Page<StudentDTO> search(String ime,
                                   String prezime,
                                   String studProgram,
                                   Integer godina,
                                   Integer broj,
                                   Integer page,
                                   Integer size) {

        String imePattern = ime != null ? "%" + ime.toLowerCase() + "%" : null;
        String prezimePattern = prezime != null ? "%" + prezime.toLowerCase() + "%" : null;
        String studProgramPattern = studProgram != null ? "%" + studProgram.toLowerCase() + "%" : null;

        Page<StudentIndeks> siList = studentIndeksService.findStudentIndeks(
                imePattern,
                prezimePattern,
                studProgramPattern,
                godina,
                broj,
                PageRequest.of(page, size, Sort.by("id").descending())
        );

        return siList.map(EntityMappers::fromStudentIndeksToDTO);
    }

    @Transactional
    public StudentIndeksResponse fastSearch(String indeksShort) {
        String[] parsedData = ParseUtils.parseIndeks(indeksShort);
        if(parsedData!=null) {
            StudentIndeks si = studentIndeksService.findStudentIndeks(parsedData[0], 2000+Integer.parseInt(parsedData[1]),Integer.parseInt(parsedData[2]));
            return entityMappers.fromStudentIndexToResponse(si);
        }else return null;
    }

    @Transactional
    public StudentIndeksResponse emailSearch(String studEmail) {
        String[] parsedData = ParseUtils.parseEmail(studEmail);
        if(parsedData!=null) {
            StudentIndeks si = studentIndeksService.findStudentIndeks(parsedData[0], 2000+Integer.parseInt(parsedData[1]),Integer.parseInt(parsedData[2]));
            return entityMappers.fromStudentIndexToResponse(si);
        }else return null;
    }

}
