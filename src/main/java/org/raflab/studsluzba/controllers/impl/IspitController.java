package org.raflab.studsluzba.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.controllers.request.IspitIzlazakRequest;
import org.raflab.studsluzba.controllers.request.IspitPrijavaRequest;
import org.raflab.studsluzba.controllers.request.IspitRequest;
import org.raflab.studsluzba.controllers.response.IspitPrijavaResponse;
import org.raflab.studsluzba.controllers.response.IspitResponse;
import org.raflab.studsluzba.controllers.response.IspitRezultatResponse;
import org.raflab.studsluzba.controllers.response.PredispitniPoeniStudentResponse;
import org.raflab.studsluzba.model.entities.Ispit;
import org.raflab.studsluzba.services.IspitService;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ispit")
public class IspitController {

    private final IspitService service;

    @PostMapping("/add")
    public Long add(@RequestBody IspitRequest req) {
        return service.add(req);
    }

    @GetMapping("/{id}")
    public IspitResponse get(@PathVariable Long id) {
        Optional<Ispit> i = service.findById(id);
        return i.map(Converters::toIspitResponse).orElse(null);
    }

    @GetMapping("/all")
    public List<IspitResponse> all() {
        return Converters.toIspitResponseList(service.findAll());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }

    @GetMapping("/{ispitId}/prijavljeni")
    public List<IspitPrijavaResponse> getPrijavljeni(@PathVariable Long ispitId) {
        return service.getPrijavljeniStudentiZaIspit(ispitId);
    }
    @GetMapping("/{ispitId}/prosecna-ocena")
    public Double getProsecnaOcenaNaIspitu(@PathVariable Long ispitId) {
        return service.getProsecnaOcenaNaIspitu(ispitId);
    }
    @PostMapping("/{ispitId}/prijavi")
    public IspitPrijavaResponse prijavi(
            @PathVariable Long ispitId,
            @RequestBody IspitPrijavaRequest req
    ) {
        req.setIspitId(ispitId);
        return service.prijaviIspit(req);
    }
    @GetMapping("/{ispitId}/rezultati")
    public List<IspitRezultatResponse> getRezultati(@PathVariable Long ispitId) {
        return service.getRezultatiIspita(ispitId);
    }
    @PostMapping("/izlazak")
    public Long dodajIzlazak(@RequestBody IspitIzlazakRequest req) {
        return service.dodajIspitIzlazak(req);
    }

    @GetMapping("/predispitni-poeni")
    public PredispitniPoeniStudentResponse getPredispitniPoeni(
            @RequestParam Long studentIndeksId,
            @RequestParam Long predmetId,
            @RequestParam Long skGodinaId
    ) {
        return service.getPredispitniPoeni(studentIndeksId, predmetId, skGodinaId);
    }
    @GetMapping("/broj-izlazaka")
    public Long countIzlazaka(
            @RequestParam Long studentIndeksId,
            @RequestParam Long predmetId
    ) {
        return service.countIzlazakaNaPredmet(studentIndeksId, predmetId);
    }


}
