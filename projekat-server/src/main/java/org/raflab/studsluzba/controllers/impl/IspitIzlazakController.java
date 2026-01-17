package org.raflab.studsluzba.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.controllers.request.IspitIzlazakRequest;
import org.raflab.studsluzba.controllers.response.IspitIzlazakResponse;
import org.raflab.studsluzba.model.entities.IspitIzlazak;
import org.raflab.studsluzba.services.IspitIzlazakService;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ispit-izlazak")
public class IspitIzlazakController {

    final IspitIzlazakService service;

    // POST /add
    @PostMapping("/add")
    public Long add(@RequestBody IspitIzlazakRequest req) {
        return service.add(req);
    }

    // GET /{id}
    @GetMapping("/{id}")
    public IspitIzlazakResponse get(@PathVariable Long id) {
        Optional<IspitIzlazak> e = service.findById(id);
        return e.map(Converters::toIspitIzlazakResponse).orElse(null);
    }

    // GET /all
    @GetMapping("/all")
    public List<IspitIzlazakResponse> all() {
        return Converters.toIspitIzlazakResponseList(service.findAll());
    }

    // DELETE /{id}
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
