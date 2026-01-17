package org.raflab.studsluzba.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.UplataResponse;
import org.raflab.studsluzba.model.entities.Uplata;
import org.raflab.studsluzba.repositories.UplataRepository;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/uplate")
@RequiredArgsConstructor
public class UplataController {

    private final UplataRepository uplataRepository;

    @GetMapping("/upis-godine/{upisGodineId}")
    public List<UplataResponse> getUplateZaUpisGodine(
            @PathVariable Long upisGodineId) {

        List<Uplata> uplate = uplataRepository.findByUpisGodineId(upisGodineId);
        return Converters.toUplataResponseList(uplate);
    }
}
