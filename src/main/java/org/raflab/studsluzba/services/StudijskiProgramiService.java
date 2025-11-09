package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.model.entities.StudijskiProgram;
import org.raflab.studsluzba.repositories.StudijskiProgramRepository;
import org.raflab.studsluzba.utils.EntityMappers;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class StudijskiProgramiService {

    private final StudijskiProgramRepository studijskiProgramRepository;

    public List<StudijskiProgram> getAllStudProgramiSortedDesc(){
        return studijskiProgramRepository.getAllSortedByGodinaDesc();
    }

    public Iterable<String> getAllStudProgramOznaka() {
        return studijskiProgramRepository.findAllOznaka();
    }

    public List<StudijskiProgram> findByOznaka(String oznaka){
        return studijskiProgramRepository.findByOznaka(oznaka);
    }

}
