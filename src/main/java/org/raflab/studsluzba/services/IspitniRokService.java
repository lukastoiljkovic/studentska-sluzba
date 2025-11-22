package org.raflab.studsluzba.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.controllers.request.IspitniRokRequest;
import org.raflab.studsluzba.model.entities.IspitniRok;
import org.raflab.studsluzba.model.entities.SkolskaGodina;
import org.raflab.studsluzba.repositories.IspitniRokRepository;
import org.raflab.studsluzba.repositories.SkolskaGodinaRepository;
import org.springframework.stereotype.Service;

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

    public void deleteById(Long id) {
        ispitniRokRepository.deleteById(id);
    }
}
