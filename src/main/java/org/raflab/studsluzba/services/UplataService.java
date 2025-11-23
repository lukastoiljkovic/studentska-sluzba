package org.raflab.studsluzba.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.entities.UpisGodine;
import org.raflab.studsluzba.model.entities.Uplata;
import org.raflab.studsluzba.repositories.UpisGodineRepository;
import org.raflab.studsluzba.repositories.UplataRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UplataService {

    private final UplataRepository uplataRepository;
    private final UpisGodineRepository upisGodineRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final double SKOLARINA_EUR = 3000.0;

    public Uplata dodajUplatu(Long upisGodineId, Double iznosEUR) {
        UpisGodine upis = upisGodineRepository.findById(upisGodineId)
                .orElseThrow(() -> new RuntimeException("Upis godine ne postoji"));

        // dohvat trenutnog srednjeg kursa EUR -> RSD
        String url = "https://kurs.resenje.org/api/v1/currencies/eur/rates/today";
        Double kurs = restTemplate.getForObject(url, Double.class);

        if (kurs == null) throw new RuntimeException("Ne mogu da dohvatim kurs");

        double iznosRSD = iznosEUR * kurs;

        Uplata uplata = new Uplata();
        uplata.setDatum(LocalDate.now());
        uplata.setIznosRSD(iznosRSD);
        uplata.setKurs(kurs);
        uplata.setUpisGodine(upis);

        return uplataRepository.save(uplata);
    }

    public double preostaliIznosEUR(Long upisGodineId) {
        List<Uplata> uplate = uplataRepository.findByUpisGodineId(upisGodineId);
        double sumaEUR = uplate.stream().mapToDouble(u -> u.getIznosRSD() / u.getKurs()).sum();
        return SKOLARINA_EUR - sumaEUR;
    }

    public double preostaliIznosRSD(Long upisGodineId) {
        List<Uplata> uplate = uplataRepository.findByUpisGodineId(upisGodineId);
        double sumaRSD = uplate.stream().mapToDouble(Uplata::getIznosRSD).sum();
        // koristimo kurs poslednje uplate
        double kurs = uplate.isEmpty() ? 0 : uplate.get(uplate.size() - 1).getKurs();
        return (kurs == 0) ? 0 : (SKOLARINA_EUR * kurs - sumaRSD);
    }
}

