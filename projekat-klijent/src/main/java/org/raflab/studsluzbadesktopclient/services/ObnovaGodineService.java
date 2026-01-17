package org.raflab.studsluzbadesktopclient.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ObnovaGodineService {

    private final WebClient webClient;

    public Mono<Long> addObnovaZaStudenta(
            Long studentIndeksId,
            Long skolskaGodinaId,
            Integer godinaStudija,
            String napomena,
            Set<Long> predmetiPrethodnaGodinaIds,
            Set<Long> predmetiNarednaGodinaIds,
            LocalDate datum) {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/obnova/add-za-studenta")
                        .queryParam("studentIndeksId", studentIndeksId)
                        .queryParam("skolskaGodinaId", skolskaGodinaId)
                        .queryParam("godinaStudija", godinaStudija)
                        .queryParam("napomena", napomena)
                        .queryParam("predmetiPrethodnaGodinaIds", predmetiPrethodnaGodinaIds)
                        .queryParam("predmetiNarednaGodinaIds", predmetiNarednaGodinaIds)
                        .queryParam("datum", datum)
                        .build())
                .retrieve()
                .bodyToMono(Long.class);
    }

    public Flux<ObnovaGodineResponse> getObnoveForStudent(Long studentIndeksId) {
        return webClient.get()
                .uri("/api/obnova/student/{studentIndeksId}", studentIndeksId)
                .retrieve()
                .bodyToFlux(ObnovaGodineResponse.class);
    }

    public Mono<ObnovaGodineResponse> getById(Long id) {
        return webClient.get()
                .uri("/api/obnova/{id}", id)
                .retrieve()
                .bodyToMono(ObnovaGodineResponse.class);
    }

    public Flux<ObnovaGodineResponse> getAll() {
        return webClient.get()
                .uri("/api/obnova/all")
                .retrieve()
                .bodyToFlux(ObnovaGodineResponse.class);
    }

    public Mono<Void> delete(Long id) {
        return webClient.delete()
                .uri("/api/obnova/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
