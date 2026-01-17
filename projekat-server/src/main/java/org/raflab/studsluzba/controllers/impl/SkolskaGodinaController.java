package org.raflab.studsluzba.controllers.impl;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.model.entities.SkolskaGodina;
import org.raflab.studsluzba.services.SkolskaGodinaService;
import org.raflab.studsluzba.controllers.response.SkolskaGodinaResponse;
import org.raflab.studsluzba.controllers.request.SkolskaGodinaRequest;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping(path="/api/skolskaGodina")
public class SkolskaGodinaController {

    private final SkolskaGodinaService skolskaGodinaService;

    @PostMapping("/add")
    public SkolskaGodinaResponse addSkolskaGodina(@RequestBody SkolskaGodinaRequest req){
        SkolskaGodina sg = Converters.toSkolskaGodina(req);
        return Converters.toSkolskaGodinaResponse(skolskaGodinaService.addSkolskaGodina(sg));
    }

    @GetMapping("/{id}")
    public SkolskaGodinaResponse getSkolskaGodinaById(@PathVariable Long id){
        Optional<SkolskaGodina> rez = skolskaGodinaService.findById(id);
        return rez.map(Converters::toSkolskaGodinaResponse).orElse(null);
    }

    @GetMapping("/all")
    public List<SkolskaGodinaResponse> getAllSkolskeGodine(){
        return Converters.toSkolskaGodinaResponseList(skolskaGodinaService.findAll());
    }

    @DeleteMapping("/{id}")
    public void deleteSkolskaGodina(@PathVariable Long id){
        skolskaGodinaService.deleteById(id);
    }

}
