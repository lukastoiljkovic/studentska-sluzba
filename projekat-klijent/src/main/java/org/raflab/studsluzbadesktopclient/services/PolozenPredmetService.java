package org.raflab.studsluzbadesktopclient.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PolozenPredmetService {

    private final WebClient webClient;

    public Mono<PageResponse<PolozenPredmetResponse>> getPolozeniIspiti(Long studentIndeksId, int page, int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/predmet/polozen/polozeni/{studentIndeksId}")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build(studentIndeksId))
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<
                        PageResponse<PolozenPredmetResponse>>() {});
    }

    public Mono<PageResponse<NepolozenPredmetResponse>> getNepolozeniIspiti(Long studentIndeksId, int page, int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/predmet/polozen/nepolozeni/{studentIndeksId}")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build(studentIndeksId))
                .retrieve()
                .bodyToMono(new org.springframework.core.ParameterizedTypeReference<
                        PageResponse<NepolozenPredmetResponse>>() {});
    }
    public Mono<Long> add(PolozenPredmetRequest req) {
        return webClient.post()
                .uri("/api/predmet/polozen/add")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(Long.class);
    }

    public Mono<Void> delete(Long id) {
        return webClient.delete()
                .uri("/api/predmet/polozen/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }
}