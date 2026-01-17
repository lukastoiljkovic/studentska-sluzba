// controllers/impl/TokStudijaController.java
package org.raflab.studsluzba.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzba.services.TokStudijaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/tok-studija")
@RequiredArgsConstructor
public class TokStudijaController {

    private final TokStudijaService service;

    @PostMapping("/add")
    public TokStudijaResponse create(@RequestBody TokStudijaRequest req) {
        return service.create(req);
    }

    @GetMapping("/all")
    public List<TokStudijaResponse> list(@RequestParam(required = false) Long studentIndeksId) {
        return service.list(studentIndeksId);
    }

    @GetMapping("/{id}")
    public TokStudijaResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
