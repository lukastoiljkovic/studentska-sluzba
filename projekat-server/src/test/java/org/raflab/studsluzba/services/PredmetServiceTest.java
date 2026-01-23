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
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PredmetServiceTest {

    @Mock PredmetRepository predmetRepository;
    @Mock StudijskiProgramRepository studijskiProgramRepository;
    @Mock PolozenPredmetRepository polozenPredmetRepository;
    @Mock DrziPredmetRepository drziPredmetRepository;
    @Mock PredispitnaObavezaRepository predispitnaObavezaRepository;
    @Mock SlusaPredmetRepository slusaPredmetRepository;
    @Mock PredispitnaIzlazakRepository predispitnaIzlazakRepository;
    @Mock IspitRepository ispitRepository;

    @InjectMocks PredmetService predmetService;

    // ---- PolozenPredmetService deps ----
    @Mock StudentIndeksRepository studentIndeksRepository;
    @Mock IspitIzlazakRepository ispitIzlazakRepository;

    @InjectMocks PolozenPredmetService polozenPredmetService;

    @Captor ArgumentCaptor<Predmet> predmetCaptor;
    @Captor ArgumentCaptor<PolozenPredmet> polozenCaptor;

    // ---------------- helpers ----------------

    private static StudijskiProgram studProgram(Long id, int trajanjeSemestara) {
        StudijskiProgram sp = new StudijskiProgram();
        sp.setId(id);
        sp.setTrajanjeSemestara(trajanjeSemestara);
        sp.setNaziv("RI");
        sp.setOznaka("RI");
        sp.setGodinaAkreditacije(2020);
        return sp;
    }

    private static PredmetRequest predmetReq(String sifra, Long spId, Integer semestar) {
        PredmetRequest r = new PredmetRequest();
        r.setSifra(sifra);
        r.setNaziv("Algoritmi");
        r.setOpis("opis");
        r.setEspb(6);
        r.setObavezan(true);
        r.setSemestar(semestar);
        r.setFondPredavanja(2);
        r.setFondVezbi(2);
        r.setStudProgramId(spId);
        return r;
    }

    private static Predmet predmet(Long id, String sifra) {
        Predmet p = new Predmet();
        p.setId(id);
        p.setSifra(sifra);
        p.setNaziv("Algoritmi");
        p.setEspb(6);
        p.setSemestar(1);
        p.setObavezan(true);
        return p;
    }

    private static StudentIndeks indeks(Long id) {
        StudentIndeks si = new StudentIndeks();
        si.setId(id);
        si.setGodina(2025);
        si.setBroj(1);
        si.setStudProgramOznaka("RI");
        StudentPodaci st = new StudentPodaci();
        st.setId(999L);
        st.setIme("Ana");
        st.setPrezime("Anić");
        si.setStudent(st);
        return si;
    }

    private static PolozenPredmetRequest polozenReq(Long siId, Long predmetId, Integer ocena, Long izlazakId) {
        PolozenPredmetRequest r = new PolozenPredmetRequest();
        r.setStudentIndeksId(siId);
        r.setPredmetId(predmetId);
        r.setOcena(ocena);
        r.setPriznat(false);
        r.setIspitIzlazakId(izlazakId);
        return r;
    }

    private static PredispitnaObaveza obaveza(Long id, Predmet p) {
        PredispitnaObaveza po = new PredispitnaObaveza();
        po.setId(id);
        po.setPredmet(p);
        po.setVrsta("Kolokvijum");
        po.setMaxPoena(30);
        return po;
    }

    private static PredispitnaIzlazak izlazakPred(Long id, PredispitnaObaveza po, SlusaPredmet sp) {
        PredispitnaIzlazak pi = new PredispitnaIzlazak();
        pi.setId(id);
        pi.setPredispitnaObaveza(po);
        pi.setSlusaPredmet(sp);
        pi.setPoeni(10);
        pi.setDatum(LocalDate.of(2026, 1, 1));
        return pi;
    }

    private static DrziPredmet drzi(Long id, Predmet p, Nastavnik n) {
        DrziPredmet dp = new DrziPredmet();
        dp.setId(id);
        dp.setPredmet(p);
        dp.setNastavnik(n);
        return dp;
    }

    private static SlusaPredmet slusa(Long id, StudentIndeks si, DrziPredmet dp) {
        SlusaPredmet sp = new SlusaPredmet();
        sp.setId(id);
        sp.setStudentIndeks(si);
        sp.setDrziPredmet(dp);
        SkolskaGodina sg = new SkolskaGodina();
        sg.setId(1L);
        sg.setNaziv("2025/2026.");
        sp.setSkolskaGodina(sg);
        return sp;
    }

    private static Nastavnik nastavnik(Long id, String ime, String prezime) {
        Nastavnik n = new Nastavnik();
        n.setId(id);
        n.setIme(ime);
        n.setPrezime(prezime);
        n.setEmail("n@raf.rs");
        return n;
    }

    // =========================================================
    // PredmetService tests
    // =========================================================

    @Nested
    class PredmetServiceTests {

        @Test
        @DisplayName("addPredmet: blank sifra -> 400")
        void addPredmet_blankSifra_400() {
            PredmetRequest req = predmetReq("   ", 1L, 1);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> predmetService.addPredmet(req));

            assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
            verifyNoInteractions(predmetRepository, studijskiProgramRepository);
        }

        @Test
        @DisplayName("addPredmet: exists sifra -> 409")
        void addPredmet_existsSifra_409() {
            when(predmetRepository.existsBySifraIgnoreCase("CS101")).thenReturn(true);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> predmetService.addPredmet(predmetReq("cs101", 1L, 1)));

            assertEquals(HttpStatus.CONFLICT, ex.getStatus());
            verify(predmetRepository).existsBySifraIgnoreCase("CS101");
            verify(predmetRepository, never()).save(any());
        }

        @Test
        @DisplayName("addPredmet: studProgramId null -> 400")
        void addPredmet_missingStudProgramId_400() {
            PredmetRequest req = predmetReq("CS101", null, 1);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> predmetService.addPredmet(req));

            assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
            verify(predmetRepository, never()).save(any());
        }

        @Test
        @DisplayName("addPredmet: studijski program ne postoji -> 404")
        void addPredmet_spMissing_404() {
            when(predmetRepository.existsBySifraIgnoreCase("CS101")).thenReturn(false);
            when(studijskiProgramRepository.findById(1L)).thenReturn(Optional.empty());

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> predmetService.addPredmet(predmetReq("cs101", 1L, 1)));

            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
            verify(predmetRepository, never()).save(any());
        }

        @Test
        @DisplayName("addPredmet: semestar van opsega -> 400")
        void addPredmet_semestarOutOfRange_400() {
            when(predmetRepository.existsBySifraIgnoreCase("CS101")).thenReturn(false);
            when(studijskiProgramRepository.findById(1L)).thenReturn(Optional.of(studProgram(1L, 8)));

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> predmetService.addPredmet(predmetReq("CS101", 1L, 99)));

            assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
            verify(predmetRepository, never()).save(any());
        }

        @Test
        @DisplayName("addPredmet: happy path -> trim/uppercase sifra + save")
        void addPredmet_happyPath_setsUppercaseSifra() {
            when(predmetRepository.existsBySifraIgnoreCase("CS101")).thenReturn(false);
            StudijskiProgram sp = studProgram(1L, 8);
            when(studijskiProgramRepository.findById(1L)).thenReturn(Optional.of(sp));

            Predmet saved = new Predmet();
            saved.setId(10L);
            when(predmetRepository.save(any(Predmet.class))).thenReturn(saved);

            Long id = predmetService.addPredmet(predmetReq("  cs101  ", 1L, 1));
            assertEquals(10L, id);

            verify(predmetRepository).save(predmetCaptor.capture());
            Predmet toSave = predmetCaptor.getValue();

            assertEquals("CS101", toSave.getSifra());
            assertEquals(sp, toSave.getStudProgram());
            assertEquals(1, toSave.getSemestar());
        }

        @Test
        @DisplayName("deletePredmet: predmet ne postoji -> 404")
        void deletePredmet_notFound_404() {
            when(predmetRepository.existsById(5L)).thenReturn(false);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> predmetService.deletePredmet(5L));

            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
            verify(predmetRepository, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("deletePredmet: ima zakazanih ispita -> 409 i ne briše predmet")
        void deletePredmet_hasScheduledExams_conflict409() {
            when(predmetRepository.existsById(1L)).thenReturn(true);

            when(predispitnaObavezaRepository.findAll()).thenReturn(Collections.emptyList());
            when(drziPredmetRepository.findAll()).thenReturn(Collections.emptyList());
            when(polozenPredmetRepository.findAll()).thenReturn(Collections.emptyList());

            Ispit exam = new Ispit();
            Predmet p = predmet(1L, "CS101");
            exam.setId(77L);
            exam.setPredmet(p);
            when(ispitRepository.findAll()).thenReturn(List.of(exam));

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> predmetService.deletePredmet(1L));

            assertEquals(HttpStatus.CONFLICT, ex.getStatus());
            verify(predmetRepository, never()).deleteById(1L);
        }

        @Test
        @DisplayName("deletePredmet: happy cascade (obaveze+izlasci, drzi+slusa+izlasci) pa delete predmet")
        void deletePredmet_happyCascade() {
            Long predmetId = 1L;
            when(predmetRepository.existsById(predmetId)).thenReturn(true);

            Predmet p = predmet(predmetId, "CS101");

            // predispitne obaveze + izlazci
            PredispitnaObaveza po1 = obaveza(10L, p);
            when(predispitnaObavezaRepository.findAll()).thenReturn(List.of(po1));

            // drzi/slusa + izlazci vezani na slusaPredmet
            Nastavnik n = nastavnik(1L, "Pera", "Perić");
            DrziPredmet dp1 = drzi(20L, p, n);
            when(drziPredmetRepository.findAll()).thenReturn(List.of(dp1));

            StudentIndeks si = indeks(100L);
            SlusaPredmet sp1 = slusa(30L, si, dp1);
            when(slusaPredmetRepository.findAll()).thenReturn(List.of(sp1));

            PredispitnaIzlazak pi1 = izlazakPred(40L, po1, sp1);
            PredispitnaIzlazak pi2 = izlazakPred(41L, po1, sp1);

            // service filtrira preko predispitnaIzlazakRepository.findAll() više puta
            when(predispitnaIzlazakRepository.findAll()).thenReturn(List.of(pi1, pi2));

            // nema polozenih i nema ispita
            when(polozenPredmetRepository.findAll()).thenReturn(Collections.emptyList());
            when(ispitRepository.findAll()).thenReturn(Collections.emptyList());

            predmetService.deletePredmet(predmetId);

            verify(predispitnaIzlazakRepository, times(2)).deleteById(40L);
            verify(predispitnaIzlazakRepository, times(2)).deleteById(41L);
            verify(predispitnaObavezaRepository).deleteById(10L);

            verify(slusaPredmetRepository).deleteById(30L);
            verify(drziPredmetRepository).deleteById(20L);

            verify(predmetRepository).deleteById(predmetId);
        }

        @Test
        @DisplayName("getProsecnaOcenaZaPredmetURasponu: invalid range -> 400")
        void avg_invalidRange_400() {
            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> predmetService.getProsecnaOcenaZaPredmetURasponu(1L, 2026, 2025));
            assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        }

        @Test
        @DisplayName("getProsecnaOcenaZaPredmetURasponu: predmet ne postoji -> 404")
        void avg_predmetMissing_404() {
            when(predmetRepository.existsById(1L)).thenReturn(false);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> predmetService.getProsecnaOcenaZaPredmetURasponu(1L, 2020, 2025));

            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        }

        @Test
        @DisplayName("getProsecnaOcenaZaPredmetURasponu: avg null -> 0.0")
        void avg_null_returnsZero() {
            when(predmetRepository.existsById(1L)).thenReturn(true);
            when(polozenPredmetRepository.findAverageGradeForPredmetAndYearRange(1L, "2020", "2025"))
                    .thenReturn(null);

            Double out = predmetService.getProsecnaOcenaZaPredmetURasponu(1L, 2020, 2025);
            assertEquals(0.0, out);
        }

        @Test
        @DisplayName("getProsecnaOcenaZaPredmetURasponu: avg non-null -> returns avg")
        void avg_nonNull_returnsAvg() {
            when(predmetRepository.existsById(1L)).thenReturn(true);
            when(polozenPredmetRepository.findAverageGradeForPredmetAndYearRange(1L, "2020", "2025"))
                    .thenReturn(8.25);

            Double out = predmetService.getProsecnaOcenaZaPredmetURasponu(1L, 2020, 2025);
            assertEquals(8.25, out);
        }
    }

    // =========================================================
    // PolozenPredmetService tests
    // =========================================================

    @Nested
    class PolozenPredmetServiceTests {

        @Test
        @DisplayName("getPolozeniIspiti: delegira na repo i mapira")
        void getPolozeniIspiti_delegates() {
            Long siId = 100L;
            Pageable pageable = PageRequest.of(0, 10);

            StudentIndeks si = indeks(siId);
            Predmet p = predmet(1L, "CS101");

            PolozenPredmet pp = new PolozenPredmet();
            pp.setId(5L);
            pp.setStudentIndeks(si);
            pp.setPredmet(p);
            pp.setOcena(8);

            when(polozenPredmetRepository.findByStudentIndeksIdAndOcenaIsNotNull(siId, pageable))
                    .thenReturn(new PageImpl<>(List.of(pp), pageable, 1));

            Page<PolozenPredmetResponse> out = polozenPredmetService.getPolozeniIspiti(siId, pageable);

            assertEquals(1, out.getTotalElements());
            verify(polozenPredmetRepository).findByStudentIndeksIdAndOcenaIsNotNull(siId, pageable);
        }

        @Test
        @DisplayName("getNepolozeniIspiti: filtrira položene, deduplikacija predmeta, setuje broj izlazaka")
        void getNepolozeniIspiti_filtersAndDedupes() {
            Long siId = 100L;
            Pageable pageable = PageRequest.of(0, 10);

            StudentIndeks si = indeks(siId);

            Predmet p1 = predmet(1L, "CS101");
            Predmet p2 = predmet(2L, "CS102"); // ovaj je položen i treba da se preskoči

            Nastavnik n = nastavnik(1L, "Pera", "Perić");

            DrziPredmet dp1 = drzi(10L, p1, n);
            DrziPredmet dp2 = drzi(11L, p2, n);

            // namerno 2 slusaPredmet za isti p1 -> dedupe
            SlusaPredmet sp1 = slusa(20L, si, dp1);
            SlusaPredmet sp1_dup = slusa(21L, si, dp1);
            SlusaPredmet sp2 = slusa(22L, si, dp2);

            when(slusaPredmetRepository.findAllByStudentIndeksIdWithPredmet(siId))
                    .thenReturn(List.of(sp1, sp1_dup, sp2));

            PolozenPredmet ppP2 = new PolozenPredmet();
            ppP2.setId(50L);
            ppP2.setStudentIndeks(si);
            ppP2.setPredmet(p2);
            ppP2.setOcena(7);

            when(polozenPredmetRepository.findByStudentIndeksIdAndOcenaIsNotNull(siId))
                    .thenReturn(List.of(ppP2));

            when(ispitIzlazakRepository.countByStudentIndeks_IdAndIspitPrijava_Ispit_Predmet_Id(siId, 1L))
                    .thenReturn(3L);

            Page<NepolozenPredmetResponse> out = polozenPredmetService.getNepolozeniIspiti(siId, pageable);

            assertEquals(1, out.getTotalElements()); // samo p1
            NepolozenPredmetResponse r = out.getContent().get(0);

            assertEquals(20L, r.getSlusaPredmetId()); // prvi p1 koji je upao
            assertEquals(1L, r.getPredmetId());
            assertEquals("CS101", r.getPredmetSifra());
            assertEquals("Algoritmi", r.getPredmetNaziv());
            assertEquals(3, r.getBrojIzlazaka());
            assertEquals("Pera Perić", r.getNastavnikIme());
        }

        @Test
        @DisplayName("getNepolozeniIspiti: paginacija radi (offset/limit na listi)")
        void getNepolozeniIspiti_paginates() {
            Long siId = 100L;

            StudentIndeks si = indeks(siId);
            Nastavnik n = nastavnik(1L, "Pera", "Perić");

            Predmet p1 = predmet(1L, "CS101"); p1.setNaziv("A");
            Predmet p2 = predmet(2L, "CS102"); p2.setNaziv("B");
            Predmet p3 = predmet(3L, "CS103"); p3.setNaziv("C");

            DrziPredmet dp1 = drzi(10L, p1, n);
            DrziPredmet dp2 = drzi(11L, p2, n);
            DrziPredmet dp3 = drzi(12L, p3, n);

            SlusaPredmet sp1 = slusa(20L, si, dp1);
            SlusaPredmet sp2 = slusa(21L, si, dp2);
            SlusaPredmet sp3 = slusa(22L, si, dp3);

            when(slusaPredmetRepository.findAllByStudentIndeksIdWithPredmet(siId))
                    .thenReturn(List.of(sp1, sp2, sp3));
            when(polozenPredmetRepository.findByStudentIndeksIdAndOcenaIsNotNull(siId))
                    .thenReturn(Collections.emptyList());

            when(ispitIzlazakRepository.countByStudentIndeks_IdAndIspitPrijava_Ispit_Predmet_Id(anyLong(), anyLong()))
                    .thenReturn(0L);

            Pageable pageable = PageRequest.of(1, 1); // druga stranica, 1 item -> treba da vrati p2
            Page<NepolozenPredmetResponse> out = polozenPredmetService.getNepolozeniIspiti(siId, pageable);

            assertEquals(3, out.getTotalElements());
            assertEquals(1, out.getContent().size());
            assertEquals(2L, out.getContent().get(0).getPredmetId());
        }

        @Test
        @DisplayName("addPolozenPredmet: bez ispitIzlazakId -> save i vrati id")
        void addPolozenPredmet_withoutIzlazak() {
            when(studentIndeksRepository.findById(100L)).thenReturn(Optional.of(indeks(100L)));
            when(predmetRepository.findById(1L)).thenReturn(Optional.of(predmet(1L, "CS101")));

            PolozenPredmet saved = new PolozenPredmet();
            saved.setId(77L);
            when(polozenPredmetRepository.save(any(PolozenPredmet.class))).thenReturn(saved);

            Long id = polozenPredmetService.addPolozenPredmet(polozenReq(100L, 1L, 8, null));
            assertEquals(77L, id);

            verify(polozenPredmetRepository).save(polozenCaptor.capture());
            assertNull(polozenCaptor.getValue().getIspitIzlazak());
        }

        @Test
        @DisplayName("addPolozenPredmet: sa ispitIzlazakId -> poveže izlazak i snimi")
        void addPolozenPredmet_withIzlazak() {
            when(studentIndeksRepository.findById(100L)).thenReturn(Optional.of(indeks(100L)));
            when(predmetRepository.findById(1L)).thenReturn(Optional.of(predmet(1L, "CS101")));

            IspitIzlazak ie = new IspitIzlazak();
            ie.setId(500L);
            when(ispitIzlazakRepository.findById(500L)).thenReturn(Optional.of(ie));

            PolozenPredmet saved = new PolozenPredmet();
            saved.setId(88L);
            when(polozenPredmetRepository.save(any(PolozenPredmet.class))).thenReturn(saved);

            Long id = polozenPredmetService.addPolozenPredmet(polozenReq(100L, 1L, 9, 500L));
            assertEquals(88L, id);

            verify(polozenPredmetRepository).save(polozenCaptor.capture());
            assertNotNull(polozenCaptor.getValue().getIspitIzlazak());
            assertEquals(500L, polozenCaptor.getValue().getIspitIzlazak().getId());
        }

        @Test
        @DisplayName("deleteById: ne postoji -> 404")
        void deleteById_notFound_404() {
            when(polozenPredmetRepository.existsById(1L)).thenReturn(false);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> polozenPredmetService.deleteById(1L));

            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
            verify(polozenPredmetRepository, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("deleteById: DataIntegrityViolation -> 409")
        void deleteById_conflict_409() {
            when(polozenPredmetRepository.existsById(1L)).thenReturn(true);
            doThrow(new DataIntegrityViolationException("fk"))
                    .when(polozenPredmetRepository).deleteById(1L);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> polozenPredmetService.deleteById(1L));

            assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        }
    }
}
