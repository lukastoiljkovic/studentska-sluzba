package org.raflab.studsluzba.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzba.services.UpisGodineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/upis-godine")
@RequiredArgsConstructor
public class UpisGodineController {

    private final UpisGodineService service;

    /// obnova godine za studenta gde je potrebno omogućiti da se, pored nepoloženih,
    /// izaberu predmeti iz naredne godine. Maksimalni zbir ESPB poena može biti 60.
    /// Dodaje se nova obnova godine za studenta i predmeti koje sluša, a koji će
    /// inicijalno biti nepoloženi.
    @PostMapping("/add")
    public UpisGodineResponse create(@RequestBody UpisGodineRequest req) {
        return service.create(req);
    }

    @GetMapping("/all")
    public List<UpisGodineResponse> list(
            @RequestParam(required = false) Long studentIndeksId,
            @RequestParam(required = false) Long skolskaGodinaId
    ) {
        return service.list(studentIndeksId, skolskaGodinaId);
    }

    @GetMapping("/{id}")
    public UpisGodineResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    /*@PutMapping("/{id}")
    public UpisGodineResponse update(@PathVariable Long id, @RequestBody UpisGodineRequest req) {
        return service.update(id, req);
    }*/

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
