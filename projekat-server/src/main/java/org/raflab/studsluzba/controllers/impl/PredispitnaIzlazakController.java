package org.raflab.studsluzba.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzba.model.entities.PredispitnaIzlazak;
import org.raflab.studsluzba.services.PredispitnaIzlazakService;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/predispitna/izlazak")
public class PredispitnaIzlazakController {

    private final PredispitnaIzlazakService service;

    @PostMapping("/add")
    public Long addNewPredispitna(@RequestBody @Valid PredispitnaIzlazakRequest req) {
        return service.addPredispitnaIzlazak(req);
    }

    @GetMapping("/{id}")
    public PredispitnaIzlazakResponse getById(@PathVariable Long id) {
        PredispitnaIzlazak p = service.findById(id);
        return p != null ? Converters.toPredispitnaIzlazakResponse(p) : null;
    }

    @GetMapping("/all")
    public List<PredispitnaIzlazakResponse> getAll() {
        return Converters.toPredispitnaIzlazakResponseList(service.findAll());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}