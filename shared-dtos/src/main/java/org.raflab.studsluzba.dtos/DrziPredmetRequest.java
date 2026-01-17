package org.raflab.studsluzba.dtos;

import lombok.Data;

import java.util.List;

@Data
public class DrziPredmetRequest {

    List<DrziPredmetNewRequest> drziPredmet;
    List<DrziPredmetNewRequest> newDrziPredmet;
}
