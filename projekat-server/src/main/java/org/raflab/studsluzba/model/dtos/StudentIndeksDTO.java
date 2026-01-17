package org.raflab.studsluzba.model.dtos;

import lombok.Data;

@Data
public class StudentIndeksDTO {
    private Long id;
    private int broj;
    private int godina;
    private String studProgramOznaka;
    private boolean aktivan;
}
