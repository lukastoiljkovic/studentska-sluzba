package org.raflab.studsluzba.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.controllers.request.IspitIzlazakRequest;
import org.raflab.studsluzba.model.entities.IspitIzlazak;
import org.raflab.studsluzba.model.entities.IspitPrijava;
import org.raflab.studsluzba.model.entities.StudentIndeks;
import org.raflab.studsluzba.repositories.IspitIzlazakRepository;
import org.raflab.studsluzba.repositories.IspitPrijavaRepository;
import org.raflab.studsluzba.repositories.StudentIndeksRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IspitIzlazakService {

    final IspitIzlazakRepository ispitIzlazakRepository;
    final IspitPrijavaRepository ispitPrijavaRepository;
    final StudentIndeksRepository studentIndeksRepository;

    public Long add(IspitIzlazakRequest req) {
        IspitPrijava prijava = ispitPrijavaRepository.findById(req.getIspitPrijavaId()).orElse(null);
        StudentIndeks indeks = studentIndeksRepository.findById(req.getStudentIndeksId()).orElse(null);

        IspitIzlazak e = new IspitIzlazak();
        e.setBrojPoena(req.getBrojPoena());
        e.setNapomena(req.getNapomena());
        e.setPonistava(Boolean.TRUE.equals(req.getPonistava()));
        e.setIspitPrijava(prijava);
        e.setStudentIndeks(indeks);

        return ispitIzlazakRepository.save(e).getId();
    }

    public Optional<IspitIzlazak> findById(Long id) {
        return ispitIzlazakRepository.findById(id);
    }

    public List<IspitIzlazak> findAll() {
        return (List<IspitIzlazak>) ispitIzlazakRepository.findAll();
    }

    public void deleteById(Long id) {
        ispitIzlazakRepository.deleteById(id);
    }
}
