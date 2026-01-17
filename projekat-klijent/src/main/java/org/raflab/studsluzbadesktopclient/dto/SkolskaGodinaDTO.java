package org.raflab.studsluzbadesktopclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkolskaGodinaDTO {
    private Long id;
    private String naziv;
    private boolean aktivna;

    @Override
    public String toString() {
        return naziv + (aktivna ? " (aktivna)" : "");
    }
}