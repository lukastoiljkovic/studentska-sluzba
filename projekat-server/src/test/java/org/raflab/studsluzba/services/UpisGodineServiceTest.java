package org.raflab.studsluzba.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.raflab.studsluzba.dtos.UpisGodineRequest;
import org.raflab.studsluzba.dtos.UpisGodineResponse;
import org.raflab.studsluzba.model.entities.*;
import org.raflab.studsluzba.repositories.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpisGodineServiceTest {

    @Mock UpisGodineRepository upisGodineRepository;
    @Mock StudentIndeksRepository studentIndeksRepository;
    @Mock SkolskaGodinaRepository skolskaGodinaRepository;
    @Mock SlusaPredmetRepository slusaPredmetRepository;
    @Mock DrziPredmetRepository drziPredmetRepository;

    @InjectMocks UpisGodineService service;

    @Captor ArgumentCaptor<UpisGodine> upisCaptor;
    @Captor ArgumentCaptor<Iterable<SlusaPredmet>> slusaIterableCaptor;

    // -------------------- helperi --------------------

    private static UpisGodineRequest baseReq(Long siId, Long sgId, int godinaStudija) {
        UpisGodineRequest req = new UpisGodineRequest();
        req.setStudentIndeksId(siId);
        req.setSkolskaGodinaId(sgId);
        req.setGodinaStudija(godinaStudija);
        req.setDatum(LocalDate.of(2025, 1, 10));
        req.setNapomena("test");
        return req;
    }

    private static StudentIndeks studentIndeks(Long id, String oznaka) {
        StudentIndeks si = new StudentIndeks();
        si.setId(id);
        si.setStudProgramOznaka(oznaka);
        return si;
    }

    private static SkolskaGodina skolskaGodina(Long id, String naziv) {
        SkolskaGodina sg = new SkolskaGodina();
        sg.setId(id);
        sg.setNaziv(naziv);
        return sg;
    }

    private static SlusaPredmet slusaSaId(Long id) {
        SlusaPredmet sp = new SlusaPredmet();
        sp.setId(id);
        return sp;
    }

    // BITNO: da ne dobijemo koliziju ID-eva kad je isti semestar
    private static DrziPredmet drziPredmet(long drziId, long predmetId, int semestar, String oznakaPrograma) {
        StudijskiProgram program = new StudijskiProgram();
        program.setOznaka(oznakaPrograma);

        Predmet p = new Predmet();
        p.setId(predmetId);
        p.setSifra("P" + semestar + "-" + oznakaPrograma);
        p.setNaziv("Predmet " + semestar + " " + oznakaPrograma);
        p.setSemestar(semestar);
        p.setEspb(6);
        p.setStudProgram(program);

        DrziPredmet dp = new DrziPredmet();
        dp.setId(drziId);
        dp.setPredmet(p);
        return dp;
    }

    private static List<SlusaPredmet> toList(Iterable<SlusaPredmet> it) {
        List<SlusaPredmet> out = new ArrayList<>();
        if (it != null) it.forEach(out::add);
        return out;
    }

    // -------------------- findUpisaneGodine --------------------

    @Nested
    class FindUpisaneGodine {

        @Test
        @DisplayName("Delegira na repo i mapira listu")
        void returnsMappedList() {
            String oznaka = "RI";
            int godina = 2023;
            int broj = 12;

            UpisGodine u1 = new UpisGodine(); u1.setId(1L);
            UpisGodine u2 = new UpisGodine(); u2.setId(2L);

            when(upisGodineRepository
                    .findByStudentIndeksStudProgramOznakaAndStudentIndeksGodinaAndStudentIndeksBroj(oznaka, godina, broj))
                    .thenReturn(List.of(u1, u2));

            List<UpisGodineResponse> res = service.findUpisaneGodine(oznaka, godina, broj);

            assertNotNull(res);
            assertEquals(2, res.size());
            assertEquals(1L, res.get(0).getId());
            assertEquals(2L, res.get(1).getId());
        }
    }

    // -------------------- create --------------------

    @Nested
    class Create {

        @Test
        @DisplayName("StudentIndeks ne postoji -> baca NoSuchElementException")
        void studentIndeksNotFound() {
            UpisGodineRequest req = baseReq(10L, 1L, 1);

            when(studentIndeksRepository.findById(10L)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> service.create(req));

            verify(upisGodineRepository, never()).save(any());
            verify(slusaPredmetRepository, never()).saveAll(any());
            verify(drziPredmetRepository, never()).findBySkolskaGodinaId(anyLong());
        }

        @Test
        @DisplayName("SkolskaGodina ne postoji -> baca NoSuchElementException")
        void skolskaGodinaNotFound() {
            UpisGodineRequest req = baseReq(10L, 1L, 1);

            when(studentIndeksRepository.findById(10L)).thenReturn(Optional.of(studentIndeks(10L, "RI")));
            when(skolskaGodinaRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> service.create(req));

            verify(upisGodineRepository, never()).save(any());
            verify(slusaPredmetRepository, never()).saveAll(any());
            verify(drziPredmetRepository, never()).findBySkolskaGodinaId(anyLong());
        }

        @Test
        @DisplayName("PredmetiKojePrenosiIds setuje u UpisGodine (koristi findByIdIn)")
        void withPredmetiKojePrenosi_setsSet() {
            UpisGodineRequest req = baseReq(10L, 1L, 1);
            req.setPredmetiKojePrenosiIds(Set.of(100L, 101L));

            StudentIndeks si = studentIndeks(10L, "RI");
            SkolskaGodina sg = skolskaGodina(1L, "2024/2025.");

            when(studentIndeksRepository.findById(10L)).thenReturn(Optional.of(si));
            when(skolskaGodinaRepository.findById(1L)).thenReturn(Optional.of(sg));

            when(slusaPredmetRepository.findByIdIn(Set.of(100L, 101L)))
                    .thenReturn(List.of(slusaSaId(100L), slusaSaId(101L)));

            when(drziPredmetRepository.findBySkolskaGodinaId(1L)).thenReturn(Collections.emptyList());

            UpisGodine saved = new UpisGodine();
            saved.setId(555L);
            when(upisGodineRepository.save(any(UpisGodine.class))).thenReturn(saved);

            UpisGodineResponse resp = service.create(req);
            assertNotNull(resp);

            verify(upisGodineRepository).save(upisCaptor.capture());
            UpisGodine toSave = upisCaptor.getValue();

            assertEquals(si, toSave.getStudentIndeks());
            assertEquals(sg, toSave.getSkolskaGodina());
            assertEquals(req.getGodinaStudija(), toSave.getGodinaStudija());
            assertEquals(req.getDatum(), toSave.getDatum());
            assertEquals(req.getNapomena(), toSave.getNapomena());

            assertNotNull(toSave.getPredmetiKojePrenosi());
            assertEquals(2, toSave.getPredmetiKojePrenosi().size());

            verify(slusaPredmetRepository, never()).saveAll(any());
        }

        @Test
        @DisplayName("Kad ima predmeta za godinu i program -> napravi SlusaPredmet za svaki i pozove saveAll")
        void createsSlusaPredmetForMatchingDrziPredmet() {
            UpisGodineRequest req = baseReq(10L, 1L, 2); // godina 2 => semestar 3/4

            StudentIndeks si = studentIndeks(10L, "RI");
            SkolskaGodina sg = skolskaGodina(1L, "2024/2025.");

            when(studentIndeksRepository.findById(10L)).thenReturn(Optional.of(si));
            when(skolskaGodinaRepository.findById(1L)).thenReturn(Optional.of(sg));

            UpisGodine saved = new UpisGodine();
            saved.setId(700L);
            when(upisGodineRepository.save(any(UpisGodine.class))).thenReturn(saved);

            // UNIQUE ID-evi (drziId, predmetId) da izbjegnemo sudare
            DrziPredmet dp1 = drziPredmet(2003L, 1003L, 3, "RI");
            DrziPredmet dp2 = drziPredmet(2004L, 1004L, 4, "RI");
            DrziPredmet dpOtherYear = drziPredmet(2001L, 1001L, 1, "RI"); // godinaPredmeta=1
            DrziPredmet dpOtherProg = drziPredmet(3003L, 2003L, 3, "SI"); // isti semestar ali DRUGI ID

            when(drziPredmetRepository.findBySkolskaGodinaId(1L))
                    .thenReturn(List.of(dp1, dp2, dpOtherYear, dpOtherProg));

            UpisGodineResponse resp = service.create(req);
            assertNotNull(resp);

            verify(slusaPredmetRepository).saveAll(slusaIterableCaptor.capture());
            List<SlusaPredmet> savedSlusa = toList(slusaIterableCaptor.getValue());

            assertEquals(2, savedSlusa.size());

            Set<Long> dpIds = savedSlusa.stream()
                    .map(sp -> sp.getDrziPredmet().getId())
                    .collect(Collectors.toSet());

            assertTrue(dpIds.contains(dp1.getId()));
            assertTrue(dpIds.contains(dp2.getId()));
            assertFalse(dpIds.contains(dpOtherYear.getId()));
            assertFalse(dpIds.contains(dpOtherProg.getId()));

            for (SlusaPredmet sp : savedSlusa) {
                assertEquals(si, sp.getStudentIndeks());
                assertEquals(sg, sp.getSkolskaGodina());
                assertNull(sp.getGrupa());
                assertNotNull(sp.getDrziPredmet());
            }
        }

        @Test
        @DisplayName("Kad nema predmeta za godinu -> ne poziva saveAll")
        void noPredmetiZaGodinu_noSaveAll() {
            UpisGodineRequest req = baseReq(10L, 1L, 3); // godina 3 => semestar 5/6

            StudentIndeks si = studentIndeks(10L, "RI");
            SkolskaGodina sg = skolskaGodina(1L, "2024/2025.");

            when(studentIndeksRepository.findById(10L)).thenReturn(Optional.of(si));
            when(skolskaGodinaRepository.findById(1L)).thenReturn(Optional.of(sg));

            UpisGodine saved = new UpisGodine();
            saved.setId(701L);
            when(upisGodineRepository.save(any(UpisGodine.class))).thenReturn(saved);

            when(drziPredmetRepository.findBySkolskaGodinaId(1L)).thenReturn(List.of(
                    drziPredmet(2101L, 1101L, 1, "RI"),
                    drziPredmet(3105L, 2105L, 5, "SI")
            ));

            UpisGodineResponse resp = service.create(req);
            assertNotNull(resp);

            verify(slusaPredmetRepository, never()).saveAll(any());
        }
    }
}
