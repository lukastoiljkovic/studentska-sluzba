// File: main/java/org/raflab/studsluzbadesktopclient/dtos/PolozeniIspitiDTO.java
package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolozeniIspitiDTO {
    // Header podaci
    private String ime;
    private String prezime;
    private String indeksFormatirano;
    private Double prosecnaOcena;
    private Integer ukupnoEspb;

    // Lista ispita (JasperReports automatski iterira preko ove liste)
    private List<IspitZDTO> ispiti;
}