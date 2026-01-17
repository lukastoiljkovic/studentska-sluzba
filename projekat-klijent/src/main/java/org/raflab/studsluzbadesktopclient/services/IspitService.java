package org.raflab.studsluzbadesktopclient.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class IspitService {

    private final WebClient webClient;

    public Flux<IspitResponse> getAll() {
        return webClient.get()
                .uri("/api/ispit/all")
                .retrieve()
                .bodyToFlux(IspitResponse.class);
    }

    public Mono<IspitResponse> getById(Long id) {
        return webClient.get()
                .uri("/api/ispit/{id}", id)
                .retrieve()
                .bodyToMono(IspitResponse.class);
    }

    public Flux<IspitPrijavaResponse> getPrijavljeni(Long ispitId) {
        return webClient.get()
                .uri("/api/ispit/{ispitId}/prijavljeni", ispitId)
                .retrieve()
                .bodyToFlux(IspitPrijavaResponse.class);
    }

    public Flux<IspitRezultatResponse> getRezultati(Long ispitId) {
        return webClient.get()
                .uri("/api/ispit/{ispitId}/rezultati", ispitId)
                .retrieve()
                .bodyToFlux(IspitRezultatResponse.class);
    }

    public Mono<IspitPrijavaResponse> prijaviIspit(Long ispitId, Long studentIndeksId) {
        IspitPrijavaRequest body = new IspitPrijavaRequest();
        body.setIspitId(ispitId);
        body.setStudentIndeksId(studentIndeksId);
        body.setDatum(java.time.LocalDate.now());

        return webClient.post()
                .uri("/api/ispit/{ispitId}/prijavi", ispitId)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(IspitPrijavaResponse.class);
    }

    public Mono<Long> dodajIzlazak(Long ispitPrijavaId, Integer brojPoena, String napomena) {
        IspitIzlazakRequest body = new IspitIzlazakRequest();
        body.setIspitPrijavaId(ispitPrijavaId);
        body.setBrojPoena(brojPoena);
        body.setNapomena(napomena != null ? napomena : "");
        body.setPonistava(false);

        return webClient.post()
                .uri("/api/ispit/izlazak")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Long.class);
    }

    public Mono<Long> save(IspitRequest req) {
        return webClient.post()
                .uri("/api/ispit/add")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(Long.class);
    }

    public Mono<Double> getProsecnaOcena(Long ispitId) {
        return webClient.get()
                .uri("/api/ispit/{ispitId}/prosecna-ocena", ispitId)
                .retrieve()
                .bodyToMono(Double.class);
    }

    public Mono<PredispitniPoeniStudentResponse> getPredispitniPoeni(
            Long studentIndeksId, Long predmetId, Long skGodinaId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/ispit/predispitni-poeni")
                        .queryParam("studentIndeksId", studentIndeksId)
                        .queryParam("predmetId", predmetId)
                        .queryParam("skGodinaId", skGodinaId)
                        .build())
                .retrieve()
                .bodyToMono(PredispitniPoeniStudentResponse.class);
    }

    public Mono<Long> countIzlazaka(Long studentIndeksId, Long predmetId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/ispit/broj-izlazaka")
                        .queryParam("studentIndeksId", studentIndeksId)
                        .queryParam("predmetId", predmetId)
                        .build())
                .retrieve()
                .bodyToMono(Long.class);
    }
}
