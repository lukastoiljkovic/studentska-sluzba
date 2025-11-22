package org.raflab.studsluzba.controllers.impl;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.controllers.request.NastavnikRequest;
import org.raflab.studsluzba.controllers.request.ObnovaGodineRequest;
import org.raflab.studsluzba.controllers.request.ObnovaGodineRequest;
import org.raflab.studsluzba.controllers.response.NastavnikResponse;
import org.raflab.studsluzba.controllers.response.ObnovaGodineResponse;
import org.raflab.studsluzba.controllers.response.ObnovaGodineResponse;
import org.raflab.studsluzba.model.entities.Nastavnik;
import org.raflab.studsluzba.model.entities.ObnovaGodine;
import org.raflab.studsluzba.services.ObnovaGodineService;
import org.raflab.studsluzba.utils.Converters;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping(path="/api/obnova")
public class ObnovaGodineController {

    final ObnovaGodineService obnovaGodineService;

    @PostMapping(path="/add")
    public Long addNewObnova(@RequestBody @Valid ObnovaGodineRequest obnovaRequest) {
        return obnovaGodineService.addObnova(obnovaRequest);
    }

    @GetMapping(path = "/{id}")
    public ObnovaGodineResponse getObnovaById(@PathVariable Long id) {
        // servis treba da vraća Optional<ObnovaGodine>
        return obnovaGodineService.findById(id)
                .map(Converters::toObnovaResponse) // promeni sa toObnovaGodineResponse → toObnovaResponse
                .orElse(null);
    }

    @GetMapping(path = "/all")
    public List<ObnovaGodineResponse> getAllObnova() {
        return Converters.toObnovaResponseList(obnovaGodineService.findAll());
    }

    @DeleteMapping("/{id}")
    public void deleteObnova(@PathVariable Long id) {
        obnovaGodineService.deleteById(id);
    }

}
