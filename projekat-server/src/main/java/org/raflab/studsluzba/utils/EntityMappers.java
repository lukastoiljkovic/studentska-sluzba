package org.raflab.studsluzba.utils;

import org.raflab.studsluzba.controllers.response.StudentIndeksResponse;
import org.raflab.studsluzba.controllers.response.StudentPodaciResponse;
import org.raflab.studsluzba.model.entities.StudentIndeks;
import org.raflab.studsluzba.model.entities.StudentPodaci;
import org.raflab.studsluzba.model.dtos.StudentDTO;
import org.springframework.stereotype.Component;

@Component
public class EntityMappers {
    // mapiranje iz entity objekata u DTOs

    public static StudentDTO fromStudentPodaciToDTO(StudentPodaci sp) {
        StudentDTO s = new StudentDTO();
        s.setIdStudentPodaci(sp.getId());
        s.setIme(sp.getIme());
        s.setPrezime(sp.getPrezime());
        return s;

    }

    public static StudentDTO fromStudentIndeksToDTO(StudentIndeks si) {
        StudentDTO s = fromStudentPodaciToDTO(si.getStudent());
        s.setIdIndeks(si.getId());
        s.setGodinaUpisa(si.getGodina());
        s.setBroj(si.getBroj());
        s.setStudProgramOznaka(si.getStudProgramOznaka());
        s.setAktivanIndeks(si.isAktivan());
        return s;

    }

    public StudentIndeksResponse fromStudentIndexToResponse(StudentIndeks si) {
        if (si == null) {
            return null;
        }
        StudentIndeksResponse response = new StudentIndeksResponse();
        response.setId(si.getId());
        response.setBroj(si.getBroj());
        response.setGodina(si.getGodina());
        response.setStudProgramOznaka(si.getStudProgramOznaka());
        response.setNacinFinansiranja(si.getNacinFinansiranja());
        response.setAktivan(si.isAktivan());
        response.setVaziOd(si.getVaziOd());
        response.setOstvarenoEspb(si.getOstvarenoEspb());
        response.setStudent(Converters.toStudentPodaciResponse(si.getStudent()));
        response.setStudijskiProgram(Converters.toStudijskiProgramResponse(si.getStudijskiProgram()));
        return response;
    }

    public StudentPodaciResponse fromStudentPodaciToResponse(StudentPodaci student) {
        if (student == null) return null;

        StudentPodaciResponse resp = new StudentPodaciResponse();
        resp.setId(student.getId());
        resp.setIme(student.getIme());
        resp.setPrezime(student.getPrezime());
        resp.setSrednjeIme(student.getSrednjeIme());
        resp.setJmbg(student.getJmbg());
        resp.setPol(student.getPol());
        resp.setDatumRodjenja(student.getDatumRodjenja());
        resp.setDrzavaRodjenja(student.getDrzavaRodjenja());
        resp.setMestoRodjenja(student.getMestoRodjenja());
        resp.setDrzavljanstvo(student.getDrzavljanstvo());
        resp.setNacionalnost(student.getNacionalnost());
        resp.setMestoPrebivalista(student.getMestoPrebivalista());
        resp.setAdresaPrebivalista(student.getAdresaPrebivalista());
        resp.setBrojTelefonaMobilni(student.getBrojTelefonaMobilni());
        resp.setBrojTelefonaFiksni(student.getBrojTelefonaFiksni());
        resp.setEmailFakultetski(student.getEmailFakultetski());
        resp.setEmailPrivatni(student.getEmailPrivatni());
        resp.setBrojLicneKarte(student.getBrojLicneKarte());
        resp.setLicnuKartuIzdao(student.getLicnuKartuIzdao());
        resp.setUspehSrednjaSkola(student.getUspehSrednjaSkola());
        resp.setUspehPrijemni(student.getUspehPrijemni());

        // dodaj nazive entiteta
        resp.setSrednjaSkola(student.getSrednjaSkola() != null ? student.getSrednjaSkola().getNaziv() : null);
        resp.setPrethodnaUstanova(student.getPrethodnaUstanova() != null ? student.getPrethodnaUstanova().getNaziv() : null);

        return resp;
    }

}

