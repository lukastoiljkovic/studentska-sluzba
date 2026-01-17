package org.raflab.studsluzba.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.controllers.request.PredispitnaObavezaRequest;
import org.raflab.studsluzba.controllers.response.PredispitnaObavezaResponse;
import org.raflab.studsluzba.model.entities.PredispitnaObaveza;
import org.raflab.studsluzba.services.PredispitnaObavezaService;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/predispitna/obaveza")
public class PredispitnaObavezaController {

    private final PredispitnaObavezaService service;

    @PostMapping("/add")
    public Long addNew(@RequestBody @Valid PredispitnaObavezaRequest req) {
        return service.addPredispitnaObaveza(req);
    }

    @GetMapping("/{id}")
    public PredispitnaObavezaResponse getById(@PathVariable Long id) {
        Optional<PredispitnaObaveza> p = service.findById(id);
        return p.map(Converters::toPredispitnaObavezaResponse).orElse(null);
    }

    @GetMapping("/all")
    public List<PredispitnaObavezaResponse> getAll() {
        return Converters.toPredispitnaObavezaResponseList(service.findAll());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
