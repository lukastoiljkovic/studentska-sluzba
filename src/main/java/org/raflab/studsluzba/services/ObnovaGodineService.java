package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.controllers.request.ObnovaGodineRequest;
import org.raflab.studsluzba.model.entities.ObnovaGodine;
import org.raflab.studsluzba.model.entities.SkolskaGodina;
import org.raflab.studsluzba.model.entities.SlusaPredmet;
import org.raflab.studsluzba.model.entities.StudentIndeks;
import org.raflab.studsluzba.repositories.ObnovaGodineRepository;
import org.raflab.studsluzba.repositories.SkolskaGodinaRepository;
import org.raflab.studsluzba.repositories.SlusaPredmetRepository;
import org.raflab.studsluzba.repositories.StudentIndeksRepository;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@AllArgsConstructor
public class ObnovaGodineService {

    private final SkolskaGodinaRepository skolskaGodinaRepository;
    private final SlusaPredmetRepository slusaPredmetRepository;
    private final ObnovaGodineRepository obnovaGodineRepository;
    private final StudentIndeksRepository studentIndeksRepository;

    public Long addObnova(ObnovaGodineRequest req) {

        StudentIndeks si = studentIndeksRepository.findById(req.getStudentIndeksId()).orElseThrow();
        SkolskaGodina sg = skolskaGodinaRepository.findById(req.getSkolskaGodinaId()).orElseThrow();

        Set<SlusaPredmet> predmeti =
                StreamSupport.stream(
                        slusaPredmetRepository.findAllById(req.getPredmetiKojeObnavljaIds()).spliterator(),
                        false
                ).collect(Collectors.toSet());

        ObnovaGodine o = Converters.toObnova(req, si, sg, predmeti);

        return obnovaGodineRepository.save(o).getId();
    }

    public Optional<ObnovaGodine> findById(Long id) {
        return obnovaGodineRepository.findById(id);
    }

    public List<ObnovaGodine> findAll() {
        return (List<ObnovaGodine>) obnovaGodineRepository.findAll();
    }

    public void deleteById(Long id) {
        obnovaGodineRepository.deleteById(id);
    }

}
