package org.raflab.studsluzbadesktopclient.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PredmetService {

    private final WebClient webClient;

    public Mono<PredmetResponse> getById(Long id) {
        return webClient.get()
                .uri("/api/predmet/{id}", id)
                .retrieve()
                .bodyToMono(PredmetResponse.class);
    }

    public Flux<PredmetResponse> getPredmetiZaProgram(Long studProgramId) {
        return webClient.get()
                .uri("/api/studprogram/{id}/predmeti", studProgramId)
                .retrieve()
                .bodyToFlux(PredmetResponse.class);
    }

    public Mono<List<PredmetResponse>> getPredmetiNaStudijskomProgramu(Long studProgramId) {
        return webClient.get()
                .uri("/api/studprogram/{id}/predmeti", studProgramId)
                .retrieve()
                .bodyToFlux(PredmetResponse.class)
                .collectList();
    }

    public Mono<Void> delete(Long id) {
        return webClient.delete()
                .uri("/api/predmet/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }


    public Flux<PredmetResponse> getAll() {
        return webClient.get()
                .uri("/api/predmet/all")
                .retrieve()
                .bodyToFlux(PredmetResponse.class);
    }

    public Mono<Long> save(PredmetRequest req) {
        return webClient.post()
                .uri("/api/predmet/add")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(Long.class);
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