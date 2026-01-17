package org.raflab.studsluzba.dtos;

import lombok.Data;
import org.raflab.studsluzba.dtos.enums.VrstaSkole;

@Data
public class SrednjaSkolaRequest {
    private String naziv;
    private String mesto;
    private VrstaSkole vrsta;
}