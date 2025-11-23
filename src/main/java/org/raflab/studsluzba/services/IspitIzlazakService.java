package org.raflab.studsluzba.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.controllers.request.IspitIzlazakRequest;
import org.raflab.studsluzba.model.entities.IspitIzlazak;
import org.raflab.studsluzba.model.entities.IspitPrijava;
import org.raflab.studsluzba.model.entities.PolozenPredmet;
import org.raflab.studsluzba.model.entities.StudentIndeks;
import org.raflab.studsluzba.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IspitIzlazakService {

    final IspitIzlazakRepository ispitIzlazakRepository;
    final IspitPrijavaRepository ispitPrijavaRepository;
    final StudentIndeksRepository studentIndeksRepository;
    private final PredispitnaIzlazakRepository predispitnaIzlazakRepository;
    private final PolozenPredmetRepository polozenPredmetRepository;

    public Optional<IspitIzlazak> findById(Long id) {
        return ispitIzlazakRepository.findById(id);
    }

    public List<IspitIzlazak> findAll() {
        return (List<IspitIzlazak>) ispitIzlazakRepository.findAll();
    }

    public void deleteById(Long id) {
        ispitIzlazakRepository.deleteById(id);
    }
    public Long add(IspitIzlazakRequest req) {
        // validacija ulaza
        if (req.getIspitPrijavaId() == null || req.getBrojPoena() == null || req.getBrojPoena() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ispitPrijavaId i pozitivan brojPoena su obavezni.");
        }

        IspitPrijava prijava = ispitPrijavaRepository.findById(req.getIspitPrijavaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ispit prijava ne postoji."));

        // (opciono) ako je client poslao studentIndeksId, proveri konzistentnost
        if (req.getStudentIndeksId() != null &&
                !prijava.getStudentIndeks().getId().equals(req.getStudentIndeksId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "studentIndeksId ne odgovara prijavi.");
        }

        // jedan izlazak po prijavi (OneToOne)
        if (prijava.getIspitIzlazak() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Za ovu prijavu već postoji izlazak.");
        }

        // (opciono) zabrani unos ako je ispit zaključen
        if (prijava.getIspit() != null && prijava.getIspit().isZakljucen()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ispit je zaključen.");
        }

        // kreiraj izlazak
        IspitIzlazak ie = new IspitIzlazak();
        ie.setIspitPrijava(prijava);
        ie.setStudentIndeks(prijava.getStudentIndeks());
        ie.setBrojPoena(req.getBrojPoena());
        ie.setNapomena(req.getNapomena());
        ie.setPonistava(Boolean.TRUE.equals(req.getPonistava())); // Boolean → boolean

        IspitIzlazak sacuvan = ispitIzlazakRepository.save(ie);
        prijava.setIspitIzlazak(sacuvan);
        ispitPrijavaRepository.save(prijava);

        // ako je poništio — nema dalje
        if (Boolean.TRUE.equals(req.getPonistava())) return sacuvan.getId();

        // saberi predispitne poene (tačan predmet i školska godina)
        Long siId = prijava.getStudentIndeks().getId();
        Long predmetId = prijava.getIspit().getPredmet().getId();
        Long skGodId = prijava.getIspit().getIspitniRok().getSkolskaGodina().getId();

        Integer predispitni = predispitnaIzlazakRepository
                .sumPoeniZaStudentaPredmetGodinu(siId, predmetId, skGodId);

        int ukupno = (predispitni != null ? predispitni : 0) + req.getBrojPoena();

        // ako je ukupno ≥ 51 — upiši u Položenim sa ocenom
        if (ukupno >= 51) {
            PolozenPredmet pp = new PolozenPredmet();
            pp.setPriznat(false);
            pp.setStudentIndeks(prijava.getStudentIndeks());
            pp.setPredmet(prijava.getIspit().getPredmet());
            pp.setIspitIzlazak(sacuvan);
            pp.setOcena(mapToOcena(ukupno));
            polozenPredmetRepository.save(pp);
        }

        return sacuvan.getId();
    }

    private int mapToOcena(int poeni) {
        if (poeni >= 91) return 10;
        if (poeni >= 81) return 9;
        if (poeni >= 71) return 8;
        if (poeni >= 61) return 7;
        if (poeni >= 51) return 6;
        return 5; // ne snimamo u Položenim ispod 51
    }

}
