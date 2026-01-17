package org.raflab.studsluzba.controllers.impl;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.model.entities.SrednjaSkola;
import org.raflab.studsluzba.services.SrednjaSkolaService;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@AllArgsConstructor
@RequestMapping("/api/srednjaSkola")
public class SrednjaSkolaController {

    private final SrednjaSkolaService srednjaSkolaService;

    @PostMapping("/add")
    public SrednjaSkolaResponse addSrednjaSkola(@RequestBody SrednjaSkolaRequest req){
        SrednjaSkola ss = Converters.toSrednjaSkola(req);
        return Converters.toSrednjaSkolaResponse(srednjaSkolaService.addSrednjaSkola(ss));
    }

    @GetMapping("/{id}")
    public SrednjaSkolaResponse getSrednjaSkolaById(@PathVariable Long id){
        Optional<SrednjaSkola> rez = srednjaSkolaService.findById(id);
        return rez.map(Converters::toSrednjaSkolaResponse).orElse(null);
    }

    @GetMapping("/all")
    public List<SrednjaSkolaResponse> getAllSrednjeSkole(){
        return Converters.toSrednjaSkolaResponseList(srednjaSkolaService.findAll());
    }

    @DeleteMapping("/{id}")
    public void deleteSrednjaSkola(@PathVariable Long id){
        srednjaSkolaService.deleteById(id);
    }

}
