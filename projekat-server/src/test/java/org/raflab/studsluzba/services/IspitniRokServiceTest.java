package org.raflab.studsluzba.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.raflab.studsluzba.dtos.IspitniRokRequest;
import org.raflab.studsluzba.model.entities.*;
import org.raflab.studsluzba.repositories.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IspitniRokServiceTest {

    @Mock IspitniRokRepository ispitniRokRepository;
    @Mock SkolskaGodinaRepository skolskaGodinaRepository;
    @Mock IspitRepository ispitRepository;
    @Mock PolozenPredmetRepository polozenPredmetRepository;
    @Mock IspitPrijavaRepository ispitPrijavaRepository;
    @Mock IspitIzlazakRepository ispitIzlazakRepository;

    @InjectMocks IspitniRokService service;

    @Captor ArgumentCaptor<IspitniRok> rokCaptor;

    // ---------------- helperi ----------------

    private static SkolskaGodina skGod(Long id, String naziv) {
        SkolskaGodina sg = new SkolskaGodina();
        sg.setId(id);
        sg.setNaziv(naziv);
        return sg;
    }

    private static IspitniRok rok(Long id, SkolskaGodina sg) {
        IspitniRok r = new IspitniRok();
        r.setId(id);
        r.setNaziv("Januar");
        r.setDatumPocetka(LocalDateTime.of(2026, 1, 10, 0, 0));
        r.setDatumZavrsetka(LocalDateTime.of(2026, 1, 20, 0, 0));
        r.setSkolskaGodina(sg);
        return r;
    }

    private static Predmet predmet(Long id, String naziv) {
        Predmet p = new Predmet();
        p.setId(id);
        p.setNaziv(naziv);
        p.setSifra("P-" + id);
        p.setEspb(6);
        p.setSemestar(1);
        return p;
    }

    private static Nastavnik nastavnik(Long id) {
        Nastavnik n = new Nastavnik();
        n.setId(id);
        n.setEmail("n" + id + "@raf.rs");
        n.setIme("N");
        n.setPrezime("N");
        return n;
    }

    private static Ispit ispit(Long id, IspitniRok rok) {
        Ispit i = new Ispit();
        i.setId(id);
        i.setIspitniRok(rok);
        i.setDatumVremePocetka(LocalDateTime.of(2026, 1, 12, 10, 0));
        i.setZakljucen(false);
        i.setPredmet(predmet(100L + id, "Predmet" + id));
        i.setNastavnik(nastavnik(200L + id));
        return i;
    }

    private static StudentPodaci student(Long id) {
        StudentPodaci s = new StudentPodaci();
        s.setId(id);
        s.setIme("Ime" + id);
        s.setPrezime("Prez" + id);
        s.setSrednjeIme("X");
        s.setPol('M');
        s.setDatumRodjenja(LocalDate.of(2000, 1, 1));
        s.setDrzavaRodjenja("SRB");
        s.setMestoRodjenja("BG");
        s.setDrzavljanstvo("SRB");
        s.setMestoPrebivalista("BG");
        s.setAdresaPrebivalista("Ulica 1");
        s.setEmailFakultetski("f" + id + "@raf.rs");
        s.setEmailPrivatni("p" + id + "@mail.com");
        s.setBrojLicneKarte("123");
        s.setLicnuKartuIzdao("MUP");
        return s;
    }

    private static StudentIndeks indeks(Long id, StudentPodaci st) {
        StudentIndeks si = new StudentIndeks();
        si.setId(id);
        si.setGodina(2025);
        si.setBroj(1);
        si.setStudProgramOznaka("RI");
        si.setStudent(st);
        return si;
    }

    private static IspitPrijava prijava(Long id, StudentIndeks si, Ispit i) {
        IspitPrijava ip = new IspitPrijava();
        ip.setId(id);
        ip.setDatum(LocalDate.of(2026, 1, 5));
        ip.setStudentIndeks(si);
        ip.setIspit(i);
        return ip;
    }

    private static IspitIzlazak izlazak(Long id, IspitPrijava ip) {
        IspitIzlazak ie = new IspitIzlazak();
        ie.setId(id);
        ie.setIspitPrijava(ip);
        ie.setStudentIndeks(ip.getStudentIndeks());
        ie.setBrojPoena(40);
        ie.setNapomena("x");
        ie.setPonistava(false);
        return ie;
    }

    private static PolozenPredmet polozen(Long id, StudentIndeks si, Predmet p, IspitIzlazak ie) {
        PolozenPredmet pp = new PolozenPredmet();
        pp.setId(id);
        pp.setStudentIndeks(si);
        pp.setPredmet(p);
        pp.setIspitIzlazak(ie);
        pp.setOcena(6);
        pp.setPriznat(false);
        return pp;
    }

    private static IspitniRokRequest req(Long skGodId) {
        IspitniRokRequest r = new IspitniRokRequest();
        r.setNaziv("Januar");
        r.setDatumPocetka(LocalDateTime.of(2026, 1, 10, 0, 0));
        r.setDatumZavrsetka(LocalDateTime.of(2026, 1, 20, 0, 0));
        r.setSkolskaGodinaId(skGodId);
        return r;
    }

    // ---------------- add ----------------

    @Nested
    class AddTests {

        @Test
        @DisplayName("Kad skolska godina postoji -> setuje je i snima rok")
        void add_setsSkolskaGodina() {
            SkolskaGodina sg = skGod(1L, "2025/2026.");
            when(skolskaGodinaRepository.findById(1L)).thenReturn(Optional.of(sg));

            IspitniRok saved = new IspitniRok();
            saved.setId(99L);
            when(ispitniRokRepository.save(any(IspitniRok.class))).thenReturn(saved);

            Long outId = service.add(req(1L));
            assertEquals(99L, outId);

            verify(ispitniRokRepository).save(rokCaptor.capture());
            IspitniRok toSave = rokCaptor.getValue();

            assertEquals("Januar", toSave.getNaziv());
            assertEquals(LocalDateTime.of(2026, 1, 10, 0, 0), toSave.getDatumPocetka());
            assertEquals(LocalDateTime.of(2026, 1, 20, 0, 0), toSave.getDatumZavrsetka());
            assertEquals(sg, toSave.getSkolskaGodina());
        }

        @Test
        @DisplayName("Kad skolska godina ne postoji -> skolskaGodina je null, ali snima rok")
        void add_skolskaGodinaMissing_setsNull() {
            when(skolskaGodinaRepository.findById(777L)).thenReturn(Optional.empty());

            IspitniRok saved = new IspitniRok();
            saved.setId(5L);
            when(ispitniRokRepository.save(any(IspitniRok.class))).thenReturn(saved);

            Long outId = service.add(req(777L));
            assertEquals(5L, outId);

            verify(ispitniRokRepository).save(rokCaptor.capture());
            assertNull(rokCaptor.getValue().getSkolskaGodina());
        }
    }

    // ---------------- findById / findAll ----------------

    @Test
    @DisplayName("findById delegira na repo")
    void findById_delegates() {
        IspitniRok r = new IspitniRok();
        r.setId(1L);
        when(ispitniRokRepository.findById(1L)).thenReturn(Optional.of(r));

        Optional<IspitniRok> out = service.findById(1L);

        assertTrue(out.isPresent());
        assertEquals(1L, out.get().getId());
        verify(ispitniRokRepository).findById(1L);
    }

    @Test
    @DisplayName("findAll delegira na repo")
    void findAll_delegates() {
        IspitniRok r1 = new IspitniRok(); r1.setId(1L);
        IspitniRok r2 = new IspitniRok(); r2.setId(2L);

        when(ispitniRokRepository.findAll()).thenReturn(List.of(r1, r2));

        List<IspitniRok> out = service.findAll();

        assertEquals(2, out.size());
        assertEquals(1L, out.get(0).getId());
        assertEquals(2L, out.get(1).getId());
        verify(ispitniRokRepository).findAll();
    }

    // ---------------- deleteById ----------------

    @Nested
    class DeleteByIdTests {

        @Test
        @DisplayName("Ako rok ne postoji -> 404")
        void delete_notFound_throws404() {
            when(ispitniRokRepository.existsById(10L)).thenReturn(false);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.deleteById(10L));

            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
            verify(ispitniRokRepository, never()).deleteById(anyLong());
            verifyNoInteractions(ispitRepository, ispitPrijavaRepository, ispitIzlazakRepository, polozenPredmetRepository);
        }

        @Test
        @DisplayName("Kad nema ispita u roku -> obriše samo rok")
        void delete_noExams_deletesOnlyRok() {
            when(ispitniRokRepository.existsById(1L)).thenReturn(true);

            // findAll() nema ispita sa ispitniRokId=1
            IspitniRok otherRok = rok(99L, skGod(1L, "2025/2026."));
            when(ispitRepository.findAll()).thenReturn(List.of(
                    ispit(10L, otherRok),
                    ispit(11L, otherRok)
            ));

            service.deleteById(1L);

            verify(ispitniRokRepository).deleteById(1L);
            verify(ispitRepository, never()).deleteById(anyLong());
            verify(ispitPrijavaRepository, never()).deleteById(anyLong());
            verify(ispitIzlazakRepository, never()).deleteById(anyLong());
            verify(polozenPredmetRepository, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("Happy path: kaskadno obriše ispite/prijave/položene/izlazak pa rok")
        void delete_cascadesEverything() {
            when(ispitniRokRepository.existsById(1L)).thenReturn(true);

            SkolskaGodina sg = skGod(1L, "2025/2026.");
            IspitniRok targetRok = rok(1L, sg);

            // u repo findAll vrati 2 ispita u target roku + 1 van
            Ispit i1 = ispit(10L, targetRok);
            Ispit i2 = ispit(11L, targetRok);
            Ispit other = ispit(99L, rok(2L, sg));

            when(ispitRepository.findAll()).thenReturn(List.of(i1, i2, other));

            // za i1: jedna prijava sa izlaskom + jedan položen predmet povezan sa tim izlaskom
            StudentPodaci st = student(1L);
            StudentIndeks si = indeks(100L, st);

            IspitPrijava ip1 = prijava(201L, si, i1);
            IspitIzlazak ie1 = izlazak(301L, ip1);
            ip1.setIspitIzlazak(ie1);

            when(ispitPrijavaRepository.findAllByIspitId(10L)).thenReturn(List.of(ip1));

            PolozenPredmet ppMatch = polozen(401L, si, i1.getPredmet(), ie1);
            PolozenPredmet ppOther = polozen(999L, si, i2.getPredmet(), izlazak(9999L, prijava(9998L, si, i2)));

            when(polozenPredmetRepository.findAll()).thenReturn(List.of(ppMatch, ppOther));

            // za i2: jedna prijava bez izlaska
            IspitPrijava ip2 = prijava(202L, si, i2);
            ip2.setIspitIzlazak(null);
            when(ispitPrijavaRepository.findAllByIspitId(11L)).thenReturn(List.of(ip2));

            service.deleteById(1L);

            // i1 kaskada
            verify(polozenPredmetRepository).deleteById(401L);
            verify(ispitIzlazakRepository).deleteById(301L);
            verify(ispitPrijavaRepository).deleteById(201L);
            verify(ispitRepository).deleteById(10L);

            // i2 kaskada
            verify(ispitPrijavaRepository).deleteById(202L);
            verify(ispitRepository).deleteById(11L);

            // rok
            verify(ispitniRokRepository).deleteById(1L);

            // ne smije dirati other ispit
            verify(ispitRepository, never()).deleteById(99L);
        }

        @Test
        @DisplayName("Ako DataIntegrityViolation -> 409")
        void delete_conflict_throws409() {
            when(ispitniRokRepository.existsById(1L)).thenReturn(true);

            // nema ispita u roku, ali deleteById rok baci DIVE
            when(ispitRepository.findAll()).thenReturn(Collections.emptyList());
            doThrow(new DataIntegrityViolationException("fk"))
                    .when(ispitniRokRepository).deleteById(1L);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.deleteById(1L));

            assertEquals(HttpStatus.CONFLICT, ex.getStatus());
            assertNotNull(ex.getReason());
        }
    }
}
