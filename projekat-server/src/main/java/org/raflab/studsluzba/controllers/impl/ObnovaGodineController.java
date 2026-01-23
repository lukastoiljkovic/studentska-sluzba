package org.raflab.studsluzba.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzba.model.entities.Nastavnik;
import org.raflab.studsluzba.model.entities.ObnovaGodine;
import org.raflab.studsluzba.services.ObnovaGodineService;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/obnova")
public class ObnovaGodineController {

    final ObnovaGodineService obnovaGodineService;

    /// pregled obnovljenih godina po broju indeksa
    @GetMapping("/student/by-indeks/{indeksShort}")
    public List<ObnovaGodineDetailedResponse> getObnoveForStudentByIndeks(
            @PathVariable String indeksShort) {

        // resolve studentIndeksId preko parsera i aktivnog indeksa
        Long studentIndeksId = obnovaGodineService.resolveStudentIndeksId(indeksShort);

        if (studentIndeksId == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Ne postoji aktivan indeks za: " + indeksShort);
        }

        return obnovaGodineService.getObnoveForStudentDetailed(studentIndeksId);
    }


    /*/// - pregled obnovljenih godina za broj indeksa
    @GetMapping("/student/{studentIndeksId}")
    public List<ObnovaGodineDetailedResponse> getObnoveForStudent(@PathVariable Long studentIndeksId) {
        return obnovaGodineService.getObnoveForStudentDetailed(studentIndeksId);
    }*/

    /// obnova godine za studenta gde je potrebno omogućiti da se, pored nepoloženih,
    /// izaberu predmeti iz naredne godine. Maksimalni zbir ESPB poena može biti 60.
    /// Dodaje se nova obnova godine za studenta i predmeti koje sluša, a koji će inicijalno
    /// biti nepoloženi.
    @PostMapping("/add-za-studenta")
    public Long addObnovaZaStudenta(
            @RequestParam Long studentIndeksId,
            @RequestParam Long skolskaGodinaId,
            @RequestParam Integer godinaStudija,
            @RequestParam(required = false) String napomena,
            @RequestParam(required = false) Set<Long> predmetiPrethodnaGodinaIds,
            @RequestParam(required = false) Set<Long> predmetiNarednaGodinaIds,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate datum
    ) {
        if (datum == null) {
            datum = LocalDate.now();
        }
        return obnovaGodineService.addObnovaGodineNarednaGodina(
                studentIndeksId, skolskaGodinaId, predmetiPrethodnaGodinaIds,
                predmetiNarednaGodinaIds, godinaStudija, napomena, datum
        );
    }

    @PostMapping(path="/add")
    public ObnovaGodineResponse addObnova(@RequestBody @Valid ObnovaGodineRequest request) {
        return obnovaGodineService.addObnova(request);
    }

    @GetMapping(path = "/{id}")
    public ObnovaGodineDetailedResponse getObnovaById(@PathVariable Long id) {
        return obnovaGodineService.getObnovaDetailed(id);
    }

    @GetMapping(path = "/all")
    public List<ObnovaGodineResponse> getAllObnova() {
        return Converters.toObnovaResponseList(obnovaGodineService.findAll());
    }

    @DeleteMapping("/{id}")
    public void deleteObnova(@PathVariable Long id) {
        obnovaGodineService.deleteById(id);
    }

}
