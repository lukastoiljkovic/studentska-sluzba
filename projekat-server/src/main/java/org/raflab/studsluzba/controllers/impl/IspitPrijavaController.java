package org.raflab.studsluzba.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.controllers.request.IspitPrijavaRequest;
import org.raflab.studsluzba.controllers.response.IspitPrijavaResponse;
import org.raflab.studsluzba.model.entities.IspitPrijava;
import org.raflab.studsluzba.services.IspitPrijavaService;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ispit-prijava")
public class IspitPrijavaController {

    private final IspitPrijavaService service;

    @PostMapping("/add")
    public Long add(@RequestBody IspitPrijavaRequest req) {
        return service.add(req);
    }

    @GetMapping("/{id}")
    public IspitPrijavaResponse get(@PathVariable Long id) {
        Optional<IspitPrijava> p = service.findById(id);
        return p.map(Converters::toIspitPrijavaResponse).orElse(null);
    }

    @GetMapping("/all")
    public List<IspitPrijavaResponse> all() {
        return Converters.toIspitPrijavaResponseList(service.findAll());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
