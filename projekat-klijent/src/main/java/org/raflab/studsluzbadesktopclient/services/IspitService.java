package org.raflab.studsluzbadesktopclient.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzbadesktopclient.dto.IspitDTO;
import org.raflab.studsluzbadesktopclient.dto.IspitPrijavaDTO;
import org.raflab.studsluzbadesktopclient.dto.IspitRezultatDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class IspitService {

    private final WebClient webClient;

    public Flux<IspitDTO> getAll() {
        return webClient.get()
                .uri("/api/ispit/all")
                .retrieve()
                .bodyToFlux(IspitDTO.class);
    }

    public Mono<IspitDTO> getById(Long id) {
        return webClient.get()
                .uri("/api/ispit/{id}", id)
                .retrieve()
                .bodyToMono(IspitDTO.class);
    }

    public Flux<IspitPrijavaDTO> getPrijavljeni(Long ispitId) {
        return webClient.get()
                .uri("/api/ispit/{ispitId}/prijavljeni", ispitId)
                .retrieve()
                .bodyToFlux(IspitPrijavaDTO.class);
    }

    public Flux<IspitRezultatDTO> getRezultati(Long ispitId) {
        return webClient.get()
                .uri("/api/ispit/{ispitId}/rezultati", ispitId)
                .retrieve()
                .bodyToFlux(IspitRezultatDTO.class);
    }

    public Mono<IspitPrijavaDTO> prijaviIspit(Long ispitId, Long studentIndeksId) {
        Map<String, Object> body = Map.of(
                "ispitId", ispitId,
                "studentIndeksId", studentIndeksId,
                "datum", java.time.LocalDate.now().toString()
        );

        return webClient.post()
                .uri("/api/ispit/{ispitId}/prijavi", ispitId)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(IspitPrijavaDTO.class);
    }

    public Mono<Long> dodajIzlazak(Long ispitPrijavaId, Integer brojPoena, String napomena) {
        Map<String, Object> body = Map.of(
                "ispitPrijavaId", ispitPrijavaId,
                "brojPoena", brojPoena,
                "napomena", napomena != null ? napomena : "",
                "ponistava", false
        );

        return webClient.post()
                .uri("/api/ispit/izlazak")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Long.class);
    }

    public Mono<Double> getProsecnaOcena(Long ispitId) {
        return webClient.get()
                .uri("/api/ispit/{ispitId}/prosecna-ocena", ispitId)
                .retrieve()
                .bodyToMono(Double.class);
    }
}