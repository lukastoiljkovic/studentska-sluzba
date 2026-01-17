package org.raflab.studsluzba.controllers.impl;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzba.model.entities.Predmet;
import org.raflab.studsluzba.services.PredmetService;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/predmet")
public class PredmetController {

    private final PredmetService predmetService;

    @PostMapping(path="/add")
    public Long addNewPredmet(@RequestBody @Valid PredmetRequest req) {
        return predmetService.addPredmet(req);
    }

    @GetMapping(path="/{id}")
    public PredmetResponse getPredmetById(@PathVariable Long id) {
        Optional<Predmet> p = predmetService.getPredmetById(id);
        return p.map(Converters::toPredmetResponse).orElse(null);
    }

    @GetMapping(path="/all")
    public List<PredmetResponse> getAllPredmeti() {
        return predmetService.getAllPredmeti();
    }

    @DeleteMapping(path="/{id}")
    public void deletePredmet(@PathVariable Long id) {
        predmetService.deletePredmet(id);
    }

    @GetMapping(path="/all/{godinaAkreditacije}")
    public List<PredmetResponse> getPredmetiForGodinaAkreditacije(@PathVariable Integer godinaAkreditacije) {
        return predmetService.getPredmetiForGodinaAkreditacije(godinaAkreditacije);
    }
    @GetMapping(path="/all/paged")
    public Page<PredmetResponse> getAllPredmetiPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "sifra") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        return predmetService.getAllPredmetiPaged(page, size, sort, direction);
    }
    @GetMapping("/{id}/prosecna-ocena")
    public Double getProsecnaOcena(
            @PathVariable Long id,
            @RequestParam Integer from,
            @RequestParam Integer to
    ) {
        return predmetService.getProsecnaOcenaZaPredmetURasponu(id, from, to);
    }

}
