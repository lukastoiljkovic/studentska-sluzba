package org.raflab.studsluzba.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzba.model.entities.IspitniRok;
import org.raflab.studsluzba.services.IspitniRokService;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ispitni-rok")
public class IspitniRokController {

    final IspitniRokService service;

    // POST /add
    @PostMapping("/add")
    public Long add(@RequestBody @Valid IspitniRokRequest req) {
        return service.add(req);
    }

    // GET /{id}
    @GetMapping("/{id}")
    public IspitniRokResponse get(@PathVariable Long id) {
        Optional<IspitniRok> x = service.findById(id);
        return x.map(Converters::toIspitniRokResponse).orElse(null);
    }

    // GET /all
    @GetMapping("/all")
    public List<IspitniRokResponse> all() {
        return Converters.toIspitniRokResponseList(service.findAll());
    }

    // DELETE /{id}
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
