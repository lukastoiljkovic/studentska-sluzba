package org.raflab.studsluzba.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.controllers.request.GrupaRequest;
import org.raflab.studsluzba.model.entities.Grupa;
import org.raflab.studsluzba.model.entities.SkolskaGodina;
import org.raflab.studsluzba.model.entities.StudijskiProgram;
import org.raflab.studsluzba.repositories.GrupaRepository;
import org.raflab.studsluzba.repositories.SkolskaGodinaRepository;
import org.raflab.studsluzba.repositories.StudijskiProgramRepository;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GrupaService {

    final GrupaRepository grupaRepository;
    final StudijskiProgramRepository studijskiProgramRepository;
    final SkolskaGodinaRepository skolskaGodinaRepository;

    public Long addGrupa(GrupaRequest req) {
        StudijskiProgram sp = studijskiProgramRepository.findById(req.getStudijskiProgramId()).orElse(null);
        SkolskaGodina sg = skolskaGodinaRepository.findById(req.getSkolskaGodinaId()).orElse(null);

        Grupa g = Converters.toGrupa(req, sp, sg);
        return grupaRepository.save(g).getId();
    }

    public Optional<Grupa> findById(Long id) {
        return grupaRepository.findById(id);
    }

    public List<Grupa> findAll() {
        return grupaRepository.findAll();
    }

    public void deleteById(Long id) {
        grupaRepository.deleteById(id);
    }
}
