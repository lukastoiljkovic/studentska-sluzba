package org.raflab.studsluzba.services;
import org.raflab.studsluzba.dtos.*;
import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzba.model.entities.*;
import org.raflab.studsluzba.repositories.*;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class GrupaService {

    final GrupaRepository grupaRepository;
    final StudijskiProgramRepository studijskiProgramRepository;
    final SkolskaGodinaRepository skolskaGodinaRepository;
    final SlusaPredmetRepository slusaPredmetRepository;
    final PredispitnaIzlazakRepository predispitnaIzlazakRepository;

    @Transactional
    public Long addGrupa(GrupaRequest req) {
        StudijskiProgram sp = studijskiProgramRepository.findById(req.getStudijskiProgramId()).orElse(null);
        SkolskaGodina sg = skolskaGodinaRepository.findById(req.getSkolskaGodinaId()).orElse(null);

        Grupa g = Converters.toGrupa(req, sp, sg);
        return grupaRepository.save(g).getId();
    }

    @Transactional
    public Optional<Grupa> findById(Long id) {
        return grupaRepository.findById(id);
    }

    @Transactional
    public List<Grupa> findAll() {
        return grupaRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        if (!grupaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Grupa sa ID " + id + " ne postoji.");
        }

        try {
            List<SlusaPredmet> slusaPredmeti = StreamSupport
                    .stream(slusaPredmetRepository.findAll().spliterator(), false)
                    .filter(sp -> sp.getGrupa() != null && sp.getGrupa().getId().equals(id))
                    .collect(Collectors.toList());

            for (SlusaPredmet sp : slusaPredmeti) {
                List<PredispitnaIzlazak> izlasci = StreamSupport
                        .stream(predispitnaIzlazakRepository.findAll().spliterator(), false)
                        .filter(pi -> pi.getSlusaPredmet() != null && pi.getSlusaPredmet().getId().equals(sp.getId()))
                        .collect(Collectors.toList());

                izlasci.forEach(pi -> predispitnaIzlazakRepository.deleteById(pi.getId()));
            }

            for (SlusaPredmet sp : slusaPredmeti) {
                sp.setGrupa(null);
                slusaPredmetRepository.save(sp);
            }
            
            grupaRepository.deleteById(id);

        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ne može se obrisati grupa jer postoje povezani zapisi: " + e.getMessage());
        }
    }
}
