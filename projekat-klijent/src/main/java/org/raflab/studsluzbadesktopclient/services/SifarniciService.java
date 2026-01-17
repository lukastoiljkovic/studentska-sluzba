package org.raflab.studsluzbadesktopclient.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzbadesktopclient.dto.SkolskaGodinaDTO;
import org.raflab.studsluzbadesktopclient.dto.SrednjaSkolaDTO;
import org.raflab.studsluzbadesktopclient.dto.StudijskiProgramDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SifarniciService {

    private final WebClient webClient;

    // ========== STUDIJSKI PROGRAMI ==========

    public Flux<StudijskiProgramDTO> getAllStudijskiProgrami() {
        return webClient.get()
                .uri("/api/studprogram/all/sorted")
                .retrieve()
                .bodyToFlux(StudijskiProgramDTO.class);
    }

    public Flux<String> getStudijskiProgramiOznake() {
        return webClient.get()
                .uri("/api/studprogram/oznaka/all")
                .retrieve()
                .bodyToFlux(String.class);
    }

    // ========== ŠKOLSKE GODINE ==========

    public Flux<SkolskaGodinaDTO> getAllSkolskeGodine() {
        return webClient.get()
                .uri("/api/skolskaGodina/all")
                .retrieve()
                .bodyToFlux(SkolskaGodinaDTO.class);
    }

    public Mono<SkolskaGodinaDTO> getSkolskaGodinaById(Long id) {
        return webClient.get()
                .uri("/api/skolskaGodina/{id}", id)
                .retrieve()
                .bodyToMono(SkolskaGodinaDTO.class);
    }

    // ========== SREDNJE ŠKOLE ==========

    public Flux<SrednjaSkolaDTO> getAllSrednjeSkole() {
        return webClient.get()
                .uri("/api/srednjaSkola/all")
                .retrieve()
                .bodyToFlux(SrednjaSkolaDTO.class);
    }

    public Mono<Long> saveSrednjaSkola(SrednjaSkolaDTO skola) {
        return webClient.post()
                .uri("/api/srednjaSkola/add")
                .bodyValue(skola)
                .retrieve()
                .bodyToMono(Long.class);
    }
}