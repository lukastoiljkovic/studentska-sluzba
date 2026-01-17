package org.raflab.studsluzbadesktopclient.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzbadesktopclient.dto.PredmetDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PredmetService {

    private final WebClient webClient;

    public Flux<PredmetDTO> getAll() {
        return webClient.get()
                .uri("/api/predmet/all")
                .retrieve()
                .bodyToFlux(PredmetDTO.class);
    }

    public Mono<PredmetDTO> getById(Long id) {
        return webClient.get()
                .uri("/api/predmet/{id}", id)
                .retrieve()
                .bodyToMono(PredmetDTO.class);
    }

    public Flux<PredmetDTO> getPredmetiZaProgram(Long studProgramId) {
        return webClient.get()
                .uri("/api/studprogram/{id}/predmeti", studProgramId)
                .retrieve()
                .bodyToFlux(PredmetDTO.class);
    }

    public Mono<Long> save(PredmetDTO predmet) {
        return webClient.post()
                .uri("/api/predmet/add")
                .bodyValue(predmet)
                .retrieve()
                .bodyToMono(Long.class);
    }

    public Mono<Void> delete(Long id) {
        return webClient.delete()
                .uri("/api/predmet/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<Double> getProsecnaOcena(Long predmetId, Integer fromYear, Integer toYear) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/predmet/{id}/prosecna-ocena")
                        .queryParam("from", fromYear)
                        .queryParam("to", toYear)
                        .build(predmetId))
                .retrieve()
                .bodyToMono(Double.class);
    }
}