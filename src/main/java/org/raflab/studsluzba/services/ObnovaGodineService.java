package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.controllers.request.ObnovaGodineRequest;
import org.raflab.studsluzba.controllers.response.ObnovaGodineResponse;
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

import javax.transaction.Transactional;
import java.time.LocalDate;
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

    public List<ObnovaGodineResponse> getObnoveForStudentIndeks(Long studentIndeksId) {
        return obnovaGodineRepository.findByStudentIndeksId(studentIndeksId)
                .stream()
                .map(Converters::toObnovaResponse)
                .collect(Collectors.toList());
    }

    public Long addObnovaGodineNarednaGodina(Long studentIndeksId,
                                          Long skolskaGodinaId,
                                          Set<Long> predmetiPrethodnaGodinaIds,
                                          Set<Long> predmetiNarednaGodinaIds,
                                          Integer godinaStudija,
                                          String napomena,
                                          LocalDate datum) {

        StudentIndeks si = studentIndeksRepository.findById(studentIndeksId).orElseThrow();
        SkolskaGodina sg = skolskaGodinaRepository.findById(skolskaGodinaId).orElseThrow();

        Set<SlusaPredmet> predmeti = new HashSet<>();

        // predmeti iz prethodne godine (nepoloženi)
        if (predmetiPrethodnaGodinaIds != null) {
            predmeti.addAll(StreamSupport.stream(
                    slusaPredmetRepository.findAllById(predmetiPrethodnaGodinaIds).spliterator(), false
            ).collect(Collectors.toSet()));
        }

        // predmeti iz naredne godine
        if (predmetiNarednaGodinaIds != null) {
            predmeti.addAll(StreamSupport.stream(
                    slusaPredmetRepository.findAllById(predmetiNarednaGodinaIds).spliterator(), false
            ).collect(Collectors.toSet()));
        }

        // provera ukupnog ESPB
        int ukupnoEspb = predmeti.stream()
                .mapToInt(sp -> sp.getDrziPredmet().getPredmet().getEspb())
                .sum();

        if (ukupnoEspb > 60) {
            throw new RuntimeException("Ukupan zbir ESPB poena ne sme da prelazi 60. Trenutno: " + ukupnoEspb);
        }

        // kreiranje nove obnove godine
        ObnovaGodine obnova = new ObnovaGodine();
        obnova.setStudentIndeks(si);
        obnova.setSkolskaGodina(sg);
        obnova.setGodinaStudija(godinaStudija);
        obnova.setDatum(datum);
        obnova.setNapomena(napomena);
        obnova.setPredmetiKojeObnavlja(predmeti);

        obnovaGodineRepository.save(obnova);

        // inicijalno svi predmeti se markiraju kao nepoloženi
        predmeti.forEach(sp -> sp.setStudentIndeks(si));
        slusaPredmetRepository.saveAll(predmeti);

        return obnova.getId();
    }

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

    @Transactional
    public List<ObnovaGodine> findAll() {
        return (List<ObnovaGodine>) obnovaGodineRepository.findAllWithPredmeti();
    }

    public void deleteById(Long id) {
        obnovaGodineRepository.deleteById(id);
    }

}
