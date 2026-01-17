package org.raflab.studsluzba.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.model.entities.UpisGodine;
import org.raflab.studsluzba.model.entities.Uplata;
import org.raflab.studsluzba.repositories.UpisGodineRepository;
import org.raflab.studsluzba.repositories.UplataRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UplataService {

    private final UplataRepository uplataRepository;
    private final UpisGodineRepository upisGodineRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final double SKOLARINA_EUR = 3000.0;

    public Uplata dodajUplatu(Long upisGodineId, Double iznosEUR) {
        UpisGodine upis = upisGodineRepository.findById(upisGodineId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Upis godine ne postoji"));

        // ========= ISPRAVLJENO: API vraća JSON objekat, ne samo broj =========
        String url = "https://kurs.resenje.org/api/v1/currencies/eur/rates/today";

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || !response.containsKey("exchange_middle")) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                        "Nije moguće dohvatiti kurs EUR.");
            }

            Object kursValue = response.get("exchange_middle");
            double kurs;

            if (kursValue instanceof Number) {
                kurs = ((Number) kursValue).doubleValue();
            } else if (kursValue instanceof String) {
                kurs = Double.parseDouble((String) kursValue);
            } else {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                        "Neispravan format kursa.");
            }

            double iznosRSD = iznosEUR * kurs;

            Uplata uplata = new Uplata();
            uplata.setDatum(LocalDate.now());
            uplata.setIznosRSD(iznosRSD);
            uplata.setIznosEUR(iznosEUR);
            uplata.setKurs(kurs);
            uplata.setUpisGodine(upis);

            return uplataRepository.save(uplata);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Greška pri dohvatanju kursa: " + e.getMessage());
        }
    }

    public double preostaliIznosEUR(Long upisGodineId) {
        List<Uplata> uplate = uplataRepository.findByUpisGodineId(upisGodineId);
        double sumaEUR = uplate.stream()
                .mapToDouble(u -> u.getIznosEUR() != null ? u.getIznosEUR() : (u.getIznosRSD() / u.getKurs()))
                .sum();
        return Math.max(0, SKOLARINA_EUR - sumaEUR);
    }

    public double preostaliIznosRSD(Long upisGodineId) {
        List<Uplata> uplate = uplataRepository.findByUpisGodineId(upisGodineId);
        if (uplate.isEmpty()) return 0;

        double kurs = uplate.get(uplate.size() - 1).getKurs();
        double sumaRSD = uplate.stream().mapToDouble(Uplata::getIznosRSD).sum();
        return Math.max(0, (SKOLARINA_EUR * kurs) - sumaRSD);
    }
}