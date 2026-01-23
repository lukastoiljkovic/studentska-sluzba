package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.model.entities.*;
import org.raflab.studsluzba.repositories.*;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.raflab.studsluzba.dtos.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class PredmetService {

    private final PredmetRepository predmetRepository;
    private final StudijskiProgramRepository studijskiProgramRepository;
    private final PolozenPredmetRepository polozenPredmetRepository;
    private final DrziPredmetRepository drziPredmetRepository;
    private final PredispitnaObavezaRepository predispitnaObavezaRepository;
    private final SlusaPredmetRepository slusaPredmetRepository;
    private final PredispitnaIzlazakRepository predispitnaIzlazakRepository;
    private final IspitRepository ispitRepository;

    @Transactional
    public Long addPredmet(PredmetRequest req) {
        String rawSifra = req.getSifra();
        if (rawSifra == null || rawSifra.isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Šifra predmeta je obavezna.");
        }
        String sifra = rawSifra.trim().toUpperCase();

        if (predmetRepository.existsBySifraIgnoreCase(sifra)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.CONFLICT, "Predmet sa šifrom '" + sifra + "' već postoji.");
        }

        if (req.getStudProgramId() == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "studProgramId je obavezan.");
        }
        StudijskiProgram sp = studijskiProgramRepository.findById(req.getStudProgramId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Studijski program ne postoji."));

        if (req.getSemestar() == null || req.getSemestar() < 1 || req.getSemestar() > sp.getTrajanjeSemestara()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Semestar mora biti u opsegu 1-" + sp.getTrajanjeSemestara());
        }

        Predmet p = Converters.toPredmet(req, studijskiProgramRepository);
        p.setSifra(sifra);
        p.setStudProgram(sp);

        return predmetRepository.save(p).getId();
    }

    @Transactional
    public Optional<Predmet> getPredmetById(Long id) {
        return predmetRepository.findById(id);
    }

    @Transactional
    public List<PredmetResponse> getAllPredmeti() {
        return Converters.toPredmetResponseList(predmetRepository.findAll());
    }


    @Transactional
    public void deletePredmet(Long id) {
        if (!predmetRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Predmet sa ID " + id + " ne postoji.");
        }

        try {
            List<PredispitnaObaveza> obaveze = StreamSupport
                    .stream(predispitnaObavezaRepository.findAll().spliterator(), false)
                    .filter(po -> po.getPredmet() != null && po.getPredmet().getId().equals(id))
                    .collect(Collectors.toList());

            for (PredispitnaObaveza po : obaveze) {
                List<PredispitnaIzlazak> izlasci = StreamSupport
                        .stream(predispitnaIzlazakRepository.findAll().spliterator(), false)
                        .filter(pi -> pi.getPredispitnaObaveza() != null && pi.getPredispitnaObaveza().getId().equals(po.getId()))
                        .collect(Collectors.toList());

                izlasci.forEach(pi -> predispitnaIzlazakRepository.deleteById(pi.getId()));
                predispitnaObavezaRepository.deleteById(po.getId());
            }

            List<DrziPredmet> drziPredmeti = StreamSupport
                    .stream(drziPredmetRepository.findAll().spliterator(), false)
                    .filter(dp -> dp.getPredmet() != null && dp.getPredmet().getId().equals(id))
                    .collect(Collectors.toList());

            for (DrziPredmet dp : drziPredmeti) {
                List<SlusaPredmet> slusaPredmeti = StreamSupport
                        .stream(slusaPredmetRepository.findAll().spliterator(), false)
                        .filter(sp -> sp.getDrziPredmet() != null && sp.getDrziPredmet().getId().equals(dp.getId()))
                        .collect(Collectors.toList());

                for (SlusaPredmet sp : slusaPredmeti) {
                    List<PredispitnaIzlazak> izlasci = StreamSupport
                            .stream(predispitnaIzlazakRepository.findAll().spliterator(), false)
                            .filter(pi -> pi.getSlusaPredmet() != null && pi.getSlusaPredmet().getId().equals(sp.getId()))
                            .collect(Collectors.toList());
                    izlasci.forEach(pi -> predispitnaIzlazakRepository.deleteById(pi.getId()));

                    slusaPredmetRepository.deleteById(sp.getId());
                }

                drziPredmetRepository.deleteById(dp.getId());
            }

            List<PolozenPredmet> polozeni = StreamSupport
                    .stream(polozenPredmetRepository.findAll().spliterator(), false)
                    .filter(pp -> pp.getPredmet() != null && pp.getPredmet().getId().equals(id))
                    .collect(Collectors.toList());

            if (!polozeni.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Ne može se obrisati predmet jer postoji " + polozeni.size() + " položen(ih) ispit(a).");
            }

            List<Ispit> ispiti = StreamSupport
                    .stream(ispitRepository.findAll().spliterator(), false)
                    .filter(i -> i.getPredmet() != null && i.getPredmet().getId().equals(id))
                    .collect(Collectors.toList());

            if (!ispiti.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Ne može se obrisati predmet jer ima " + ispiti.size() + " zakazan(ih) ispit(a).");
            }

            predmetRepository.deleteById(id);

        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ne može se obrisati predmet jer postoje povezani zapisi.");
        }
    }

    @Transactional
    public List<PredmetResponse> getPredmetiForGodinaAkreditacije(Integer godinaAkreditacije) {
        return Converters.toPredmetResponseList(predmetRepository.getPredmetForGodinaAkreditacije(godinaAkreditacije));
    }

    @Transactional
    public List<PredmetResponse> getPredmetiNaStudijskomProgramu(Long studProgramId) {
        return Converters.toPredmetResponseList(
                predmetRepository.findByStudProgramIdOrderBySemestarAscNazivAsc(studProgramId)
        );
    }

    @Transactional
    public Page<PredmetResponse> getAllPredmetiPaged(int page, int size, String sort, String direction) {
        Sort s = direction.equalsIgnoreCase("desc")
                ? Sort.by(sort).descending()
                : Sort.by(sort).ascending();
        Page<Predmet> p = predmetRepository.findAll(PageRequest.of(page, size, s));
        return p.map(Converters::toPredmetResponse);
    }

    @Transactional
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
