package org.raflab.studsluzba.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.controllers.request.IspitIzlazakRequest;
import org.raflab.studsluzba.controllers.request.IspitPrijavaRequest;
import org.raflab.studsluzba.controllers.request.IspitRequest;
import org.raflab.studsluzba.controllers.response.IspitPrijavaResponse;
import org.raflab.studsluzba.controllers.response.IspitRezultatResponse;
import org.raflab.studsluzba.controllers.response.PredispitniPoeniStudentResponse;
import org.raflab.studsluzba.model.entities.*;
import org.raflab.studsluzba.repositories.*;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IspitService {

    private final IspitRepository ispitRepository;
    private final IspitniRokRepository ispitniRokRepository;
    private final NastavnikRepository nastavnikRepository;
    private final PredmetRepository predmetRepository;
    private final IspitPrijavaRepository ispitPrijavaRepository;
    private final PolozenPredmetRepository polozenPredmetRepository;
    private final StudentIndeksRepository studentIndeksRepository;
    private final SlusaPredmetRepository slusaPredmetRepository;
    private final PredispitnaIzlazakRepository predispRepo;
    private final IspitIzlazakRepository ispitIzlazakRepository;

    public Long add(IspitRequest req) {
        IspitniRok rok = ispitniRokRepository.findById(req.getIspitniRokId()).orElseThrow();
        Nastavnik nastavnik = nastavnikRepository.findById(req.getNastavnikId()).orElseThrow();
        Predmet predmet = predmetRepository.findById(req.getPredmetId()).orElseThrow();

        Ispit i = new Ispit();
        i.setDatumVremePocetka(req.getDatumVremePocetka());
        i.setZakljucen(req.isZakljucen());
        i.setIspitniRok(rok);
        i.setNastavnik(nastavnik);
        i.setPredmet(predmet);

        return ispitRepository.save(i).getId();
    }

    public List<IspitPrijavaResponse> getPrijavljeniStudentiZaIspit(Long ispitId) {
        if (!ispitRepository.existsById(ispitId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ispit ne postoji.");
        }
        List<IspitPrijava> prijave = ispitPrijavaRepository.findAllByIspitId(ispitId);
        return Converters.toIspitPrijavaResponseList(prijave);
    }

    public Double getProsecnaOcenaNaIspitu(Long ispitId) {
        if (!ispitRepository.existsById(ispitId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ispit ne postoji.");
        }
        Double avg = polozenPredmetRepository.avgOcenaZaIspit(ispitId);
        return avg != null ? avg : 0.0;
    }

    public IspitPrijavaResponse prijaviIspit(IspitPrijavaRequest req) {
        if (req.getStudentIndeksId() == null || req.getIspitId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "studentIndeksId i ispitId su obavezni.");
        }

        StudentIndeks si = studentIndeksRepository.findById(req.getStudentIndeksId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "StudentIndeks ne postoji."));
        Ispit ispit = ispitRepository.findById(req.getIspitId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ispit ne postoji."));

        if (ispitPrijavaRepository.existsByStudentIndeksIdAndIspitId(si.getId(), ispit.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Student je već prijavljen na ovaj ispit.");
        }

        Long predmetId = ispit.getPredmet().getId();
        Long skGodId = ispit.getIspitniRok().getSkolskaGodina().getId();

        boolean slusa = slusaPredmetRepository
                .existsByStudentIndeksIdAndDrziPredmet_Predmet_IdAndSkolskaGodina_Id(si.getId(), predmetId, skGodId);

        if (!slusa) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Student ne sluša ovaj predmet u odgovarajućoj školskoj godini.");
        }

        IspitPrijava p = new IspitPrijava();
        p.setStudentIndeks(si);
        p.setIspit(ispit);
        p.setDatum(java.time.LocalDate.now());

        IspitPrijava saved = ispitPrijavaRepository.save(p);
        return Converters.toIspitPrijavaResponse(saved);
    }

    public List<IspitRezultatResponse> getRezultatiIspita(Long ispitId) {
        Ispit ispit = ispitRepository.findById(ispitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ispit ne postoji."));

        Long predmetId = ispit.getPredmet().getId();
        Long skGodId = ispit.getIspitniRok().getSkolskaGodina().getId();

        List<IspitPrijava> prijave = ispitPrijavaRepository.findAllByIspitId(ispitId);

        List<IspitRezultatResponse> result = prijave.stream().map(prijava -> {
            StudentIndeks si = prijava.getStudentIndeks();

            Integer predispitni = predispRepo.sumPoeniZaStudentaPredmetGodinu(
                    si.getId(), predmetId, skGodId);

            Integer ispitni = ispitIzlazakRepository
                    .findTopByIspitPrijava_IdAndPonistavaFalseOrderByIdDesc(prijava.getId())
                    .map(IspitIzlazak::getBrojPoena)
                    .orElse(0);

            int ukupno = (predispitni == null ? 0 : predispitni) + (ispitni == null ? 0 : ispitni);

            return IspitRezultatResponse.builder()
                    .studentId(si.getStudent().getId())
                    .studentIndeksId(si.getId())
                    .ime(si.getStudent().getIme())
                    .prezime(si.getStudent().getPrezime())
                    .studProgramOznaka(si.getStudProgramOznaka())
                    .godinaUpisa(si.getGodina())
                    .brojIndeksa(si.getBroj())
                    .predispitni(predispitni == null ? 0 : predispitni)
                    .ispitni(ispitni == null ? 0 : ispitni)
                    .ukupno(ukupno)
                    .build();
        }).sorted(Comparator
                .comparing(IspitRezultatResponse::getStudProgramOznaka, Comparator.nullsLast(String::compareTo))
                .thenComparing(IspitRezultatResponse::getGodinaUpisa, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(IspitRezultatResponse::getBrojIndeksa, Comparator.nullsLast(Integer::compareTo))
        ).collect(Collectors.toList());

        return result;
    }

    @Transactional
    public Long dodajIspitIzlazak(IspitIzlazakRequest req) {
        if (req.getIspitPrijavaId() == null || req.getBrojPoena() == null || req.getBrojPoena() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ispitPrijavaId i pozitivan brojPoena su obavezni.");
        }

        IspitPrijava prijava = ispitPrijavaRepository.findById(req.getIspitPrijavaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ispit prijava ne postoji."));

        if (req.getStudentIndeksId() != null &&
                !prijava.getStudentIndeks().getId().equals(req.getStudentIndeksId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "studentIndeksId ne odgovara prijavi.");
        }

        if (prijava.getIspitIzlazak() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Za ovu prijavu već postoji izlazak.");
        }

        if (prijava.getIspit() != null && prijava.getIspit().isZakljucen()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ispit je zaključen.");
        }

        IspitIzlazak ie = new IspitIzlazak();
        ie.setIspitPrijava(prijava);
        ie.setStudentIndeks(prijava.getStudentIndeks());
        ie.setBrojPoena(req.getBrojPoena());
        ie.setNapomena(req.getNapomena());
        ie.setPonistava(Boolean.TRUE.equals(req.getPonistava()));

        IspitIzlazak sacuvan = ispitIzlazakRepository.save(ie);
        prijava.setIspitIzlazak(sacuvan);
        ispitPrijavaRepository.save(prijava);

        if (Boolean.TRUE.equals(req.getPonistava())) {
            return sacuvan.getId();
        }

        Long siId = prijava.getStudentIndeks().getId();
        Long predmetId = prijava.getIspit().getPredmet().getId();
        Long skGodId = prijava.getIspit().getIspitniRok().getSkolskaGodina().getId();

        Integer predispitni = predispRepo.sumPoeniZaStudentaPredmetGodinu(siId, predmetId, skGodId);
        int ukupno = (predispitni != null ? predispitni : 0) + req.getBrojPoena();

        if (ukupno >= 51) {
            Optional<PolozenPredmet> existing = polozenPredmetRepository.findByStudentIndeksAndPredmet(siId, predmetId);

            PolozenPredmet pp;
            if (existing.isPresent()) {
                pp = existing.get();
                int novaOcena = mapToOcena(ukupno);
                if (novaOcena > pp.getOcena()) {
                    pp.setOcena(novaOcena);
                    pp.setIspitIzlazak(sacuvan);
                }
            } else {
                pp = new PolozenPredmet();
                pp.setPriznat(false);
                pp.setStudentIndeks(prijava.getStudentIndeks());
                pp.setPredmet(prijava.getIspit().getPredmet());
                pp.setIspitIzlazak(sacuvan);
                pp.setOcena(mapToOcena(ukupno));
            }

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
        return 5;
    }

    public PredispitniPoeniStudentResponse getPredispitniPoeni(Long studentIndeksId, Long predmetId, Long skGodId) {
        Integer ukupno = predispRepo.sumPoeniZaStudentaPredmetGodinu(studentIndeksId, predmetId, skGodId);
        if (ukupno == null) ukupno = 0;

        List<PredispitnaIzlazak> list = predispRepo
                .findAllBySlusaPredmet_StudentIndeks_IdAndSlusaPredmet_DrziPredmet_Predmet_IdAndSlusaPredmet_SkolskaGodina_Id(
                        studentIndeksId, predmetId, skGodId);

        List<PredispitniPoeniStudentResponse.Stavka> stavke =
                list.stream().map(pi -> PredispitniPoeniStudentResponse.Stavka.builder()
                        .izlazakId(pi.getId())
                        .slusaPredmetId(pi.getSlusaPredmet().getId())
                        .predispitnaObavezaId(
                                pi.getPredispitnaObaveza() != null ? pi.getPredispitnaObaveza().getId() : null)
                        .vrsta(pi.getPredispitnaObaveza() != null ? pi.getPredispitnaObaveza().getVrsta() : null)
                        .maxPoena(pi.getPredispitnaObaveza() != null ? pi.getPredispitnaObaveza().getMaxPoena() : null)
                        .poeni(pi.getPoeni())
                        .datum(pi.getDatum())
                        .build()
                ).collect(Collectors.toList());

        return PredispitniPoeniStudentResponse.builder()
                .studentIndeksId(studentIndeksId)
                .predmetId(predmetId)
                .skolskaGodinaId(skGodId)
                .ukupno(ukupno)
                .stavke(stavke)
                .build();
    }

    public long countIzlazakaNaPredmet(Long studentIndeksId, Long predmetId) {
        return ispitIzlazakRepository.countByStudentIndeks_IdAndIspitPrijava_Ispit_Predmet_Id(studentIndeksId, predmetId);
    }

    public Optional<Ispit> findById(Long id) {
        return ispitRepository.findById(id);
    }

    public List<Ispit> findAll() {
        return (List<Ispit>) ispitRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        if (!ispitRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Entitet sa ID " + id + " ne postoji.");
        }
        try {
            ispitRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ne moze se obrisati entitet jer postoje povezani zapisi.");
        }
    }
}