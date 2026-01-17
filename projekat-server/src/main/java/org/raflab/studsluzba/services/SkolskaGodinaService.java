package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.model.entities.SkolskaGodina;
import org.raflab.studsluzba.repositories.SkolskaGodinaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class SkolskaGodinaService {

    private final SkolskaGodinaRepository skolskaGodinaRepository;

    public SkolskaGodina addSkolskaGodina(SkolskaGodina sg){
        return skolskaGodinaRepository.save(sg);
    }

    public Optional<SkolskaGodina> findById(Long id){
        return skolskaGodinaRepository.findById(id);
    }

    public List<SkolskaGodina> findAll(){
        return skolskaGodinaRepository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        if (!skolskaGodinaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Entitet sa ID " + id + " ne postoji.");
        }
        try {
            skolskaGodinaRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ne može se obrisati entitet jer postoje povezani zapisi.");
        }
    }

}
