package org.raflab.studsluzbadesktopclient.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UpisGodineService {

    private final WebClient webClient;

    public Mono<UpisGodineResponse> create(UpisGodineRequest req) {
        return webClient.post()
                .uri("/api/upis-godine/add")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(UpisGodineResponse.class);
    }

    public Flux<UpisGodineResponse> list(Long studentIndeksId, Long skolskaGodinaId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/upis-godine/all")
                        .queryParam("studentIndeksId", studentIndeksId)
                        .queryParam("skolskaGodinaId", skolskaGodinaId)
                        .build())
                .retrieve()
                .bodyToFlux(UpisGodineResponse.class);
    }

    public Mono<UpisGodineResponse> get(Long id) {
        return webClient.get()
                .uri("/api/upis-godine/{id}", id)
                .retrieve()
                .bodyToMono(UpisGodineResponse.class);
    }

    public Mono<Void> delete(Long id) {
        return webClient.delete()
                .uri("/api/upis-godine/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
