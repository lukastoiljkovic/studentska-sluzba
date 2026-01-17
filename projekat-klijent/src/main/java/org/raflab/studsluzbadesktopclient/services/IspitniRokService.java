package org.raflab.studsluzbadesktopclient.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class IspitniRokService {

    private final WebClient webClient;

    public Flux<IspitniRokResponse> getAll() {
        return webClient.get()
                .uri("/api/ispitni-rok/all")
                .retrieve()
                .bodyToFlux(IspitniRokResponse.class);
    }

    public Mono<IspitniRokResponse> getById(Long id) {
        return webClient.get()
                .uri("/api/ispitni-rok/{id}", id)
                .retrieve()
                .bodyToMono(IspitniRokResponse.class);
    }

    public Mono<Long> save(IspitniRokRequest req) {
        return webClient.post()
                .uri("/api/ispitni-rok/add")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(Long.class);
    }

    public Mono<Void> delete(Long id) {
        return webClient.delete()
                .uri("/api/ispitni-rok/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }
}