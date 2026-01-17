package org.raflab.studsluzba.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
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
@RequestMapping(path = "/api/predmet/polozen")
public class PolozenPredmetController {

    private final PolozenPredmetService polozenPredmetService;

    // selekcija svih položenih ispita za broj indeksa studenta, paginirano
    @GetMapping("/polozeni/{studentIndeksId}")
    public PageResponse<PolozenPredmetResponse> getPolozeni(
            @PathVariable Long studentIndeksId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PolozenPredmetResponse> p = polozenPredmetService
                .getPolozeniIspiti(studentIndeksId, PageRequest.of(page, size));
        return toPageResponse(p, page, size);
    }

    // selekcija svih nepoloženih ispita za broj indeksa studenta, paginirano
    @GetMapping("/nepolozeni/{studentIndeksId}")
    public PageResponse<NepolozenPredmetResponse> getNepolozeni(
            @PathVariable Long studentIndeksId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<NepolozenPredmetResponse> p = polozenPredmetService
                .getNepolozeniIspiti(studentIndeksId, PageRequest.of(page, size));
        return toPageResponse(p, page, size);
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

    // helper: pretvara Spring Page u tvoj PageResponse (POJO)
    private <T> PageResponse<T> toPageResponse(Page<T> p, int page, int size) {
        PageResponse<T> dto = new PageResponse<>();
        dto.setContent(p.getContent());
        dto.setPage(page);
        dto.setSize(size);
        dto.setTotalElements(p.getTotalElements());
        dto.setTotalPages(p.getTotalPages());
        dto.setLast(p.isLast());
        return dto;
    }
}
