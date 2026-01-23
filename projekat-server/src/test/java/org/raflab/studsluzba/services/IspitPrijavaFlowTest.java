package org.raflab.studsluzba.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.raflab.studsluzba.dtos.IspitPrijavaRequest;
import org.raflab.studsluzba.dtos.IspitPrijavaResponse;
import org.raflab.studsluzba.model.entities.*;
import org.raflab.studsluzba.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IspitPrijavaFlowTest {

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

    @Captor ArgumentCaptor<IspitPrijava> prijavaCaptor;

    // -------- helperi (minimalno potrebno) --------

    private static StudentPodaci student(Long id, String ime, String prezime) {
        StudentPodaci s = new StudentPodaci();
        s.setId(id);
        s.setIme(ime);
        s.setPrezime(prezime);
        s.setSrednjeIme("X");
        s.setPol('M');
        s.setDatumRodjenja(LocalDate.of(2000, 1, 1));
        s.setDrzavaRodjenja("SRB");
        s.setMestoRodjenja("BG");
        s.setDrzavljanstvo("SRB");
        s.setMestoPrebivalista("BG");
        s.setAdresaPrebivalista("Ulica 1");
        s.setEmailFakultetski("f@raf.rs");
        s.setEmailPrivatni("p@mail.com");
        s.setBrojLicneKarte("123");
        s.setLicnuKartuIzdao("MUP");
        return s;
    }

    private static StudentIndeks indeks(Long id, String oznaka, int godina, int broj, StudentPodaci st) {
        StudentIndeks si = new StudentIndeks();
        si.setId(id);
        si.setStudProgramOznaka(oznaka);
        si.setGodina(godina);
        si.setBroj(broj);
        si.setStudent(st);
        si.setAktivan(true);
        return si;
    }

    private static SkolskaGodina skGod(Long id) {
        SkolskaGodina sg = new SkolskaGodina();
        sg.setId(id);
        sg.setNaziv("2025/2026.");
        sg.setAktivna(true);
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

    private static Predmet predmet(Long id, String sifra, String naziv) {
        Predmet p = new Predmet();
        p.setId(id);
        p.setSifra(sifra);
        p.setNaziv(naziv);
        p.setEspb(6);
        p.setSemestar(1);
        p.setObavezan(true);
        return p;
    }

    private static Ispit ispit(Long id, Predmet p, IspitniRok r) {
        Ispit i = new Ispit();
        i.setId(id);
        i.setPredmet(p);
        i.setIspitniRok(r);
        i.setDatumVremePocetka(LocalDateTime.of(2026, 1, 12, 10, 0));
        i.setZakljucen(false);
        return i;
    }

    private static IspitPrijavaRequest req(Long siId, Long ispitId) {
        IspitPrijavaRequest r = new IspitPrijavaRequest();
        r.setStudentIndeksId(siId);
        r.setIspitId(ispitId);
        return r;
    }

    // -------- tests --------

    @Test
    @DisplayName("Ako studentIndeksId ili ispitId null -> 400")
    void prijavi_nullFields_throws400() {
        ResponseStatusException ex1 = assertThrows(ResponseStatusException.class,
                () -> service.prijaviIspit(req(null, 10L)));
        assertEquals(HttpStatus.BAD_REQUEST, ex1.getStatus());

        ResponseStatusException ex2 = assertThrows(ResponseStatusException.class,
                () -> service.prijaviIspit(req(1L, null)));
        assertEquals(HttpStatus.BAD_REQUEST, ex2.getStatus());

        verifyNoInteractions(studentIndeksRepository, ispitRepository, ispitPrijavaRepository, slusaPredmetRepository);
    }

    @Test
    @DisplayName("StudentIndeks ne postoji -> 404")
    void prijavi_missingStudentIndeks_throws404() {
        when(studentIndeksRepository.findById(100L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.prijaviIspit(req(100L, 10L)));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        verify(studentIndeksRepository).findById(100L);
        verifyNoInteractions(ispitRepository, ispitPrijavaRepository, slusaPredmetRepository);
    }

    @Test
    @DisplayName("Ispit ne postoji -> 404")
    void prijavi_missingIspit_throws404() {
        StudentPodaci st = student(1L, "A", "B");
        StudentIndeks si = indeks(100L, "RI", 2025, 1, st);

        when(studentIndeksRepository.findById(100L)).thenReturn(Optional.of(si));
        when(ispitRepository.findById(10L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.prijaviIspit(req(100L, 10L)));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        verify(studentIndeksRepository).findById(100L);
        verify(ispitRepository).findById(10L);
        verifyNoInteractions(ispitPrijavaRepository, slusaPredmetRepository);
    }

    @Test
    @DisplayName("Ako je student već prijavljen -> 409")
    void prijavi_alreadyExists_throws409() {
        StudentPodaci st = student(1L, "A", "B");
        StudentIndeks si = indeks(100L, "RI", 2025, 1, st);

        SkolskaGodina sg = skGod(1L);
        IspitniRok r = rok(2L, sg);
        Predmet p = predmet(3L, "MAT1", "Mat");
        Ispit i = ispit(10L, p, r);

        when(studentIndeksRepository.findById(100L)).thenReturn(Optional.of(si));
        when(ispitRepository.findById(10L)).thenReturn(Optional.of(i));
        when(ispitPrijavaRepository.existsByStudentIndeksIdAndIspitId(100L, 10L)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.prijaviIspit(req(100L, 10L)));

        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        verify(ispitPrijavaRepository).existsByStudentIndeksIdAndIspitId(100L, 10L);
        verifyNoInteractions(slusaPredmetRepository);
        verify(ispitPrijavaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Ako student ne sluša predmet u skolskoj godini -> 400")
    void prijavi_notListening_throws400() {
        StudentPodaci st = student(1L, "A", "B");
        StudentIndeks si = indeks(100L, "RI", 2025, 1, st);

        SkolskaGodina sg = skGod(1L);
        IspitniRok r = rok(2L, sg);
        Predmet p = predmet(3L, "MAT1", "Mat");
        Ispit i = ispit(10L, p, r);

        when(studentIndeksRepository.findById(100L)).thenReturn(Optional.of(si));
        when(ispitRepository.findById(10L)).thenReturn(Optional.of(i));
        when(ispitPrijavaRepository.existsByStudentIndeksIdAndIspitId(100L, 10L)).thenReturn(false);

        when(slusaPredmetRepository.existsByStudentIndeksIdAndDrziPredmet_Predmet_IdAndSkolskaGodina_Id(
                100L, 3L, 1L)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.prijaviIspit(req(100L, 10L)));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        verify(ispitPrijavaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Happy path: sluša + nije prijavljen -> snima prijavu i vrati response")
    void prijavi_happyPath_savesAndReturnsResponse() {
        StudentPodaci st = student(1L, "A", "B");
        StudentIndeks si = indeks(100L, "RI", 2025, 1, st);

        SkolskaGodina sg = skGod(1L);
        IspitniRok r = rok(2L, sg);
        Predmet p = predmet(3L, "MAT1", "Mat");
        Ispit i = ispit(10L, p, r);

        when(studentIndeksRepository.findById(100L)).thenReturn(Optional.of(si));
        when(ispitRepository.findById(10L)).thenReturn(Optional.of(i));
        when(ispitPrijavaRepository.existsByStudentIndeksIdAndIspitId(100L, 10L)).thenReturn(false);

        when(slusaPredmetRepository.existsByStudentIndeksIdAndDrziPredmet_Predmet_IdAndSkolskaGodina_Id(
                100L, 3L, 1L)).thenReturn(true);

        // save vraća isti objekat ali sa ID
        when(ispitPrijavaRepository.save(any(IspitPrijava.class))).thenAnswer(inv -> {
            IspitPrijava x = inv.getArgument(0, IspitPrijava.class);
            x.setId(777L);
            return x;
        });

        IspitPrijavaResponse out = service.prijaviIspit(req(100L, 10L));

        assertNotNull(out);
        assertEquals(777L, out.getId());
        assertEquals(100L, out.getStudentIndeksId());
        assertEquals(10L, out.getIspitId());
        assertEquals("MAT1", out.getPredmetSifra());
        assertEquals("Mat", out.getPredmetNaziv());
        assertEquals(LocalDateTime.of(2026, 1, 12, 10, 0), out.getDatumIspita());

        verify(ispitPrijavaRepository).save(prijavaCaptor.capture());
        IspitPrijava saved = prijavaCaptor.getValue();

        assertEquals(si, saved.getStudentIndeks());
        assertEquals(i, saved.getIspit());
        assertEquals(LocalDate.now(), saved.getDatum()); // bitno: setuje se u servisu
    }
}
