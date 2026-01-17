package org.raflab.studsluzba.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzba.model.entities.VisokoskolskaUstanova;
import org.raflab.studsluzba.repositories.VisokoskolskaUstanovaRepository;
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
public class VisokoskolskaUstanovaService {

    private final VisokoskolskaUstanovaRepository repo;

    public Optional<VisokoskolskaUstanova> findById(Long id) {
        return repo.findById(id);
    }

    public List<VisokoskolskaUstanova> findAll() {
        Iterable<VisokoskolskaUstanova> it = repo.findAll();
        return StreamSupport.stream(it.spliterator(), false).collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Entitet sa ID " + id + " ne postoji.");
        }
        try {
            repo.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ne može se obrisati entitet jer postoje povezani zapisi.");
        }
    }

    public Long add(VisokoskolskaUstanovaRequest req) {
        VisokoskolskaUstanova v = new VisokoskolskaUstanova();
        v.setNaziv(req.getNaziv());
        v.setMesto(req.getMesto());

        // Mapiranje enum-a
        if (req.getVrsta() != null) {
            v.setVrsta(VisokoskolskaUstanova.Vrsta.valueOf(req.getVrsta().name()));
        }

        return repo.save(v).getId();
    }
}
