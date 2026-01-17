package org.raflab.studsluzba.dtos;

import java.util.List;

import lombok.Data;


@Data
public class StudentWebProfileDTO {
    private StudentIndeksDTO aktivanIndeks;
    private List<SlusaPredmetDTO> slusaPredmete;
}
