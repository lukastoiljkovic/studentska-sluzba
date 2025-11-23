package org.raflab.studsluzba.controllers.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UplataResponse {
    private Long id;
    private LocalDate datum;
    private Double iznosRSD;
    private Double kurs;
    private Double iznosEUR;
}
