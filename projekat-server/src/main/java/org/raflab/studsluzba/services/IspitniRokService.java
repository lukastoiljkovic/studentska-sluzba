package org.raflab.studsluzba.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzba.model.entities.IspitniRok;
import org.raflab.studsluzba.model.entities.SkolskaGodina;
import org.raflab.studsluzba.repositories.IspitniRokRepository;
import org.raflab.studsluzba.repositories.SkolskaGodinaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IspitniRokService {

    final IspitniRokRepository ispitniRokRepository;
    final SkolskaGodinaRepository skolskaGodinaRepository;

    public Long add(IspitniRokRequest req) {
        SkolskaGodina sg = skolskaGodinaRepository.findById(req.getSkolskaGodinaId()).orElse(null);

        IspitniRok ir = new IspitniRok();
        ir.setNaziv(req.getNaziv());
        ir.setDatumPocetka(req.getDatumPocetka());
        ir.setDatumZavrsetka(req.getDatumZavrsetka());
        ir.setSkolskaGodina(sg);

        return ispitniRokRepository.save(ir).getId();
    }

    public Optional<IspitniRok> findById(Long id) {
        return ispitniRokRepository.findById(id);
    }

    public List<IspitniRok> findAll() {
        return (List<IspitniRok>) ispitniRokRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        if (!ispitniRokRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Entitet sa ID " + id + " ne postoji.");
        }
        try {
            ispitniRokRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ne moze se obrisati entitet jer postoje povezani zapisi.");
        }
    }

}
