// services/TokStudijaService.java
package org.raflab.studsluzba.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.controllers.request.TokStudijaRequest;
import org.raflab.studsluzba.controllers.response.TokStudijaResponse;
import org.raflab.studsluzba.model.entities.*;
import org.raflab.studsluzba.repositories.*;
import org.raflab.studsluzba.utils.Converters;
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
public class TokStudijaService {

    private final TokStudijaRepository tokStudijaRepository;
    private final StudentIndeksRepository studentIndeksRepository;
    private final UpisGodineRepository upisGodineRepository;
    private final ObnovaGodineRepository obnovaGodineRepository;

    // CREATE
    @Transactional
    public TokStudijaResponse create(TokStudijaRequest req) {
        StudentIndeks si = studentIndeksRepository.findById(req.getStudentIndeksId())
                .orElseThrow(() -> new NoSuchElementException("StudentIndeks ne postoji: id=" + req.getStudentIndeksId()));

        TokStudija ts = new TokStudija();
        ts.setStudentIndeks(si);

        if (req.getUpisGodineIds() != null && !req.getUpisGodineIds().isEmpty()) {
            List<UpisGodine> upisi = upisGodineRepository.findByIdIn(req.getUpisGodineIds());
            ts.setUpisi(new HashSet<>(upisi));
        }
        if (req.getObnovaGodineIds() != null && !req.getObnovaGodineIds().isEmpty()) {
            List<ObnovaGodine> obnove = obnovaGodineRepository.findByIdIn(req.getObnovaGodineIds());
            ts.setObnove(new HashSet<>(obnove));
        }

        TokStudija saved = tokStudijaRepository.save(ts);
        return Converters.toTokStudijaResponse(saved);
    }

    // READ (list/filter)
    @Transactional(readOnly = true)
    public List<TokStudijaResponse> list(Long studentIndeksId) {
        Iterable<TokStudija> src = (studentIndeksId == null)
                ? tokStudijaRepository.findAll()
                : tokStudijaRepository.findAllByStudentIndeksId(studentIndeksId);

        return Converters.toTokStudijaResponseList(src);
    }

    @Transactional(readOnly = true)
    public TokStudijaResponse get(Long id) {
        TokStudija ts = tokStudijaRepository.findByIdWithCollections(id)
                .orElseThrow(() -> new NoSuchElementException("TokStudija ne postoji: id=" + id));
        return Converters.toTokStudijaResponse(ts);
    }

    @Transactional
    public void delete(Long id) {
        if (!tokStudijaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Entitet sa ID " + id + " ne postoji.");
        }
        try {
            tokStudijaRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ne može se obrisati entitet jer postoje povezani zapisi.");
        }
    }
}
