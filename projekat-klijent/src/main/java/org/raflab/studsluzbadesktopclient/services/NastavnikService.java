package org.raflab.studsluzbadesktopclient.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class NastavnikService {

    private final WebClient webClient;

    public Flux<NastavnikResponse> getAll() {
        return webClient.get()
                .uri("/api/nastavnik/all")
                .retrieve()
                .bodyToFlux(NastavnikResponse.class);
    }

    public Flux<NastavnikResponse> search(String ime, String prezime) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/nastavnik/search")
                        .queryParam("ime", ime)
                        .queryParam("prezime", prezime)
                        .build())
                .retrieve()
                .bodyToFlux(NastavnikResponse.class);
    }

    public Mono<NastavnikResponse> getById(Long id) {
        return webClient.get()
                .uri("/api/nastavnik/{id}", id)
                .retrieve()
                .bodyToMono(NastavnikResponse.class);
    }

    public Mono<Long> save(NastavnikResponse nastavnik) {
        return webClient.post()
                .uri("/api/nastavnik/add")
                .bodyValue(nastavnik)
                .retrieve()
                .bodyToMono(Long.class);
    }

    public Mono<Void> delete(Long id) {
        return webClient.delete()
                .uri("/api/nastavnik/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }
}