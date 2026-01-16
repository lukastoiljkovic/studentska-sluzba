package org.raflab.studsluzba.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.controllers.request.PolozenPredmetRequest;
import org.raflab.studsluzba.controllers.response.NepolozenPredmetResponse;
import org.raflab.studsluzba.controllers.response.PolozenPredmetResponse;
import org.raflab.studsluzba.model.entities.PolozenPredmet;
import org.raflab.studsluzba.services.PolozenPredmetService;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/predmet/polozen")
public class PolozenPredmetController {

    private final PolozenPredmetService polozenPredmetService;

    /// - selekcija svih položenih ispita za broj indeksa studenta, paginirano
    @GetMapping("/polozeni/{studentIndeksId}")
    public Page<PolozenPredmetResponse> getPolozeni(
            @PathVariable Long studentIndeksId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return polozenPredmetService.getPolozeniIspiti(studentIndeksId, PageRequest.of(page, size));
    }

    /// - selekcija svih nepoloženih ispita za broj indeksa studenta, paginirano
    @GetMapping("/nepolozeni/{studentIndeksId}")
    public Page<NepolozenPredmetResponse> getNepolozeni(
            @PathVariable Long studentIndeksId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return polozenPredmetService.getNepolozeniIspiti(studentIndeksId, PageRequest.of(page, size));
    }

    @PostMapping("/add")
    public Long add(@RequestBody PolozenPredmetRequest req) {
        return polozenPredmetService.addPolozenPredmet(req);
    }

    @GetMapping("/{id}")
    public PolozenPredmetResponse getById(@PathVariable Long id) {
        Optional<PolozenPredmet> pp = polozenPredmetService.findById(id);
        return pp.map(Converters::toPolozenPredmetResponse).orElse(null);
    }

    @GetMapping("/all")
    public List<PolozenPredmetResponse> getAll() {
        return Converters.toPolozenPredmetResponseList(polozenPredmetService.findAll());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        polozenPredmetService.deleteById(id);
    }
}
