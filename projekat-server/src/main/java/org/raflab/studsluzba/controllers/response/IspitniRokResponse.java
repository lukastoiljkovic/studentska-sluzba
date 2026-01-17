package org.raflab.studsluzba.controllers.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IspitniRokResponse {
    private Long id;
    private String naziv;
    private LocalDateTime datumPocetka;
    private LocalDateTime datumZavrsetka;

    private Long skolskaGodinaId;
    private String skolskaGodinaNaziv;
}
