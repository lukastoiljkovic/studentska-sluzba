package org.raflab.studsluzbadesktopclient.dtos;

import lombok.Data;

@Data
public class ZapisnikHeaderDTO {
    // Osnovni podaci o ispitu
    private String predmetNaziv;
    private String predmetSifra;
    private String ispitniRokNaziv;
    private String datumIspita;
    private String nastavnikImePrezime;

    // Statistika
    private Integer ukupnoPrijavljenih;
    private Integer ukupnoPolozilo;
    private Double prosecnaOcena;

    // Dodatno
    private String skolskaGodinaNaziv;
}