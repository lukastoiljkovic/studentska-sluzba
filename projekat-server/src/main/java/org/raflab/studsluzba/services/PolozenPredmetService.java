package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzba.model.entities.*;
import org.raflab.studsluzba.repositories.*;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PolozenPredmetService {

    private final PolozenPredmetRepository polozenPredmetRepository;
    private final StudentIndeksRepository studentIndeksRepository;
    private final PredmetRepository predmetRepository;
    private final IspitIzlazakRepository ispitIzlazakRepository;
    private final SlusaPredmetRepository slusaPredmetRepository; // DODAJ OVO

    // POLOŽENI - ostaje isto
    public Page<PolozenPredmetResponse> getPolozeniIspiti(Long studentIndeksId, Pageable pageable) {
        return polozenPredmetRepository
                .findByStudentIndeksIdAndOcenaIsNotNull(studentIndeksId, pageable)
                .map(Converters::toPolozenPredmetResponse);
    }

    /// U servisu
    @Transactional(readOnly = true)
    public Page<NepolozenPredmetResponse> getNepolozeniIspiti(Long studentIndeksId, Pageable pageable) {
        List<SlusaPredmet> slusaPredmete = slusaPredmetRepository
                .findAllByStudentIndeksIdWithPredmet(studentIndeksId);

        List<PolozenPredmet> polozeni = polozenPredmetRepository
                .findByStudentIndeksIdAndOcenaIsNotNull(studentIndeksId);

        Set<Long> polozeniPredmetiIds = polozeni.stream()
                .map(pp -> pp.getPredmet().getId())
                .collect(Collectors.toSet());

        List<NepolozenPredmetResponse> nepolozeni = slusaPredmete.stream()
                .filter(sp -> sp.getDrziPredmet() != null &&
                        sp.getDrziPredmet().getPredmet() != null &&
                        !polozeniPredmetiIds.contains(sp.getDrziPredmet().getPredmet().getId()))
                .map(sp -> {
                    Predmet p = sp.getDrziPredmet().getPredmet();
                    Nastavnik n = sp.getDrziPredmet().getNastavnik();

                    NepolozenPredmetResponse response = new NepolozenPredmetResponse();
                    response.setSlusaPredmetId(sp.getId());
                    response.setPredmetId(p.getId());
                    response.setPredmetSifra(p.getSifra());
                    response.setPredmetNaziv(p.getNaziv());
                    response.setEspb(p.getEspb());
                    response.setSemestar(p.getSemestar());

                    if (n != null) {
                        response.setNastavnikIme(n.getIme() + " " + n.getPrezime());
                    }

                    // Broj izlazaka na ispit
                    long brojIzlazaka = ispitIzlazakRepository
                            .countByStudentIndeks_IdAndIspitPrijava_Ispit_Predmet_Id(studentIndeksId, p.getId());
                    response.setBrojIzlazaka((int) brojIzlazaka);

                    return response;
                })
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), nepolozeni.size());

        return new PageImpl<>(nepolozeni.subList(start, end), pageable, nepolozeni.size());
    }

    public Long addPolozenPredmet(PolozenPredmetRequest req) {
        StudentIndeks si = studentIndeksRepository.findById(req.getStudentIndeksId()).orElseThrow();
        Predmet p = predmetRepository.findById(req.getPredmetId()).orElseThrow();
        IspitIzlazak izlazak = null;
        if(req.getIspitIzlazakId() != null)
            izlazak = ispitIzlazakRepository.findById(req.getIspitIzlazakId()).orElse(null);

        PolozenPredmet pp = Converters.toPolozenPredmet(req, si, p, izlazak);
        return polozenPredmetRepository.save(pp).getId();
    }

    public Optional<PolozenPredmet> findById(Long id) {
        return polozenPredmetRepository.findById(id);
    }

    public List<PolozenPredmet> findAll() {
        return (List<PolozenPredmet>) polozenPredmetRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        if (!polozenPredmetRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Entitet sa ID " + id + " ne postoji.");
        }
        try {
            polozenPredmetRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ne moze se obrisati entitet jer postoje povezani zapisi.");
        }
    }
}
