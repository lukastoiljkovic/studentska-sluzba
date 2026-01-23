package org.raflab.studsluzba.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzba.model.entities.*;
import org.raflab.studsluzba.repositories.*;
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
public class IspitniRokService {

    final IspitniRokRepository ispitniRokRepository;
    final SkolskaGodinaRepository skolskaGodinaRepository;
    final IspitRepository ispitRepository;
    final PolozenPredmetRepository polozenPredmetRepository;
    final IspitPrijavaRepository ispitPrijavaRepository;
    final IspitIzlazakRepository ispitIzlazakRepository;

    @Transactional
    public Long add(IspitniRokRequest req) {
        SkolskaGodina sg = skolskaGodinaRepository.findById(req.getSkolskaGodinaId()).orElse(null);

        IspitniRok ir = new IspitniRok();
        ir.setNaziv(req.getNaziv());
        ir.setDatumPocetka(req.getDatumPocetka());
        ir.setDatumZavrsetka(req.getDatumZavrsetka());
        ir.setSkolskaGodina(sg);

        return ispitniRokRepository.save(ir).getId();
    }

    @Transactional
    public Optional<IspitniRok> findById(Long id) {
        return ispitniRokRepository.findById(id);
    }

    @Transactional
    public List<IspitniRok> findAll() {
        return (List<IspitniRok>) ispitniRokRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        if (!ispitniRokRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Ispitni rok sa ID " + id + " ne postoji.");
        }

        try {
            // 1. Nađi sve ispite u ovom roku
            List<Ispit> ispiti = StreamSupport
                    .stream(ispitRepository.findAll().spliterator(), false)
                    .filter(i -> i.getIspitniRok() != null && i.getIspitniRok().getId().equals(id))
                    .collect(Collectors.toList());

            // 2. Za svaki ispit, obriši kaskadno
            for (Ispit ispit : ispiti) {
                deleteIspitCascade(ispit.getId());
            }

            // 3. Obriši rok
            ispitniRokRepository.deleteById(id);

        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ne može se obrisati ispitni rok jer postoje povezani zapisi: " + e.getMessage());
        }
    }

    // Helper metoda
    private void deleteIspitCascade(Long ispitId) {
        List<IspitPrijava> prijave = ispitPrijavaRepository.findAllByIspitId(ispitId);

        for (IspitPrijava prijava : prijave) {
            if (prijava.getIspitIzlazak() != null) {
                Long izlazakId = prijava.getIspitIzlazak().getId();

                // Obriši PolozenPredmet
                List<PolozenPredmet> polozeni = StreamSupport
                        .stream(polozenPredmetRepository.findAll().spliterator(), false)
                        .filter(pp -> pp.getIspitIzlazak() != null && pp.getIspitIzlazak().getId().equals(izlazakId))
                        .collect(Collectors.toList());

                polozeni.forEach(pp -> polozenPredmetRepository.deleteById(pp.getId()));
                ispitIzlazakRepository.deleteById(izlazakId);
            }

            ispitPrijavaRepository.deleteById(prijava.getId());
        }

        ispitRepository.deleteById(ispitId);
    }
}

