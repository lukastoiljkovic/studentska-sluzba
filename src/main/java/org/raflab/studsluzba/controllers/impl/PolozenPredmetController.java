package org.raflab.studsluzba.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.controllers.request.PolozenPredmetRequest;
import org.raflab.studsluzba.controllers.response.PolozenPredmetResponse;
import org.raflab.studsluzba.model.entities.PolozenPredmet;
import org.raflab.studsluzba.services.PolozenPredmetService;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/predmet/polozen")
public class PolozenPredmetController {

    private final PolozenPredmetService service;

    @PostMapping("/add")
    public Long add(@RequestBody PolozenPredmetRequest req) {
        return service.addPolozenPredmet(req);
    }

    @GetMapping("/{id}")
    public PolozenPredmetResponse getById(@PathVariable Long id) {
        Optional<PolozenPredmet> pp = service.findById(id);
        return pp.map(Converters::toPolozenPredmetResponse).orElse(null);
    }

    @GetMapping("/all")
    public List<PolozenPredmetResponse> getAll() {
        return Converters.toPolozenPredmetResponseList(service.findAll());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
