package org.raflab.studsluzba.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.controllers.request.GrupaRequest;
import org.raflab.studsluzba.controllers.response.GrupaResponse;
import org.raflab.studsluzba.model.entities.Grupa;
import org.raflab.studsluzba.services.GrupaService;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/grupa")
public class GrupaController {

    final GrupaService grupaService;

    @PostMapping("/add")
    public Long addGrupa(@RequestBody GrupaRequest req) {
        return grupaService.addGrupa(req);
    }

    @GetMapping("/{id}")
    public GrupaResponse getGrupa(@PathVariable Long id) {
        Optional<Grupa> g = grupaService.findById(id);
        return g.map(Converters::toGrupaResponse).orElse(null);
    }

    @GetMapping("/all")
    public Iterable<GrupaResponse> getAll() {
        return Converters.toGrupaResponseList(grupaService.findAll());
    }

    @DeleteMapping("/{id}")
    public void deleteGrupa(@PathVariable Long id) {
        grupaService.deleteById(id);
    }
}
