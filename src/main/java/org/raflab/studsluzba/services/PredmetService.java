package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.controllers.request.PredmetRequest;
import org.raflab.studsluzba.controllers.response.PredmetResponse;
import org.raflab.studsluzba.model.entities.Predmet;
import org.raflab.studsluzba.model.entities.StudijskiProgram;
import org.raflab.studsluzba.repositories.PolozenPredmetRepository;
import org.raflab.studsluzba.repositories.PredmetRepository;
import org.raflab.studsluzba.repositories.StudijskiProgramRepository;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PredmetService {

    private final PredmetRepository predmetRepository;
    private final StudijskiProgramRepository studijskiProgramRepository;
    private final PolozenPredmetRepository polozenPredmetRepository;


    public Long addPredmet(PredmetRequest req) {
        // Sanitizacija/normalizacija šifre
        String rawSifra = req.getSifra();
        if (rawSifra == null || rawSifra.isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Šifra predmeta je obavezna.");
        }
        String sifra = rawSifra.trim().toUpperCase();

        // Uniqueness check
        if (predmetRepository.existsBySifraIgnoreCase(sifra)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.CONFLICT, "Predmet sa šifrom '" + sifra + "' već postoji.");
        }

        // Validacija studijskog programa
        if (req.getStudProgramId() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "studProgramId je obavezan.");
        }
        StudijskiProgram sp = studijskiProgramRepository.findById(req.getStudProgramId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Studijski program ne postoji."));

        // (opciono) Validacija semestra u okviru trajanja programa
        if (req.getSemestar() == null || req.getSemestar() < 1 || req.getSemestar() > sp.getTrajanjeSemestara()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Semestar mora biti u opsegu 1-" + sp.getTrajanjeSemestara());
        }

        // Kreiranje entiteta (možeš i direktno bez Converters-a, ali zadržimo vaš stil)
        Predmet p = Converters.toPredmet(req, studijskiProgramRepository);
        p.setSifra(sifra);          // osiguraj normalizovanu vrijednost
        p.setStudProgram(sp);       // osiguraj validan program

        return predmetRepository.save(p).getId();
    }


    public Optional<Predmet> getPredmetById(Long id) {
        return predmetRepository.findById(id);
    }

    public List<PredmetResponse> getAllPredmeti() {
        return Converters.toPredmetResponseList(predmetRepository.findAll());
    }

    @Transactional
    public void deletePredmet(Long id) {
        if (!predmetRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Entitet sa ID " + id + " ne postoji.");
        }
        try {
            predmetRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ne moze se obrisati entitet jer postoje povezani zapisi.");
        }
    }

    public List<PredmetResponse> getPredmetiForGodinaAkreditacije(Integer godinaAkreditacije) {
        return Converters.toPredmetResponseList(predmetRepository.getPredmetForGodinaAkreditacije(godinaAkreditacije));
    }

    public List<PredmetResponse> getPredmetiNaStudijskomProgramu(Long studProgramId) {
        return Converters.toPredmetResponseList(
                predmetRepository.findByStudProgramIdOrderBySemestarAscNazivAsc(studProgramId)
        );
    }

    public Page<PredmetResponse> getAllPredmetiPaged(int page, int size, String sort, String direction) {
        Sort s = direction.equalsIgnoreCase("desc")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();
        Page<Predmet> p = predmetRepository.findAll(PageRequest.of(page, size, s));
        return p.map(Converters::toPredmetResponse);
    }

    public Double getProsecnaOcenaZaPredmetURasponu(Long predmetId, Integer fromYear, Integer toYear) {

        if (fromYear == null || toYear == null || fromYear > toYear) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Raspon godina nije validan.");
        }

        // provjera da predmet postoji
        if (!predmetRepository.existsById(predmetId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "Predmet ne postoji.");
        }

        Double avg = polozenPredmetRepository.findAverageGradeForPredmetAndYearRange(
                predmetId,
                String.valueOf(fromYear),
                String.valueOf(toYear)
        );

        return avg != null ? avg : 0.0;
    }


}
