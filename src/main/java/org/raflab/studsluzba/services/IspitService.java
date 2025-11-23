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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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
        // 404 ako ispit ne postoji (lepši API)
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

        // zabrana duple prijave
        if (ispitPrijavaRepository.existsByStudentIndeksIdAndIspitId(si.getId(), ispit.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Student je već prijavljen na ovaj ispit.");
        }

        // student mora slušati TAJ predmet u istoj školskoj godini kao i ispitni rok
        Long predmetId = ispit.getPredmet().getId();
        Long skGodId  = ispit.getIspitniRok().getSkolskaGodina().getId();

        boolean slusa = slusaPredmetRepository
                .existsByStudentIndeksIdAndDrziPredmet_Predmet_IdAndSkolskaGodina_Id(si.getId(), predmetId, skGodId);

        if (!slusa) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Student ne sluša ovaj predmet u odgovarajućoj školskoj godini.");
        }

        // snimi prijavu
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
        Long skGodId   = ispit.getIspitniRok().getSkolskaGodina().getId();

        List<IspitPrijava> prijave = ispitPrijavaRepository.findAllByIspitId(ispitId);

        List<IspitRezultatResponse> result = prijave.stream().map(prijava -> {
            StudentIndeks si = prijava.getStudentIndeks();

            // predispitni = suma svih PredispitnaIzlazak za (si, predmet, sk.godina)
            Integer predispitni = predispRepo.sumPoeniZaStudentaPredmetGodinu(
                    si.getId(), predmetId, skGodId);

            // ispitni = poslednji neponišten izlazak za ovu prijavu (ili 0 ako nema)
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
    public Long dodajIspitIzlazak(IspitIzlazakRequest req) {
        if (req.getIspitPrijavaId() == null || req.getStudentIndeksId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ispitPrijavaId i studentIndeksId su obavezni.");
        }

        IspitPrijava prijava = ispitPrijavaRepository.findById(req.getIspitPrijavaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "IspitPrijava ne postoji."));

        if (!prijava.getStudentIndeks().getId().equals(req.getStudentIndeksId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prijava i studentIndeks nisu usklađeni.");
        }

        // SNIMANJE IZLASKA
        IspitIzlazak izl = new IspitIzlazak();
        izl.setIspitPrijava(prijava);
        izl.setBrojPoena(req.getBrojPoena());
        izl.setPonistava(Boolean.TRUE.equals(req.getPonistava()));
        izl.setNapomena(req.getNapomena());

        IspitIzlazak sacuvan = ispitIzlazakRepository.save(izl);

        if (Boolean.TRUE.equals(req.getPonistava())) {
            return sacuvan.getId();
        }

        // IZRAČUNAVANJE UKUPNIH POENA
        Long siId = prijava.getStudentIndeks().getId();
        Long predmetId = prijava.getIspit().getPredmet().getId();
        Long skGodId = prijava.getIspit().getIspitniRok().getSkolskaGodina().getId();

        Integer predispitni = predispRepo.sumPoeniZaStudentaPredmetGodinu(siId, predmetId, skGodId);
        int ispitni = req.getBrojPoena() == null ? 0 : req.getBrojPoena();
        int ukupno = (predispitni == null ? 0 : predispitni) + ispitni;

        if (ukupno >= 51) {
            int ocena = izracunajOcenu(ukupno);

            Optional<PolozenPredmet> postoji =
                    polozenPredmetRepository.findByStudentIndeksAndPredmet(siId, predmetId);

            PolozenPredmet pp = postoji.orElseGet(PolozenPredmet::new);
            pp.setStudentIndeks(prijava.getStudentIndeks());
            pp.setPredmet(prijava.getIspit().getPredmet());
            pp.setIspitIzlazak(sacuvan);
            pp.setOcena(ocena);
            polozenPredmetRepository.save(pp);
        }

        return sacuvan.getId();
    }

    private int izracunajOcenu(int poeni) {
        if (poeni < 51) return 5;
        if (poeni <= 60) return 6;
        if (poeni <= 70) return 7;
        if (poeni <= 80) return 8;
        if (poeni <= 90) return 9;
        return 10;
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

    public void deleteById(Long id) {
        ispitRepository.deleteById(id);
    }
}
