// File: main/java/org/raflab/studsluzbadesktopclient/dtos/IspitZDTO.java
package org.raflab.studsluzbadesktopclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IspitZDTO {
    private String predmetNaziv;
    private Integer ocena;
    private Integer espb;
    private String datumPolaganja; // Format: "dd.MM.yyyy."
    private Integer semestar;

}