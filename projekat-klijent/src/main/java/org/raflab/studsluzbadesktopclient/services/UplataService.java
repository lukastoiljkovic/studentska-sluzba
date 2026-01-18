package org.raflab.studsluzbadesktopclient.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.UplataResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class UplataService{

    private final WebClient webClient;

    public Flux<UplataResponse> getUplateZaUpisGodine(Long upisGodineId) {
        return webClient.get()
                .uri("/api/uplate/upis-godine/{upisGodineId}", upisGodineId)
                .retrieve()
                .bodyToFlux(UplataResponse.class);
    }

}
