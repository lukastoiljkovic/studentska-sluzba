package org.raflab.studsluzba.utils;

import lombok.Data;

import org.raflab.studsluzba.controllers.request.*;
import org.raflab.studsluzba.controllers.response.*;
import org.raflab.studsluzba.model.entities.*;
import org.raflab.studsluzba.model.entities.IspitIzlazak;
import org.raflab.studsluzba.controllers.response.IspitniRokResponse;
import org.raflab.studsluzba.model.entities.IspitniRok;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class Converters {
    // konvertovanje izmedju entity, request i response objekata

    public static Nastavnik toNastavnik(NastavnikRequest nastavnikRequest) {
        Nastavnik nastavnik = new Nastavnik();
        nastavnik.setIme(nastavnikRequest.getIme());
        nastavnik.setPrezime(nastavnikRequest.getPrezime());
        nastavnik.setSrednjeIme(nastavnikRequest.getSrednjeIme());
        nastavnik.setEmail(nastavnikRequest.getEmail());
        nastavnik.setBrojTelefona(nastavnikRequest.getBrojTelefona());
        nastavnik.setAdresa(nastavnikRequest.getAdresa());
        nastavnik.setZvanja(nastavnikRequest.getZvanja());
        nastavnik.setDatumRodjenja(nastavnikRequest.getDatumRodjenja());
        nastavnik.setPol(nastavnikRequest.getPol());
        nastavnik.setJmbg(nastavnikRequest.getJmbg());
        return nastavnik;
    }

    public static NastavnikResponse toNastavnikResponse(Nastavnik nastavnik) {
        NastavnikResponse response = new NastavnikResponse();
        response.setId(nastavnik.getId());
        response.setIme(nastavnik.getIme());
        response.setPrezime(nastavnik.getPrezime());
        response.setSrednjeIme(nastavnik.getSrednjeIme());
        response.setEmail(nastavnik.getEmail());
        response.setBrojTelefona(nastavnik.getBrojTelefona());
        response.setAdresa(nastavnik.getAdresa());
        response.setZvanja(nastavnik.getZvanja());
        response.setDatumRodjenja(nastavnik.getDatumRodjenja());
        response.setPol(nastavnik.getPol());
        response.setJmbg(nastavnik.getJmbg());
        return response;
    }

    public static List<NastavnikResponse> toNastavnikResponseList(Iterable<Nastavnik> nastavnikIterable) {
        List<NastavnikResponse> nastavnikResponses = new ArrayList<>();

        nastavnikIterable.forEach((nastavnik) -> {
            nastavnikResponses.add(toNastavnikResponse(nastavnik));
        });
        return nastavnikResponses;
    }

    public static StudentPodaci toStudentPodaci(StudentPodaciRequest request) {
        StudentPodaci studentPodaci = new StudentPodaci();

        studentPodaci.setIme(request.getIme());
        studentPodaci.setPrezime(request.getPrezime());
        studentPodaci.setSrednjeIme(request.getSrednjeIme());

        studentPodaci.setJmbg(request.getJmbg());
        studentPodaci.setPol(request.getPol());

        studentPodaci.setDatumRodjenja(request.getDatumRodjenja());
        studentPodaci.setDrzavaRodjenja(request.getDrzavaRodjenja());
        studentPodaci.setMestoRodjenja(request.getMestoRodjenja());

        studentPodaci.setDrzavljanstvo(request.getDrzavljanstvo());
        studentPodaci.setNacionalnost(request.getNacionalnost());

        studentPodaci.setMestoPrebivalista(request.getMestoPrebivalista());
        studentPodaci.setAdresaPrebivalista(request.getAdresaPrebivalista());

        studentPodaci.setBrojTelefonaMobilni(request.getBrojTelefonaMobilni());
        studentPodaci.setBrojTelefonaFiksni(request.getBrojTelefonaFiksni());

        studentPodaci.setEmailFakultetski(request.getEmailFakultetski());
        studentPodaci.setEmailPrivatni(request.getEmailPrivatni());

        studentPodaci.setBrojLicneKarte(request.getBrojLicneKarte());
        studentPodaci.setLicnuKartuIzdao(request.getLicnuKartuIzdao());

        studentPodaci.setUspehSrednjaSkola(request.getUspehSrednjaSkola());
        studentPodaci.setUspehPrijemni(request.getUspehPrijemni());

        return studentPodaci;
    }


    public static StudentIndeks toStudentIndeks(StudentIndeksRequest studentIndeksRequest) {
        StudentIndeks studentIndeks = new StudentIndeks();
        studentIndeks.setGodina(studentIndeksRequest.getGodina());
        studentIndeks.setStudProgramOznaka(studentIndeksRequest.getStudProgramOznaka());
        studentIndeks.setNacinFinansiranja(studentIndeksRequest.getNacinFinansiranja());
        studentIndeks.setAktivan(studentIndeksRequest.isAktivan());
        studentIndeks.setVaziOd(studentIndeksRequest.getVaziOd());
        return studentIndeks;
    }

    public static StudentPodaciResponse toStudentPodaciResponse(StudentPodaci student) {
        if (student == null) return null;
        StudentPodaciResponse response = new StudentPodaciResponse();
        response.setId(student.getId());
        response.setIme(student.getIme());
        response.setPrezime(student.getPrezime());
        response.setSrednjeIme(student.getSrednjeIme());
        response.setJmbg(student.getJmbg());
        response.setPol(student.getPol());
        response.setDatumRodjenja(student.getDatumRodjenja());
        response.setDrzavaRodjenja(student.getDrzavaRodjenja());
        response.setMestoRodjenja(student.getMestoRodjenja());
        response.setDrzavljanstvo(student.getDrzavljanstvo());
        response.setNacionalnost(student.getNacionalnost());
        response.setMestoPrebivalista(student.getMestoPrebivalista());
        response.setAdresaPrebivalista(student.getAdresaPrebivalista());
        response.setBrojTelefonaMobilni(student.getBrojTelefonaMobilni());
        response.setBrojTelefonaFiksni(student.getBrojTelefonaFiksni());
        response.setEmailFakultetski(student.getEmailFakultetski());
        response.setEmailPrivatni(student.getEmailPrivatni());
        response.setBrojLicneKarte(student.getBrojLicneKarte());
        response.setLicnuKartuIzdao(student.getLicnuKartuIzdao());
        response.setUspehSrednjaSkola(student.getUspehSrednjaSkola());
        response.setUspehPrijemni(student.getUspehPrijemni());
        return response;
    }

    public static StudijskiProgramResponse toStudijskiProgramResponse(StudijskiProgram sp) {
        if (sp == null) return null;
        StudijskiProgramResponse response = new StudijskiProgramResponse();
        response.setId(sp.getId());
        response.setOznaka(sp.getOznaka());
        response.setNaziv(sp.getNaziv());
        response.setGodinaAkreditacije(sp.getGodinaAkreditacije());
        response.setZvanje(sp.getZvanje());
        response.setTrajanjeGodina(sp.getTrajanjeGodina());
        response.setTrajanjeSemestara(sp.getTrajanjeSemestara());
        response.setVrstaStudija(sp.getVrstaStudija());
        response.setUkupnoEspb(sp.getUkupnoEspb());
        return response;
    }

    public static StudentIndeksResponse toStudentIndeksResponse(StudentIndeks si) {
        if (si == null) return null;
        StudentIndeksResponse response = new StudentIndeksResponse();
        response.setId(si.getId());
        response.setBroj(si.getBroj());
        response.setGodina(si.getGodina());
        response.setStudProgramOznaka(si.getStudProgramOznaka());
        response.setNacinFinansiranja(si.getNacinFinansiranja());
        response.setAktivan(si.isAktivan());
        response.setVaziOd(si.getVaziOd());
        response.setOstvarenoEspb(si.getOstvarenoEspb());
        response.setStudent(toStudentPodaciResponse(si.getStudent()));
        response.setStudijskiProgram(toStudijskiProgramResponse(si.getStudijskiProgram()));
        return response;
    }

    // --------- GRUPA ---------

    public static Grupa toGrupa(GrupaRequest req, StudijskiProgram sp, SkolskaGodina sg) {
        Grupa g = new Grupa();
        g.setNaziv(req.getNaziv());
        g.setStudijskiProgram(sp);
        g.setSkolskaGodina(sg);
        return g;
    }

    public static GrupaResponse toGrupaResponse(Grupa grupa) {
        if (grupa == null) return null;
        GrupaResponse res = new GrupaResponse();
        res.setId(grupa.getId());
        res.setNaziv(grupa.getNaziv());

        if (grupa.getStudijskiProgram() != null)
            res.setStudijskiProgramNaziv(grupa.getStudijskiProgram().getNaziv());

        if (grupa.getSkolskaGodina() != null)
            res.setSkolskaGodinaNaziv(grupa.getSkolskaGodina().getNaziv());

        return res;
    }

    public static List<GrupaResponse> toGrupaResponseList(Iterable<Grupa> grupe) {
        List<GrupaResponse> lista = new ArrayList<>();
        grupe.forEach(g -> lista.add(toGrupaResponse(g)));
        return lista;
    }

    // ---------- ISPIT IZLAZAK ----------

    public static IspitIzlazakResponse toIspitIzlazakResponse(IspitIzlazak e) {
        if (e == null) return null;

        IspitIzlazakResponse r = new IspitIzlazakResponse();
        r.setId(e.getId());
        r.setBrojPoena(e.getBrojPoena());
        r.setNapomena(e.getNapomena());
        r.setPonistava(e.isPonistava());

        if (e.getStudentIndeks() != null) {
            r.setStudentIndeksId(e.getStudentIndeks().getId());
            r.setIndeksBroj(e.getStudentIndeks().getBroj());
            r.setIndeksGodina(e.getStudentIndeks().getGodina());
            r.setStudProgramOznaka(e.getStudentIndeks().getStudProgramOznaka());
        }

        if (e.getIspitPrijava() != null) {
            r.setIspitPrijavaId(e.getIspitPrijava().getId());
            r.setDatumPrijave(e.getIspitPrijava().getDatum());

            if (e.getIspitPrijava().getIspit() != null) {
                r.setIspitId(e.getIspitPrijava().getIspit().getId());
                r.setDatumIspita(e.getIspitPrijava().getIspit().getDatumVremePocetka());
                if (e.getIspitPrijava().getIspit().getPredmet() != null) {
                    r.setPredmetSifra(e.getIspitPrijava().getIspit().getPredmet().getSifra());
                    r.setPredmetNaziv(e.getIspitPrijava().getIspit().getPredmet().getNaziv());
                }
            }
        }
        return r;
    }

    public static IspitniRokResponse toIspitniRokResponse(IspitniRok ir) {
        if (ir == null) return null;
        IspitniRokResponse r = new IspitniRokResponse();
        r.setId(ir.getId());
        r.setNaziv(ir.getNaziv());
        r.setDatumPocetka(ir.getDatumPocetka());
        r.setDatumZavrsetka(ir.getDatumZavrsetka());
        if (ir.getSkolskaGodina() != null) {
            r.setSkolskaGodinaId(ir.getSkolskaGodina().getId());
            r.setSkolskaGodinaNaziv(ir.getSkolskaGodina().getNaziv());
        }
        return r;
    }

    public static java.util.List<IspitniRokResponse> toIspitniRokResponseList(Iterable<IspitniRok> items) {
        java.util.List<IspitniRokResponse> list = new java.util.ArrayList<>();
        items.forEach(x -> list.add(toIspitniRokResponse(x)));
        return list;
    }

    public static List<IspitIzlazakResponse> toIspitIzlazakResponseList(Iterable<IspitIzlazak> list) {
        List<IspitIzlazakResponse> out = new ArrayList<>();
        list.forEach(x -> out.add(toIspitIzlazakResponse(x)));
        return out;
    }

    public static PredispitnaIzlazak toPredispitnaIzlazak(
            PredispitnaIzlazakRequest req,
            SlusaPredmet sp,
            PredispitnaObaveza po) {

        PredispitnaIzlazak izlazak = new PredispitnaIzlazak();
        izlazak.setSlusaPredmet(sp);
        izlazak.setPredispitnaObaveza(po);
        izlazak.setPoeni(req.getPoeni());
        izlazak.setDatum(req.getDatum());
        return izlazak;
    }

    public static PredispitnaIzlazakResponse toPredispitnaIzlazakResponse(PredispitnaIzlazak izlazak) {
        PredispitnaIzlazakResponse res = new PredispitnaIzlazakResponse();
        res.setId(izlazak.getId());
        res.setSlusaPredmetId(izlazak.getSlusaPredmet() != null ? izlazak.getSlusaPredmet().getId() : null);
        res.setPredispitnaObavezaId(izlazak.getPredispitnaObaveza() != null ? izlazak.getPredispitnaObaveza().getId() : null);
        res.setPoeni(izlazak.getPoeni());
        res.setDatum(izlazak.getDatum());
        return res;
    }

    public static List<PredispitnaIzlazakResponse> toPredispitnaIzlazakResponseList(List<PredispitnaIzlazak> lista) {
        return lista.stream()
                .map(Converters::toPredispitnaIzlazakResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public static PolozenPredmet toPolozenPredmet(PolozenPredmetRequest req,
                                                  StudentIndeks si,
                                                  Predmet p,
                                                  IspitIzlazak izlazak) {
        PolozenPredmet pp = new PolozenPredmet();
        pp.setOcena(req.getOcena());
        pp.setPriznat(req.isPriznat());
        pp.setStudentIndeks(si);
        pp.setPredmet(p);
        pp.setIspitIzlazak(izlazak);
        return pp;
    }

    public static PolozenPredmetResponse toPolozenPredmetResponse(PolozenPredmet pp) {
        PolozenPredmetResponse r = new PolozenPredmetResponse();
        r.setId(pp.getId());
        r.setOcena(pp.getOcena());
        r.setPriznat(pp.isPriznat());

        if(pp.getStudentIndeks() != null) r.setStudentIndeksId(pp.getStudentIndeks().getId());
        if(pp.getPredmet() != null) r.setPredmetId(pp.getPredmet().getId());
        if(pp.getIspitIzlazak() != null) r.setIspitIzlazakId(pp.getIspitIzlazak().getId());

        return r;
    }

    public static List<PolozenPredmetResponse> toPolozenPredmetResponseList(List<PolozenPredmet> lista) {
        return lista.stream()
                .map(Converters::toPolozenPredmetResponse)
                .collect(Collectors.toList());
    }

    public static ObnovaGodine toObnova(ObnovaGodineRequest req,
                                        StudentIndeks si,
                                        SkolskaGodina sg,
                                        Set<SlusaPredmet> predmeti) {

        ObnovaGodine o = new ObnovaGodine();
        o.setGodinaStudija(req.getGodinaStudija());
        o.setDatum(req.getDatum());
        o.setNapomena(req.getNapomena());
        o.setStudentIndeks(si);
        o.setSkolskaGodina(sg);
        o.setPredmetiKojeObnavlja(predmeti);
        return o;
    }

    public static ObnovaGodineResponse toObnovaResponse(ObnovaGodine og) {

        ObnovaGodineResponse r = new ObnovaGodineResponse();

        r.setId(og.getId());
        r.setGodinaStudija(og.getGodinaStudija());
        r.setDatum(og.getDatum());
        r.setNapomena(og.getNapomena());

        if (og.getStudentIndeks() != null)
            r.setStudentIndeksId(og.getStudentIndeks().getId());

        if (og.getSkolskaGodina() != null)
            r.setSkolskaGodinaId(og.getSkolskaGodina().getId());

        if (og.getPredmetiKojeObnavlja() != null)
            r.setPredmetiKojeObnavljaIds(
                    og.getPredmetiKojeObnavlja().stream()
                            .map(SlusaPredmet::getId)
                            .collect(Collectors.toSet())
            );

        return r;
    }

    public static List<ObnovaGodineResponse> toObnovaResponseList(List<ObnovaGodine> lista) {
        return lista.stream()
                .map(Converters::toObnovaResponse)
                .collect(Collectors.toList());
    }


}
