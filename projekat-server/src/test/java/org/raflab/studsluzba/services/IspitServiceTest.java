package org.raflab.studsluzba.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzba.model.entities.*;
import org.raflab.studsluzba.repositories.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IspitServiceTest {

    @Mock IspitRepository ispitRepository;
    @Mock IspitniRokRepository ispitniRokRepository;
    @Mock NastavnikRepository nastavnikRepository;
    @Mock PredmetRepository predmetRepository;
    @Mock IspitPrijavaRepository ispitPrijavaRepository;
    @Mock PolozenPredmetRepository polozenPredmetRepository;
    @Mock StudentIndeksRepository studentIndeksRepository;
    @Mock SlusaPredmetRepository slusaPredmetRepository;
    @Mock PredispitnaIzlazakRepository predispRepo;
    @Mock IspitIzlazakRepository ispitIzlazakRepository;

    @InjectMocks IspitService service;

    @Captor ArgumentCaptor<Ispit> ispitCaptor;
    @Captor ArgumentCaptor<IspitPrijava> prijavaCaptor;
    @Captor ArgumentCaptor<IspitIzlazak> izlazakCaptor;
    @Captor ArgumentCaptor<PolozenPredmet> polozenCaptor;

    // -------------------- helperi --------------------

    private static SkolskaGodina skGod(Long id) {
        SkolskaGodina sg = new SkolskaGodina();
        sg.setId(id);
        sg.setNaziv("2025/26");
        return sg;
    }

    private static IspitniRok rok(Long id, SkolskaGodina sg) {
        IspitniRok r = new IspitniRok();
        r.setId(id);
        r.setNaziv("Januar");
        r.setDatumPocetka(LocalDateTime.of(2026, 1, 10, 0, 0));
        r.setDatumZavrsetka(LocalDateTime.of(2026, 1, 25, 0, 0));
        r.setSkolskaGodina(sg);
        return r;
    }

    private static Predmet predmet(Long id, String naziv) {
        Predmet p = new Predmet();
        p.setId(id);
        p.setNaziv(naziv);
        p.setSifra("P-" + id);
        return p;
    }

    private static Nastavnik nastavnik(Long id) {
        Nastavnik n = new Nastavnik();
        n.setId(id);
        n.setIme("Petar");
        n.setPrezime("Petrovic");
        n.setEmail("p@raf.rs");
        return n;
    }

    private static StudentPodaci student(Long id, String ime, String prezime) {
        StudentPodaci s = new StudentPodaci();
        s.setId(id);
        s.setIme(ime);
        s.setPrezime(prezime);
        s.setSrednjeIme("X");
        s.setEmailFakultetski(ime.toLowerCase() + "@raf.rs");
        s.setEmailPrivatni(ime.toLowerCase() + "@gmail.com");
        s.setPol('M');
        s.setDatumRodjenja(LocalDate.of(2000, 1, 1));
        s.setDrzavaRodjenja("RS");
        s.setMestoRodjenja("BG");
        s.setDrzavljanstvo("RS");
        s.setMestoPrebivalista("BG");
        s.setAdresaPrebivalista("Neka 1");
        s.setBrojLicneKarte("123");
        s.setLicnuKartuIzdao("MUP");
        return s;
    }

    private static StudentIndeks indeks(Long id, String oznaka, int godinaUpisa, int broj, StudentPodaci st) {
        StudentIndeks si = new StudentIndeks();
        si.setId(id);
        si.setStudProgramOznaka(oznaka);
        si.setGodina(godinaUpisa);
        si.setBroj(broj);
        si.setStudent(st);
        return si;
    }

    private static Ispit ispit(Long id, Predmet p, Nastavnik n, IspitniRok r, boolean zakljucen) {
        Ispit i = new Ispit();
        i.setId(id);
        i.setPredmet(p);
        i.setNastavnik(n);
        i.setIspitniRok(r);
        i.setZakljucen(zakljucen);
        i.setDatumVremePocetka(LocalDateTime.of(2026, 1, 15, 10, 0));
        return i;
    }

    private static IspitPrijava prijava(Long id, StudentIndeks si, Ispit ispit) {
        IspitPrijava ip = new IspitPrijava();
        ip.setId(id);
        ip.setStudentIndeks(si);
        ip.setIspit(ispit);
        ip.setDatum(LocalDate.of(2026, 1, 1));
        return ip;
    }

    // -------------------- add --------------------

    @Test
    @DisplayName("add() mapira request u Ispit i cuva")
    void add_savesAndReturnsId() {
        SkolskaGodina sg = skGod(1L);
        IspitniRok r = rok(10L, sg);
        Nastavnik n = nastavnik(20L);
        Predmet p = predmet(30L, "Mat1");

        IspitRequest req = new IspitRequest();
        req.setIspitniRokId(10L);
        req.setNastavnikId(20L);
        req.setPredmetId(30L);
        req.setZakljucen(false);
        req.setDatumVremePocetka(LocalDateTime.of(2026, 1, 20, 12, 0));

        when(ispitniRokRepository.findById(10L)).thenReturn(Optional.of(r));
        when(nastavnikRepository.findById(20L)).thenReturn(Optional.of(n));
        when(predmetRepository.findById(30L)).thenReturn(Optional.of(p));

        Ispit saved = new Ispit();
        saved.setId(999L);
        when(ispitRepository.save(any(Ispit.class))).thenReturn(saved);

        Long outId = service.add(req);
        assertEquals(999L, outId);

        verify(ispitRepository).save(ispitCaptor.capture());
        Ispit toSave = ispitCaptor.getValue();

        assertEquals(r, toSave.getIspitniRok());
        assertEquals(n, toSave.getNastavnik());
        assertEquals(p, toSave.getPredmet());
        assertEquals(req.isZakljucen(), toSave.isZakljucen());
        assertEquals(req.getDatumVremePocetka(), toSave.getDatumVremePocetka());
    }

    // -------------------- getPrijavljeniStudentiZaIspit --------------------

    @Nested
    class PrijavljeniStudenti {

        @Test
        @DisplayName("Ispit ne postoji -> 404")
        void notFound_404() {
            when(ispitRepository.existsById(1L)).thenReturn(false);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.getPrijavljeniStudentiZaIspit(1L));

            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
            verify(ispitPrijavaRepository, never()).findAllByIspitId(anyLong());
        }

        @Test
        @DisplayName("Postoji -> delegira na repo i vraca listu response-a")
        void returnsResponses() {
            when(ispitRepository.existsById(2L)).thenReturn(true);

            SkolskaGodina sg = skGod(1L);
            IspitniRok r = rok(10L, sg);
            Predmet p = predmet(30L, "Mat1");
            Nastavnik n = nastavnik(20L);
            Ispit i = ispit(2L, p, n, r, false);

            StudentPodaci s1 = student(101L, "Ana", "A");
            StudentIndeks si1 = indeks(201L, "RI", 2025, 1, s1);

            IspitPrijava ip1 = prijava(301L, si1, i);

            when(ispitPrijavaRepository.findAllByIspitId(2L)).thenReturn(List.of(ip1));

            List<IspitPrijavaResponse> out = service.getPrijavljeniStudentiZaIspit(2L);
            assertNotNull(out);
            assertEquals(1, out.size());
            assertEquals(301L, out.get(0).getId());
        }
    }

    // -------------------- getProsecnaOcenaNaIspitu --------------------

    @Nested
    class ProsecnaOcenaNaIspitu {

        @Test
        @DisplayName("Ispit ne postoji -> 404")
        void notFound_404() {
            when(ispitRepository.existsById(1L)).thenReturn(false);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.getProsecnaOcenaNaIspitu(1L));

            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        }

        @Test
        @DisplayName("Repo vrati null -> servis vraca 0.0")
        void nullAvg_returnsZero() {
            when(ispitRepository.existsById(2L)).thenReturn(true);
            when(polozenPredmetRepository.avgOcenaZaIspit(2L)).thenReturn(null);

            Double out = service.getProsecnaOcenaNaIspitu(2L);
            assertEquals(0.0, out);
        }

        @Test
        @DisplayName("Repo vrati vrednost -> servis vraca tu vrednost")
        void returnsAvg() {
            when(ispitRepository.existsById(3L)).thenReturn(true);
            when(polozenPredmetRepository.avgOcenaZaIspit(3L)).thenReturn(8.25);

            Double out = service.getProsecnaOcenaNaIspitu(3L);
            assertEquals(8.25, out);
        }
    }

    // -------------------- prijaviIspit --------------------

    @Nested
    class PrijaviIspit {

        @Test
        @DisplayName("Nedostaju obavezna polja -> 400")
        void missingRequired_400() {
            IspitPrijavaRequest req = new IspitPrijavaRequest();
            req.setStudentIndeksId(null);
            req.setIspitId(1L);
            req.setDatum(LocalDate.now());

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.prijaviIspit(req));

            assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        }

        @Test
        @DisplayName("StudentIndeks ne postoji -> 404")
        void studentNotFound_404() {
            IspitPrijavaRequest req = new IspitPrijavaRequest();
            req.setStudentIndeksId(10L);
            req.setIspitId(20L);
            req.setDatum(LocalDate.now());

            when(studentIndeksRepository.findById(10L)).thenReturn(Optional.empty());

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.prijaviIspit(req));

            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        }

        @Test
        @DisplayName("Ispit ne postoji -> 404")
        void ispitNotFound_404() {
            IspitPrijavaRequest req = new IspitPrijavaRequest();
            req.setStudentIndeksId(10L);
            req.setIspitId(20L);
            req.setDatum(LocalDate.now());

            StudentPodaci s = student(1L, "A", "B");
            StudentIndeks si = indeks(10L, "RI", 2025, 1, s);

            when(studentIndeksRepository.findById(10L)).thenReturn(Optional.of(si));
            when(ispitRepository.findById(20L)).thenReturn(Optional.empty());

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.prijaviIspit(req));

            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        }

        @Test
        @DisplayName("Vec prijavljen -> 409")
        void alreadyExists_409() {
            IspitPrijavaRequest req = new IspitPrijavaRequest();
            req.setStudentIndeksId(10L);
            req.setIspitId(20L);
            req.setDatum(LocalDate.now());

            SkolskaGodina sg = skGod(1L);
            IspitniRok r = rok(2L, sg);
            Predmet p = predmet(3L, "Mat");
            Nastavnik n = nastavnik(4L);

            StudentPodaci s = student(1L, "A", "B");
            StudentIndeks si = indeks(10L, "RI", 2025, 1, s);
            Ispit ispit = ispit(20L, p, n, r, false);

            when(studentIndeksRepository.findById(10L)).thenReturn(Optional.of(si));
            when(ispitRepository.findById(20L)).thenReturn(Optional.of(ispit));
            when(ispitPrijavaRepository.existsByStudentIndeksIdAndIspitId(10L, 20L)).thenReturn(true);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.prijaviIspit(req));

            assertEquals(HttpStatus.CONFLICT, ex.getStatus());
            verify(ispitPrijavaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Ne slusa predmet u toj skolskoj godini -> 400")
        void notListening_400() {
            IspitPrijavaRequest req = new IspitPrijavaRequest();
            req.setStudentIndeksId(10L);
            req.setIspitId(20L);
            req.setDatum(LocalDate.now());

            SkolskaGodina sg = skGod(1L);
            IspitniRok r = rok(2L, sg);
            Predmet p = predmet(3L, "Mat");
            Nastavnik n = nastavnik(4L);

            StudentPodaci s = student(1L, "A", "B");
            StudentIndeks si = indeks(10L, "RI", 2025, 1, s);
            Ispit ispit = ispit(20L, p, n, r, false);

            when(studentIndeksRepository.findById(10L)).thenReturn(Optional.of(si));
            when(ispitRepository.findById(20L)).thenReturn(Optional.of(ispit));
            when(ispitPrijavaRepository.existsByStudentIndeksIdAndIspitId(10L, 20L)).thenReturn(false);

            when(slusaPredmetRepository.existsByStudentIndeksIdAndDrziPredmet_Predmet_IdAndSkolskaGodina_Id(
                    10L, 3L, 1L
            )).thenReturn(false);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.prijaviIspit(req));

            assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
            verify(ispitPrijavaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Happy path -> save prijavu i vrati response")
        void happyPath_saves() {
            IspitPrijavaRequest req = new IspitPrijavaRequest();
            req.setStudentIndeksId(10L);
            req.setIspitId(20L);
            req.setDatum(LocalDate.of(2026, 1, 5));

            SkolskaGodina sg = skGod(1L);
            IspitniRok r = rok(2L, sg);
            Predmet p = predmet(3L, "Mat");
            Nastavnik n = nastavnik(4L);

            StudentPodaci s = student(1L, "A", "B");
            StudentIndeks si = indeks(10L, "RI", 2025, 1, s);
            Ispit ispit = ispit(20L, p, n, r, false);

            when(studentIndeksRepository.findById(10L)).thenReturn(Optional.of(si));
            when(ispitRepository.findById(20L)).thenReturn(Optional.of(ispit));
            when(ispitPrijavaRepository.existsByStudentIndeksIdAndIspitId(10L, 20L)).thenReturn(false);

            when(slusaPredmetRepository.existsByStudentIndeksIdAndDrziPredmet_Predmet_IdAndSkolskaGodina_Id(
                    10L, 3L, 1L
            )).thenReturn(true);

            IspitPrijava saved = new IspitPrijava();
            saved.setId(777L);
            saved.setStudentIndeks(si);
            saved.setIspit(ispit);
            saved.setDatum(LocalDate.now());

            when(ispitPrijavaRepository.save(any(IspitPrijava.class))).thenReturn(saved);

            IspitPrijavaResponse out = service.prijaviIspit(req);

            assertNotNull(out);
            assertEquals(777L, out.getId());

            verify(ispitPrijavaRepository).save(prijavaCaptor.capture());
            assertEquals(si, prijavaCaptor.getValue().getStudentIndeks());
            assertEquals(ispit, prijavaCaptor.getValue().getIspit());
            assertEquals(LocalDate.now(), prijavaCaptor.getValue().getDatum());
        }
    }

    // -------------------- getRezultatiIspita --------------------

    @Nested
    class RezultatiIspita {

        @Test
        @DisplayName("Ispit ne postoji -> 404")
        void ispitNotFound_404() {
            when(ispitRepository.findById(1L)).thenReturn(Optional.empty());

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.getRezultatiIspita(1L));

            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        }

        @Test
        @DisplayName("Sortira po programu, godini upisa, broju i racuna ukupno (predispitni + ispitni)")
        void calculatesAndSorts() {
            SkolskaGodina sg = skGod(1L);
            IspitniRok r = rok(2L, sg);
            Predmet p = predmet(3L, "Mat");
            Nastavnik n = nastavnik(4L);
            Ispit ispit = ispit(10L, p, n, r, false);

            when(ispitRepository.findById(10L)).thenReturn(Optional.of(ispit));

            // studenti: RI 2024/2, RI 2025/1, SI 2025/1 (da proverimo sort)
            StudentPodaci sA = student(1L, "Ana", "A");
            StudentPodaci sB = student(2L, "Boris", "B");
            StudentPodaci sC = student(3L, "Ceca", "C");

            StudentIndeks si1 = indeks(101L, "RI", 2024, 2, sA);
            StudentIndeks si2 = indeks(102L, "RI", 2025, 1, sB);
            StudentIndeks si3 = indeks(103L, "SI", 2025, 1, sC);

            IspitPrijava ip1 = prijava(201L, si2, ispit);
            IspitPrijava ip2 = prijava(202L, si1, ispit);
            IspitPrijava ip3 = prijava(203L, si3, ispit);

            when(ispitPrijavaRepository.findAllByIspitId(10L)).thenReturn(List.of(ip1, ip2, ip3));

            // predispitni: si1=20, si2=null (tretira se kao 0), si3=10
            when(predispRepo.sumPoeniZaStudentaPredmetGodinu(102L, 3L, 1L)).thenReturn(null);
            when(predispRepo.sumPoeniZaStudentaPredmetGodinu(101L, 3L, 1L)).thenReturn(20);
            when(predispRepo.sumPoeniZaStudentaPredmetGodinu(103L, 3L, 1L)).thenReturn(10);

            // ispitni: ip1=30, ip2 nema izlazak -> 0, ip3=40
            IspitIzlazak izl1 = new IspitIzlazak(); izl1.setBrojPoena(30); izl1.setPonistava(false);
            IspitIzlazak izl3 = new IspitIzlazak(); izl3.setBrojPoena(40); izl3.setPonistava(false);

            when(ispitIzlazakRepository.findTopByIspitPrijava_IdAndPonistavaFalseOrderByIdDesc(201L))
                    .thenReturn(Optional.of(izl1));
            when(ispitIzlazakRepository.findTopByIspitPrijava_IdAndPonistavaFalseOrderByIdDesc(202L))
                    .thenReturn(Optional.empty());
            when(ispitIzlazakRepository.findTopByIspitPrijava_IdAndPonistavaFalseOrderByIdDesc(203L))
                    .thenReturn(Optional.of(izl3));

            List<IspitRezultatResponse> out = service.getRezultatiIspita(10L);
            assertEquals(3, out.size());

            // sort očekivanje: RI(2024,2) pa RI(2025,1) pa SI(2025,1)
            assertEquals("RI", out.get(0).getStudProgramOznaka());
            assertEquals(2024, out.get(0).getGodinaUpisa());
            assertEquals(2, out.get(0).getBrojIndeksa());

            assertEquals("RI", out.get(1).getStudProgramOznaka());
            assertEquals(2025, out.get(1).getGodinaUpisa());
            assertEquals(1, out.get(1).getBrojIndeksa());

            assertEquals("SI", out.get(2).getStudProgramOznaka());

            // ukupno: si1 20+0=20, si2 0+30=30, si3 10+40=50
            assertEquals(20, out.get(0).getUkupno());
            assertEquals(0, out.get(1).getPredispitni());
            assertEquals(30, out.get(1).getUkupno());
            assertEquals(50, out.get(2).getUkupno());
        }
    }

    // -------------------- dodajIspitIzlazak --------------------

    @Nested
    class DodajIspitIzlazak {

        @Test
        @DisplayName("Nevalidan req (null/negativan brojPoena) -> 400")
        void invalidReq_400() {
            IspitIzlazakRequest req = new IspitIzlazakRequest();
            req.setIspitPrijavaId(null);
            req.setBrojPoena(-1);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.dodajIspitIzlazak(req));

            assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        }

        @Test
        @DisplayName("Prijava ne postoji -> 404")
        void prijavaNotFound_404() {
            IspitIzlazakRequest req = new IspitIzlazakRequest();
            req.setIspitPrijavaId(1L);
            req.setBrojPoena(10);

            when(ispitPrijavaRepository.findById(1L)).thenReturn(Optional.empty());

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.dodajIspitIzlazak(req));

            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        }

        @Test
        @DisplayName("studentIndeksId ne odgovara prijavi -> 400")
        void studentMismatch_400() {
            SkolskaGodina sg = skGod(1L);
            IspitniRok r = rok(2L, sg);
            Predmet p = predmet(3L, "Mat");
            Nastavnik n = nastavnik(4L);
            Ispit ispit = ispit(10L, p, n, r, false);

            StudentPodaci st = student(1L, "A", "B");
            StudentIndeks si = indeks(100L, "RI", 2025, 1, st);
            IspitPrijava ip = prijava(200L, si, ispit);

            when(ispitPrijavaRepository.findById(200L)).thenReturn(Optional.of(ip));

            IspitIzlazakRequest req = new IspitIzlazakRequest();
            req.setIspitPrijavaId(200L);
            req.setBrojPoena(10);
            req.setStudentIndeksId(999L);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.dodajIspitIzlazak(req));

            assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        }

        @Test
        @DisplayName("Za prijavu vec postoji izlazak -> 409")
        void alreadyHasExit_409() {
            SkolskaGodina sg = skGod(1L);
            IspitniRok r = rok(2L, sg);
            Predmet p = predmet(3L, "Mat");
            Nastavnik n = nastavnik(4L);
            Ispit ispit = ispit(10L, p, n, r, false);

            StudentPodaci st = student(1L, "A", "B");
            StudentIndeks si = indeks(100L, "RI", 2025, 1, st);
            IspitPrijava ip = prijava(200L, si, ispit);

            IspitIzlazak existingExit = new IspitIzlazak();
            existingExit.setId(999L);
            ip.setIspitIzlazak(existingExit);

            when(ispitPrijavaRepository.findById(200L)).thenReturn(Optional.of(ip));

            IspitIzlazakRequest req = new IspitIzlazakRequest();
            req.setIspitPrijavaId(200L);
            req.setBrojPoena(10);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.dodajIspitIzlazak(req));

            assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        }

        @Test
        @DisplayName("Ispit je zakljucen -> 400")
        void ispitZakljucen_400() {
            SkolskaGodina sg = skGod(1L);
            IspitniRok r = rok(2L, sg);
            Predmet p = predmet(3L, "Mat");
            Nastavnik n = nastavnik(4L);
            Ispit ispit = ispit(10L, p, n, r, true); // zaključen

            StudentPodaci st = student(1L, "A", "B");
            StudentIndeks si = indeks(100L, "RI", 2025, 1, st);
            IspitPrijava ip = prijava(200L, si, ispit);

            when(ispitPrijavaRepository.findById(200L)).thenReturn(Optional.of(ip));

            IspitIzlazakRequest req = new IspitIzlazakRequest();
            req.setIspitPrijavaId(200L);
            req.setBrojPoena(10);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.dodajIspitIzlazak(req));

            assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        }

        @Test
        @DisplayName("Ponistava=true -> sacuva izlazak i ne upisuje polozen predmet")
        void ponistava_true_noPolozen() {
            SkolskaGodina sg = skGod(1L);
            IspitniRok r = rok(2L, sg);
            Predmet p = predmet(3L, "Mat");
            Nastavnik n = nastavnik(4L);
            Ispit ispit = ispit(10L, p, n, r, false);

            StudentPodaci st = student(1L, "A", "B");
            StudentIndeks si = indeks(100L, "RI", 2025, 1, st);
            IspitPrijava ip = prijava(200L, si, ispit);

            when(ispitPrijavaRepository.findById(200L)).thenReturn(Optional.of(ip));

            IspitIzlazak saved = new IspitIzlazak();
            saved.setId(555L);
            when(ispitIzlazakRepository.save(any(IspitIzlazak.class))).thenReturn(saved);

            when(ispitPrijavaRepository.save(any(IspitPrijava.class))).thenAnswer(inv -> inv.getArgument(0));

            IspitIzlazakRequest req = new IspitIzlazakRequest();
            req.setIspitPrijavaId(200L);
            req.setBrojPoena(60);
            req.setPonistava(true);

            Long outId = service.dodajIspitIzlazak(req);
            assertEquals(555L, outId);

            verify(polozenPredmetRepository, never()).save(any());
        }

        @Test
        @DisplayName("Ukupno < 51 -> nema polozenog predmeta")
        void ukupnoBelow51_noPolozen() {
            SkolskaGodina sg = skGod(1L);
            IspitniRok r = rok(2L, sg);
            Predmet p = predmet(3L, "Mat");
            Nastavnik n = nastavnik(4L);
            Ispit ispit = ispit(10L, p, n, r, false);

            StudentPodaci st = student(1L, "A", "B");
            StudentIndeks si = indeks(100L, "RI", 2025, 1, st);
            IspitPrijava ip = prijava(200L, si, ispit);

            when(ispitPrijavaRepository.findById(200L)).thenReturn(Optional.of(ip));

            IspitIzlazak saved = new IspitIzlazak();
            saved.setId(777L);
            when(ispitIzlazakRepository.save(any(IspitIzlazak.class))).thenReturn(saved);
            when(ispitPrijavaRepository.save(any(IspitPrijava.class))).thenAnswer(inv -> inv.getArgument(0));

            when(predispRepo.sumPoeniZaStudentaPredmetGodinu(100L, 3L, 1L)).thenReturn(10); // 10 + 40 = 50

            IspitIzlazakRequest req = new IspitIzlazakRequest();
            req.setIspitPrijavaId(200L);
            req.setBrojPoena(40);

            Long outId = service.dodajIspitIzlazak(req);
            assertEquals(777L, outId);

            verify(polozenPredmetRepository, never()).save(any());
        }

        @Test
        @DisplayName("Ukupno >= 51 i ne postoji polozen -> kreira novi")
        void ukupnoAtLeast51_createsNewPolozen() {
            SkolskaGodina sg = skGod(1L);
            IspitniRok r = rok(2L, sg);
            Predmet p = predmet(3L, "Mat");
            Nastavnik n = nastavnik(4L);
            Ispit ispit = ispit(10L, p, n, r, false);

            StudentPodaci st = student(1L, "A", "B");
            StudentIndeks si = indeks(100L, "RI", 2025, 1, st);
            IspitPrijava ip = prijava(200L, si, ispit);

            when(ispitPrijavaRepository.findById(200L)).thenReturn(Optional.of(ip));

            IspitIzlazak saved = new IspitIzlazak();
            saved.setId(888L);
            when(ispitIzlazakRepository.save(any(IspitIzlazak.class))).thenReturn(saved);
            when(ispitPrijavaRepository.save(any(IspitPrijava.class))).thenAnswer(inv -> inv.getArgument(0));

            when(predispRepo.sumPoeniZaStudentaPredmetGodinu(100L, 3L, 1L)).thenReturn(20); // 20 + 40 = 60
            when(polozenPredmetRepository.findByStudentIndeksAndPredmet(100L, 3L)).thenReturn(Optional.empty());

            when(polozenPredmetRepository.save(any(PolozenPredmet.class)))
                    .thenAnswer(inv -> inv.getArgument(0, PolozenPredmet.class));

            IspitIzlazakRequest req = new IspitIzlazakRequest();
            req.setIspitPrijavaId(200L);
            req.setBrojPoena(40);

            Long outId = service.dodajIspitIzlazak(req);
            assertEquals(888L, outId);

            verify(polozenPredmetRepository).save(polozenCaptor.capture());
            PolozenPredmet pp = polozenCaptor.getValue();

            assertEquals(si, pp.getStudentIndeks());
            assertEquals(p, pp.getPredmet());
            assertEquals(saved, pp.getIspitIzlazak());
            assertFalse(pp.isPriznat());
            assertEquals(6, pp.getOcena()); // 60 poena -> 7
        }

        @Test
        @DisplayName("Ukupno >= 51 i postoji polozen sa nizom ocenom -> updateuje na vecu")
        void existingPolozen_updatesIfHigher() {
            SkolskaGodina sg = skGod(1L);
            IspitniRok r = rok(2L, sg);
            Predmet p = predmet(3L, "Mat");
            Nastavnik n = nastavnik(4L);
            Ispit ispit = ispit(10L, p, n, r, false);

            StudentPodaci st = student(1L, "A", "B");
            StudentIndeks si = indeks(100L, "RI", 2025, 1, st);
            IspitPrijava ip = prijava(200L, si, ispit);

            when(ispitPrijavaRepository.findById(200L)).thenReturn(Optional.of(ip));

            IspitIzlazak savedExit = new IspitIzlazak();
            savedExit.setId(999L);
            when(ispitIzlazakRepository.save(any(IspitIzlazak.class))).thenReturn(savedExit);
            when(ispitPrijavaRepository.save(any(IspitPrijava.class))).thenAnswer(inv -> inv.getArgument(0));

            when(predispRepo.sumPoeniZaStudentaPredmetGodinu(100L, 3L, 1L)).thenReturn(50); // 50 + 41 = 91 -> 10

            PolozenPredmet existing = new PolozenPredmet();
            existing.setId(777L);
            existing.setOcena(8);
            existing.setStudentIndeks(si);
            existing.setPredmet(p);

            when(polozenPredmetRepository.findByStudentIndeksAndPredmet(100L, 3L))
                    .thenReturn(Optional.of(existing));

            when(polozenPredmetRepository.save(any(PolozenPredmet.class)))
                    .thenAnswer(inv -> inv.getArgument(0, PolozenPredmet.class));

            IspitIzlazakRequest req = new IspitIzlazakRequest();
            req.setIspitPrijavaId(200L);
            req.setBrojPoena(41);

            service.dodajIspitIzlazak(req);

            verify(polozenPredmetRepository).save(polozenCaptor.capture());
            PolozenPredmet savedPp = polozenCaptor.getValue();

            assertEquals(10, savedPp.getOcena());
            assertEquals(savedExit, savedPp.getIspitIzlazak());
        }
    }

    // -------------------- getPredispitniPoeni --------------------

    @Test
    @DisplayName("getPredispitniPoeni: null suma -> 0 i mapira stavke")
    void getPredispitniPoeni_mapsAndZeroWhenNull() {
        when(predispRepo.sumPoeniZaStudentaPredmetGodinu(1L, 2L, 3L)).thenReturn(null);
        when(predispRepo.findAllBySlusaPredmet_StudentIndeks_IdAndSlusaPredmet_DrziPredmet_Predmet_IdAndSlusaPredmet_SkolskaGodina_Id(
                1L, 2L, 3L
        )).thenReturn(Collections.emptyList());

        PredispitniPoeniStudentResponse out = service.getPredispitniPoeni(1L, 2L, 3L);
        assertNotNull(out);
        assertEquals(0, out.getUkupno());
        assertNotNull(out.getStavke());
        assertTrue(out.getStavke().isEmpty());
    }

    // -------------------- countIzlazakaNaPredmet --------------------

    @Test
    @DisplayName("countIzlazakaNaPredmet delegira na repo")
    void countIzlazaka_delegates() {
        when(ispitIzlazakRepository.countByStudentIndeks_IdAndIspitPrijava_Ispit_Predmet_Id(10L, 20L))
                .thenReturn(5L);

        long out = service.countIzlazakaNaPredmet(10L, 20L);
        assertEquals(5L, out);
    }

    // -------------------- deleteById --------------------

    @Nested
    class DeleteById {

        @Test
        @DisplayName("Ispit ne postoji -> 404")
        void notFound_404() {
            when(ispitRepository.existsById(1L)).thenReturn(false);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.deleteById(1L));

            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
            verify(ispitRepository, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("Kaskadno brise prijave, izlazak i polozenPredmet vezan za izlazak, pa ispit")
        void cascadeDelete_happyPath() {
            when(ispitRepository.existsById(10L)).thenReturn(true);

            IspitPrijava ip1 = new IspitPrijava();
            ip1.setId(101L);
            IspitIzlazak ie = new IspitIzlazak();
            ie.setId(201L);
            ip1.setIspitIzlazak(ie);

            IspitPrijava ip2 = new IspitPrijava();
            ip2.setId(102L);

            when(ispitPrijavaRepository.findAllByIspitId(10L)).thenReturn(List.of(ip1, ip2));

            PolozenPredmet pp = new PolozenPredmet();
            pp.setId(301L);
            pp.setIspitIzlazak(ie);

            // service koristi StreamSupport nad findAll()
            when(polozenPredmetRepository.findAll()).thenReturn(List.of(pp));

            service.deleteById(10L);

            verify(polozenPredmetRepository).deleteById(301L);
            verify(ispitIzlazakRepository).deleteById(201L);

            verify(ispitPrijavaRepository).deleteById(101L);
            verify(ispitPrijavaRepository).deleteById(102L);

            verify(ispitRepository).deleteById(10L);
        }

        @Test
        @DisplayName("Ako deleteById baci DataIntegrityViolationException -> 409")
        void dataIntegrity_409() {
            when(ispitRepository.existsById(10L)).thenReturn(true);
            when(ispitPrijavaRepository.findAllByIspitId(10L)).thenReturn(Collections.emptyList());

            doThrow(new DataIntegrityViolationException("x"))
                    .when(ispitRepository).deleteById(10L);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.deleteById(10L));

            assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        }
    }
}
