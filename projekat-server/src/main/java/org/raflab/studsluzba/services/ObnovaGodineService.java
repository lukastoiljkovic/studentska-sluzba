package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.controllers.request.ObnovaGodineRequest;
import org.raflab.studsluzba.controllers.response.ObnovaGodineResponse;
import org.raflab.studsluzba.model.entities.*;
import org.raflab.studsluzba.repositories.*;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ObnovaGodineService {

    private final SkolskaGodinaRepository skolskaGodinaRepository;
    private final SlusaPredmetRepository slusaPredmetRepository;
    private final ObnovaGodineRepository obnovaGodineRepository;
    private final StudentIndeksRepository studentIndeksRepository;

    @Transactional
    public List<ObnovaGodineResponse> getObnoveForStudentIndeks(Long studentIndeksId) {
        return obnovaGodineRepository.findByStudentIndeksId(studentIndeksId)
                .stream()
                .map(Converters::toObnovaResponse)
                .collect(Collectors.toList());
    }


    @Transactional
    public Long addObnovaGodineNarednaGodina(Long studentIndeksId,
                                             Long skolskaGodinaId,
                                             Set<Long> predmetiPrethodnaGodinaIds,
                                             Set<Long> predmetiNarednaGodinaIds,
                                             Integer godinaStudija,
                                             String napomena,
                                             LocalDate datum) {

        StudentIndeks si = studentIndeksRepository.findById(studentIndeksId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "StudentIndeks ne postoji"));

        SkolskaGodina sg = skolskaGodinaRepository.findById(skolskaGodinaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SkolskaGodina ne postoji"));

        Set<SlusaPredmet> predmeti = new HashSet<>();
        if (predmetiPrethodnaGodinaIds != null && !predmetiPrethodnaGodinaIds.isEmpty()) {
            predmeti.addAll(slusaPredmetRepository.findByIdIn(predmetiPrethodnaGodinaIds));
        }
        if (predmetiNarednaGodinaIds != null && !predmetiNarednaGodinaIds.isEmpty()) {
            predmeti.addAll(slusaPredmetRepository.findByIdIn(predmetiNarednaGodinaIds));
        }

        int ukupnoEspb = predmeti.stream()
                .mapToInt(sp -> sp.getDrziPredmet().getPredmet().getEspb())
                .sum();

        if (ukupnoEspb > 60) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Ukupan zbir ESPB ne sme biti veći od 60. Trenutno: " + ukupnoEspb);
        }

        ObnovaGodine obnova = new ObnovaGodine();
        obnova.setStudentIndeks(si);
        obnova.setSkolskaGodina(sg);
        obnova.setGodinaStudija(godinaStudija);
        obnova.setDatum(datum);
        obnova.setNapomena(napomena);
        obnova.setPredmetiKojeObnavlja(predmeti);

        return obnovaGodineRepository.save(obnova).getId();
    }

    @Transactional
    public ObnovaGodineResponse addObnova(ObnovaGodineRequest request) {
        // nadji potrebne entitete
        StudentIndeks si = studentIndeksRepository.findById(request.getStudentIndeksId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        SkolskaGodina sg = skolskaGodinaRepository.findById(request.getSkolskaGodinaId())
                .orElseThrow(() -> new RuntimeException("Skolska godina not found"));

        Set<SlusaPredmet> predmeti = new HashSet<>(
                (Collection) slusaPredmetRepository.findAllById(request.getPredmetiKojeObnavljaIds())
        );

        // napravi novi entitet
        ObnovaGodine o = new ObnovaGodine();
        o.setGodinaStudija(request.getGodinaStudija());
        o.setDatum(request.getDatum());
        o.setNapomena(request.getNapomena());
        o.setStudentIndeks(si);
        o.setSkolskaGodina(sg);
        o.setPredmetiKojeObnavlja(predmeti);

        // sacuvaj
        ObnovaGodine saved = obnovaGodineRepository.save(o);

        // konvertuj u DTO (bez bidirekcija)
        return Converters.toObnovaResponse(saved);
    }

    @Transactional(readOnly = true)
    public Optional<ObnovaGodine> findById(Long id) {
        return obnovaGodineRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<ObnovaGodine> findAll() {
        return obnovaGodineRepository.findAllWithPredmeti();
    }

    @Transactional
    public void deleteById(Long id) {
        if (!obnovaGodineRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Entitet sa ID " + id + " ne postoji.");
        }
        try {
            obnovaGodineRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ne može se obrisati obnova jer postoje povezani zapisi.");
        }
    }
}