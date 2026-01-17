package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.model.entities.DrziPredmet;
import org.raflab.studsluzba.model.entities.Predmet;
import org.raflab.studsluzba.model.entities.SlusaPredmet;
import org.raflab.studsluzba.model.entities.StudentIndeks;
import org.raflab.studsluzba.repositories.DrziPredmetRepository;
import org.raflab.studsluzba.repositories.SlusaPredmetRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
public class RaspodelaNastaveService {

    private final DrziPredmetRepository drziPredmetRepository;
    private final SlusaPredmetRepository slusaPredmetRepository;

    // ZA DRZI PREDMET

    @Transactional(readOnly = true)
    public List<Predmet> getPredmetiZaNastavnikaUAktivnojSkolskojGodini(Long idNastavnika) {
        return drziPredmetRepository.getPredmetiZaNastavnikaUAktivnojSkolskojGodini(idNastavnika);
    }

    public DrziPredmet addDrziPredmet(DrziPredmet drziPredmet) {
        return drziPredmetRepository.save(drziPredmet);
    }

    @Transactional
    public void deleteDrziPredmet(Long id) {
        if (!drziPredmetRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Entitet sa ID " + id + " ne postoji.");
        }
        try {
            drziPredmetRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ne može se obrisati entitet jer postoje povezani zapisi.");
        }
    }

    // ZA SLUSA PREDMET

    public List<StudentIndeks> getStudentiSlusaPredmetAktivnaGodina(Long idPredmeta, Long idNastavnika) {
        return slusaPredmetRepository.getStudentiSlusaPredmetAktivnaGodina(idPredmeta, idNastavnika);
    }

    public List<StudentIndeks> getStudentiSlusaPredmetZaDrziPredmet(Long idDrziPredmet) {
        return slusaPredmetRepository.getStudentiSlusaPredmetZaDrziPredmet(idDrziPredmet);
    }

    public List<StudentIndeks> getStudentiNeSlusajuDrziPredmet(Long idDrziPredmet) {
        return slusaPredmetRepository.getStudentiNeSlusajuDrziPredmet(idDrziPredmet);
    }

    public SlusaPredmet addSlusaPredmet(SlusaPredmet slusaPredmet) {
        return slusaPredmetRepository.save(slusaPredmet);
    }

}
