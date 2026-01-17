package org.raflab.studsluzbadesktopclient.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SifarniciService {

    private final WebClient webClient;

    // ========== STUDIJSKI PROGRAMI ==========

    public Flux<StudijskiProgramResponse> getAllStudijskiProgrami() {
        return webClient.get()
                .uri("/api/studprogram/all/sorted")
                .retrieve()
                .bodyToFlux(StudijskiProgramResponse.class);
    }

    public Flux<String> getStudijskiProgramiOznake() {
        return webClient.get()
                .uri("/api/studprogram/oznaka/all")
                .retrieve()
                .bodyToFlux(String.class);
    }

    // ========== ŠKOLSKE GODINE ==========

    public Flux<SkolskaGodinaResponse> getAllSkolskeGodine() {
        return webClient.get()
                .uri("/api/skolskaGodina/all")
                .retrieve()
                .bodyToFlux(SkolskaGodinaResponse.class);
    }

    public Mono<SkolskaGodinaResponse> getSkolskaGodinaById(Long id) {
        return webClient.get()
                .uri("/api/skolskaGodina/{id}", id)
                .retrieve()
                .bodyToMono(SkolskaGodinaResponse.class);
    }

    // ========== SREDNJE ŠKOLE ==========

    public Flux<SrednjaSkolaResponse> getAllSrednjeSkole() {
        return webClient.get()
                .uri("/api/srednjaSkola/all")
                .retrieve()
                .bodyToFlux(SrednjaSkolaResponse.class);
    }

    public Mono<Long> saveSrednjaSkola(SrednjaSkolaResponse skola) {
        return webClient.post()
                .uri("/api/srednjaSkola/add")
                .bodyValue(skola)
                .retrieve()
                .bodyToMono(Long.class);
    }

    public Flux<StudentPodaciResponse> getStudentiPoSrednjojSkoli(String nazivSkole) {
        return webClient.post()
                .uri("/api/student/po-srednjoj-skoli?naziv=" + nazivSkole)
                .retrieve()
                .bodyToFlux(StudentPodaciResponse.class);
    }
}