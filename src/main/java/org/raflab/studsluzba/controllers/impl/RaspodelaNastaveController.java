package org.raflab.studsluzba.controllers.impl;

import java.util.List;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.model.entities.DrziPredmet;
import org.raflab.studsluzba.model.entities.Predmet;
import org.raflab.studsluzba.model.entities.SlusaPredmet;
import org.raflab.studsluzba.model.entities.StudentIndeks;
import org.raflab.studsluzba.services.RaspodelaNastaveService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Operacije za raspodelu nastave - koji profesor drzi koji predmet 
 * i koji student slusa koji predmet 
 * 
 */
@CrossOrigin
@AllArgsConstructor
@RestController
@RequestMapping(path = "/api/professor/raspodelanastave")
public class RaspodelaNastaveController {
	
    private final RaspodelaNastaveService raspodelaNastaveService;
	
	@GetMapping(path = "/drzipredmet/aktivna/nastavnik/{idNastavnika}")
	public List<Predmet> getDrziPredmetUAktivnojSkolskojGodini(@PathVariable Long idNastavnika) {
		return raspodelaNastaveService.getPredmetiZaNastavnikaUAktivnojSkolskojGodini(idNastavnika);
	}
	
	@GetMapping(path = "/slusapredmetaktivna/{idPredmeta}/{idNastavnika}")
	public List<StudentIndeks> getSlusaPredmetUAktivnojSkolskojGodini(@PathVariable Long idPredmeta, @PathVariable Long idNastavnika) {
		return raspodelaNastaveService.getStudentiSlusaPredmetAktivnaGodina(idPredmeta, idNastavnika);
	}
	
	@GetMapping(path = "/slusapredmetaktivna/{idDrziPredmet}")
	public List<StudentIndeks> getSlusaPredmetUAktivnojSkolskojGodiniForDrziPredmet(@PathVariable Long idDrziPredmet) {
		return raspodelaNastaveService.getStudentiSlusaPredmetZaDrziPredmet(idDrziPredmet);
	}
	
	@GetMapping(path = "/neslusapredmetaktivna/{idDrziPredmet}")
	public List<StudentIndeks> getNeSlusaPredmetUAktivnojSkolskojGodiniForDrziPredmet(@PathVariable Long idDrziPredmet) {
		return raspodelaNastaveService.getStudentiNeSlusajuDrziPredmet(idDrziPredmet);
	}
	
	@PostMapping(path="/drzipredmet/add") 
   	public Long addDrziPredmet (@RequestBody DrziPredmet drziPredmet) {  	    
   	    return raspodelaNastaveService.addDrziPredmet(drziPredmet).getId();
   	}
	
	@PostMapping(path="/slusapredmet/add") 
   	public Long addSlusaPredmet (@RequestBody SlusaPredmet slusaPredmet) {  	    
   	    return raspodelaNastaveService.addSlusaPredmet(slusaPredmet).getId();
   	}
	
	@DeleteMapping(path="/drzipredmet/{id}")
	public void deleteDrziPredmet(@PathVariable Long id) {
        raspodelaNastaveService.deleteDrziPredmet(id);
    }

}
