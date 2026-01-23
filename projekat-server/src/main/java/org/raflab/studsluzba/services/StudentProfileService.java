package org.raflab.studsluzba.services;

import lombok.AllArgsConstructor;
import org.raflab.studsluzba.dtos.SlusaPredmetDTO;
import org.raflab.studsluzba.dtos.StudentIndeksDTO;
import org.raflab.studsluzba.model.entities.PolozenPredmet;
import org.raflab.studsluzba.model.entities.SlusaPredmet;
import org.raflab.studsluzba.model.entities.StudentIndeks;
import org.raflab.studsluzba.dtos.StudentProfileDTO;
import org.raflab.studsluzba.dtos.StudentWebProfileDTO;
import org.raflab.studsluzba.repositories.PolozenPredmetRepository;
import org.raflab.studsluzba.repositories.SlusaPredmetRepository;
import org.raflab.studsluzba.repositories.StudentIndeksRepository;
import org.raflab.studsluzba.repositories.StudentPodaciRepository;
import org.raflab.studsluzba.utils.ParseUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.raflab.studsluzba.repositories.UpisGodineRepository;
import org.raflab.studsluzba.repositories.UplataRepository;
import org.raflab.studsluzba.utils.Converters;
import org.raflab.studsluzba.model.entities.UpisGodine;
import org.raflab.studsluzba.model.entities.Uplata;
import org.raflab.studsluzba.dtos.UplataResponse;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class StudentProfileService {

    final StudentIndeksRepository studentIndeksRepo;
    final StudentPodaciRepository studentPodaciRepo;
    final SlusaPredmetRepository slusaPredmetRepo;
    final PolozenPredmetRepository polozenPredmetRepo; 
    final StudentIndeksService studentIndeksService;
    final UpisGodineRepository upisGodineRepository;
    final UplataRepository uplataRepository;

    @Transactional(readOnly = true)
    public StudentProfileDTO getStudentProfile(Long indeksId) {
        StudentIndeks studIndeks = studentIndeksRepo.findById(indeksId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Indeks ne postoji"));

        StudentProfileDTO retVal = new StudentProfileDTO();

        // popuni indeks DTO
        StudentIndeksDTO indeksDTO = new StudentIndeksDTO();
        indeksDTO.setId(studIndeks.getId());
        indeksDTO.setBroj(studIndeks.getBroj());
        indeksDTO.setGodina(studIndeks.getGodina());
        indeksDTO.setStudProgramOznaka(studIndeks.getStudProgramOznaka());
        indeksDTO.setAktivan(studIndeks.isAktivan());
        retVal.setIndeks(indeksDTO);

        // dobavi predmete koje slusa
        List<SlusaPredmet> slusaPredmete = slusaPredmetRepo.getSlusaPredmetForIndeksAktivnaGodina(indeksId);

        // mapiranje u DTO
        List<SlusaPredmetDTO> spDTO = new ArrayList<>();
        for (SlusaPredmet slusaPredmet : slusaPredmete) {
            SlusaPredmetDTO dto = new SlusaPredmetDTO();
            if (slusaPredmet.getDrziPredmet() != null && slusaPredmet.getDrziPredmet().getPredmet() != null) {
                dto.setPredmetNaziv(slusaPredmet.getDrziPredmet().getPredmet().getNaziv());
            }
            if (slusaPredmet.getDrziPredmet() != null && slusaPredmet.getDrziPredmet().getNastavnik() != null) {
                dto.setNastavnikIme(slusaPredmet.getDrziPredmet().getNastavnik().getIme());
            }
            spDTO.add(dto);
        }

        retVal.setSlusaPredmete(spDTO);

        List<PolozenPredmet> polozeni = polozenPredmetRepo
                .findByStudentIndeksIdAndOcenaIsNotNull(indeksId);

        Set<Long> polozeniPredmetiIds = polozeni.stream()
                .map(pp -> pp.getPredmet().getId())
                .collect(Collectors.toSet());

        List<String> nepolozeni = new ArrayList<>();
        for (SlusaPredmet sp : slusaPredmete) {
            if (sp.getDrziPredmet() != null && sp.getDrziPredmet().getPredmet() != null) {
                Long predmetId = sp.getDrziPredmet().getPredmet().getId();

                if (!polozeniPredmetiIds.contains(predmetId)) {
                    nepolozeni.add(sp.getDrziPredmet().getPredmet().getNaziv());
                }
            }
        }

        retVal.setNepolozeniPredmeti(nepolozeni);

        List<UpisGodine> upisi = upisGodineRepository
                .findByStudentIndeksStudProgramOznakaAndStudentIndeksGodinaAndStudentIndeksBroj(
                        studIndeks.getStudProgramOznaka(),
                        studIndeks.getGodina(),
                        studIndeks.getBroj()
                );

        List<UplataResponse> sveUplate = new ArrayList<>();

        for (UpisGodine upis : upisi) {
            List<Uplata> uplateZaUpis =
                    uplataRepository.findByUpisGodineId(upis.getId());

            sveUplate.addAll(
                    Converters.toUplataResponseList(uplateZaUpis)
            );
        }
        
        retVal.setUplate(sveUplate);


        return retVal;
    }

    @Transactional(readOnly = true)
    public StudentWebProfileDTO getStudentWebProfile(Long indeksId) {
        StudentIndeks studIndeks = studentIndeksRepo.findById(indeksId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Indeks ne postoji"));

        StudentWebProfileDTO retVal = new StudentWebProfileDTO();

        // aktivan indeks DTO
        StudentIndeksDTO indeksDTO = new StudentIndeksDTO();
        indeksDTO.setId(studIndeks.getId());
        indeksDTO.setBroj(studIndeks.getBroj());
        indeksDTO.setGodina(studIndeks.getGodina());
        indeksDTO.setStudProgramOznaka(studIndeks.getStudProgramOznaka());
        indeksDTO.setAktivan(studIndeks.isAktivan());
        retVal.setAktivanIndeks(indeksDTO);

        // slusa predmete
        List<SlusaPredmet> slusaPredmete = slusaPredmetRepo.getSlusaPredmetForIndeksAktivnaGodina(indeksId);

        List<SlusaPredmetDTO> spDTO = new ArrayList<>();
        for (SlusaPredmet slusaPredmet : slusaPredmete) {
            SlusaPredmetDTO dto = new SlusaPredmetDTO();
            if (slusaPredmet.getDrziPredmet() != null && slusaPredmet.getDrziPredmet().getPredmet() != null) {
                dto.setPredmetNaziv(slusaPredmet.getDrziPredmet().getPredmet().getNaziv());
            }
            if (slusaPredmet.getDrziPredmet() != null && slusaPredmet.getDrziPredmet().getNastavnik() != null) {
                dto.setNastavnikIme(slusaPredmet.getDrziPredmet().getNastavnik().getIme());
            }
            spDTO.add(dto);
        }

        retVal.setSlusaPredmete(spDTO);

        return retVal;
    }

    @Transactional
    public StudentWebProfileDTO getStudentWebProfileForEmail(@RequestParam String studEmail) {
        String[] parsedData = ParseUtils.parseEmail(studEmail);
        if(parsedData != null) {
            StudentIndeks si = studentIndeksService.findStudentIndeks(
                    parsedData[0],
                    2000 + Integer.parseInt(parsedData[1]),
                    Integer.parseInt(parsedData[2])
            );
            if(si != null)
                return getStudentWebProfile(si.getId());
        }
        return null;
    }
}