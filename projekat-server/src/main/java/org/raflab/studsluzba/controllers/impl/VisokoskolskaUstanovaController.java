package org.raflab.studsluzba.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzba.model.entities.VisokoskolskaUstanova;
import org.raflab.studsluzba.services.VisokoskolskaUstanovaService;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/visokoskolska-ustanova")
public class VisokoskolskaUstanovaController {

    private final VisokoskolskaUstanovaService service;

    @PostMapping("/add")
    public Long add(@RequestBody VisokoskolskaUstanovaRequest req) {
        return service.add(req);
    }

    @GetMapping("/{id}")
    public VisokoskolskaUstanovaResponse get(@PathVariable Long id) {
        Optional<VisokoskolskaUstanova> v = service.findById(id);
        return v.map(Converters::toVisokoskolskaUstanovaResponse).orElse(null);
    }

    @GetMapping("/all")
    public List<VisokoskolskaUstanovaResponse> all() {
        return Converters.toVisokoskolskaUstanovaResponseList(service.findAll());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
