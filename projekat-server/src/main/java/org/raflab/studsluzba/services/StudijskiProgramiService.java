package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzba.model.entities.StudijskiProgram;
import org.raflab.studsluzba.repositories.StudijskiProgramRepository;
import org.raflab.studsluzba.utils.Converters;
import org.raflab.studsluzba.utils.EntityMappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.raflab.studsluzba.dtos.*;
import java.util.List;

@AllArgsConstructor
@Service
public class StudijskiProgramiService {

    private final StudijskiProgramRepository studijskiProgramRepository;

    @Transactional
    public List<StudijskiProgram> getAllStudProgramiSortedDesc(){
        return studijskiProgramRepository.getAllSortedByGodinaDesc();
    }

    @Transactional
    public Iterable<String> getAllStudProgramOznaka() {
        return studijskiProgramRepository.findAllOznaka();
    }

    @Transactional
    public List<StudijskiProgram> findByOznaka(String oznaka){
        return studijskiProgramRepository.findByOznaka(oznaka);
    }

    @Transactional
    public StudijskiProgramResponse addStudijskiProgram(StudijskiProgramRequest request) {
        StudijskiProgram sp = new StudijskiProgram();
        sp.setOznaka(request.getOznaka());
        sp.setNaziv(request.getNaziv());
        sp.setGodinaAkreditacije(request.getGodinaAkreditacije());
        sp.setZvanje(request.getZvanje());
        sp.setTrajanjeGodina(request.getTrajanjeGodina());
        sp.setTrajanjeSemestara(request.getTrajanjeSemestara());
        sp.setVrstaStudija(request.getVrstaStudija());
        sp.setUkupnoEspb(request.getUkupnoEspb());

        sp = studijskiProgramRepository.save(sp);
        return Converters.toStudijskiProgramResponse(sp);
    }

}
