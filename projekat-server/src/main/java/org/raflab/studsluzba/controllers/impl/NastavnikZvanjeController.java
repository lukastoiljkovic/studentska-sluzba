package org.raflab.studsluzba.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.controllers.request.NastavnikZvanjeRequest;
import org.raflab.studsluzba.controllers.response.NastavnikZvanjeResponse;
import org.raflab.studsluzba.model.entities.NastavnikZvanje;
import org.raflab.studsluzba.services.NastavnikZvanjeService;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/nastavnik-zvanje")
public class NastavnikZvanjeController {

    private final NastavnikZvanjeService service;

    @PostMapping("/add")
    public Long add(@RequestBody NastavnikZvanjeRequest req) {
        return service.add(req);
    }

    @GetMapping("/{id}")
    public NastavnikZvanjeResponse get(@PathVariable Long id) {
        Optional<NastavnikZvanje> nz = service.findById(id);
        return nz.map(Converters::toNastavnikZvanjeResponse).orElse(null);
    }

    @GetMapping("/all")
    public List<NastavnikZvanjeResponse> all() {
        return Converters.toNastavnikZvanjeResponseList(service.findAll());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
