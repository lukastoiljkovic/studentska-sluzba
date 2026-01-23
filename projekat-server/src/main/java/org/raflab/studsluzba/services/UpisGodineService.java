package org.raflab.studsluzba.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzba.model.entities.*;
import org.raflab.studsluzba.repositories.*;
import org.raflab.studsluzba.utils.Converters;
import org.raflab.studsluzba.utils.EntityMappers;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UpisGodineService {

    private final UpisGodineRepository upisGodineRepository;
    private final StudentIndeksRepository studentIndeksRepository;
    private final SkolskaGodinaRepository skolskaGodinaRepository;
    private final SlusaPredmetRepository slusaPredmetRepository;
    private final DrziPredmetRepository drziPredmetRepository;

    @Transactional
    public List<UpisGodineResponse> findUpisaneGodine(String studProgramOznaka, int godina, int broj) {
        List<UpisGodine> lista = upisGodineRepository
                .findByStudentIndeksStudProgramOznakaAndStudentIndeksGodinaAndStudentIndeksBroj(
                        studProgramOznaka, godina, broj);

        return lista.stream()
                .map(Converters::toUpisGodineResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UpisGodineResponse create(UpisGodineRequest req) {
        StudentIndeks si = studentIndeksRepository.findById(req.getStudentIndeksId())
                .orElseThrow(() -> new NoSuchElementException("StudentIndeks not found"));

        SkolskaGodina sg = skolskaGodinaRepository.findById(req.getSkolskaGodinaId())
                .orElseThrow(() -> new NoSuchElementException("SkolskaGodina not found"));

        UpisGodine u = new UpisGodine();
        u.setStudentIndeks(si);
        u.setSkolskaGodina(sg);
        u.setDatum(req.getDatum());
        u.setNapomena(req.getNapomena());
        u.setGodinaStudija(req.getGodinaStudija());

        if (req.getPredmetiKojePrenosiIds() != null && !req.getPredmetiKojePrenosiIds().isEmpty()) {
            u.setPredmetiKojePrenosi(new HashSet<>(slusaByIds(req.getPredmetiKojePrenosiIds())));
        }

        u = upisGodineRepository.save(u);

        List<DrziPredmet> dpLista = drziPredmetRepository.findBySkolskaGodinaId(sg.getId());

        List<DrziPredmet> predmetiZaGodinu = dpLista.stream()
                .filter(dp -> {
                    int godinaPredmeta = (dp.getPredmet().getSemestar() + 1) / 2;
                    return godinaPredmeta == req.getGodinaStudija() &&
                            dp.getPredmet().getStudProgram().getOznaka()
                                    .equals(si.getStudProgramOznaka());
                })
                .collect(Collectors.toList());

        if (predmetiZaGodinu.isEmpty()) {
            System.out.println("UPOZORENJE: Nema DrziPredmet zapisa za godinu " + req.getGodinaStudija() +
                    " i program " + si.getStudProgramOznaka() + " u školskoj godini " + sg.getNaziv());
        }

        List<SlusaPredmet> slusaList = new ArrayList<>();
        for (DrziPredmet dp : predmetiZaGodinu) {
            SlusaPredmet sp = new SlusaPredmet();
            sp.setStudentIndeks(si);
            sp.setDrziPredmet(dp);
            sp.setSkolskaGodina(sg);
            sp.setGrupa(null);
            slusaList.add(sp);
        }

        if (!slusaList.isEmpty()) {
            slusaPredmetRepository.saveAll(slusaList);
        }

        return Converters.toUpisGodineResponse(u);
    }

    @Transactional(readOnly = true)
    public List<UpisGodineResponse> list(Long studentIndeksId, Long skolskaGodinaId) {
        List<UpisGodine> all = StreamSupport.stream(upisGodineRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        if (studentIndeksId != null) {
            all = all.stream()
                    .filter(u -> u.getStudentIndeks() != null && Objects.equals(u.getStudentIndeks().getId(), studentIndeksId))
                    .collect(Collectors.toList());
        }
        if (skolskaGodinaId != null) {
            all = all.stream()
                    .filter(u -> u.getSkolskaGodina() != null && Objects.equals(u.getSkolskaGodina().getId(), skolskaGodinaId))
                    .collect(Collectors.toList());
        }
        return Converters.toUpisGodineResponseList(all);
    }

    @Transactional(readOnly = true)
    public UpisGodineResponse get(Long id) {
        UpisGodine u = upisGodineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UpisGodine ne postoji: id=" + id));
        return Converters.toUpisGodineResponse(u);
    }

    @Transactional
    public UpisGodineResponse update(Long id, UpisGodineRequest req) {
        UpisGodine u = upisGodineRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("UpisGodine ne postoji: id=" + id));

        if (req.getGodinaStudija() != null) u.setGodinaStudija(req.getGodinaStudija());
        if (req.getDatum() != null) u.setDatum(req.getDatum());
        if (req.getNapomena() != null) u.setNapomena(req.getNapomena());

        if (req.getStudentIndeksId() != null) {
            StudentIndeks si = studentIndeksRepository.findById(req.getStudentIndeksId())
                    .orElseThrow(() -> new NoSuchElementException("StudentIndeks ne postoji: id=" + req.getStudentIndeksId()));
            u.setStudentIndeks(si);
        }
        if (req.getSkolskaGodinaId() != null) {
            SkolskaGodina sg = skolskaGodinaRepository.findById(req.getSkolskaGodinaId())
                    .orElseThrow(() -> new NoSuchElementException("SkolskaGodina ne postoji: id=" + req.getSkolskaGodinaId()));
            u.setSkolskaGodina(sg);
        }

        if (u.getStudentIndeks() != null && u.getSkolskaGodina() != null) {
            boolean conflict = StreamSupport.stream(upisGodineRepository.findAll().spliterator(), false)
                    .anyMatch(other -> !Objects.equals(other.getId(), u.getId())
                            && other.getStudentIndeks() != null
                            && other.getSkolskaGodina() != null
                            && Objects.equals(other.getStudentIndeks().getId(), u.getStudentIndeks().getId())
                            && Objects.equals(other.getSkolskaGodina().getId(), u.getSkolskaGodina().getId()));
            if (conflict) throw new IllegalStateException("Upis za datu školsku godinu već postoji za ovog studenta.");
        }

        if (req.getPredmetiKojePrenosiIds() != null) {
            if (req.getPredmetiKojePrenosiIds().isEmpty()) {
                u.setPredmetiKojePrenosi(Collections.emptySet());
            } else {
                List<SlusaPredmet> sp = slusaByIds(req.getPredmetiKojePrenosiIds());
                u.setPredmetiKojePrenosi(new HashSet<>(sp));
            }
        }

        return Converters.toUpisGodineResponse(upisGodineRepository.save(u));
    }

    @Transactional
    public void delete(Long id) {
        if (!upisGodineRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Entitet sa ID " + id + " ne postoji.");
        }
        try {
            upisGodineRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ne može se obrisati entitet jer postoje povezani zapisi.");
        }
    }

    private static void require(boolean cond, String msg) {
        if (!cond) throw new IllegalArgumentException(msg);
    }

    private List<SlusaPredmet> slusaByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return slusaPredmetRepository.findByIdIn(ids);
    }
}
