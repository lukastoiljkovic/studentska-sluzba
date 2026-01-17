package org.raflab.studsluzba.model.dtos;

import java.util.List;

import lombok.Data;
import org.raflab.studsluzba.model.entities.SlusaPredmet;
import org.raflab.studsluzba.model.entities.StudentIndeks;


@Data
public class StudentWebProfileDTO {
    private StudentIndeksDTO aktivanIndeks;
    private List<SlusaPredmetDTO> slusaPredmete;
}
