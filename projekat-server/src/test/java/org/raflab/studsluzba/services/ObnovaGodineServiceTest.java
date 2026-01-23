package org.raflab.studsluzba.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.raflab.studsluzba.model.entities.*;
import org.raflab.studsluzba.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObnovaGodineServiceTest {

    @Mock private SkolskaGodinaRepository skolskaGodinaRepository;
    @Mock private SlusaPredmetRepository slusaPredmetRepository;
    @Mock private ObnovaGodineRepository obnovaGodineRepository;
    @Mock private StudentIndeksRepository studentIndeksRepository;

    @InjectMocks
    private ObnovaGodineService service;

    // -------------------- addObnovaGodineNarednaGodina --------------------

    @Test
    void addObnovaGodineNarednaGodina_studentNotFound_throws404() {
        when(studentIndeksRepository.findById(10L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                service.addObnovaGodineNarednaGodina(
                        10L, 1L,
                        Set.of(100L), Set.of(200L),
                        2, "napomena", LocalDate.now()
                )
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getReason().toLowerCase().contains("studentindeks"));

        verify(obnovaGodineRepository, never()).save(any());
    }

    @Test
    void addObnovaGodineNarednaGodina_skolskaGodinaNotFound_throws404() {
        when(studentIndeksRepository.findById(10L)).thenReturn(Optional.of(new StudentIndeks()));
        when(skolskaGodinaRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                service.addObnovaGodineNarednaGodina(
                        10L, 99L,
                        Set.of(100L), Set.of(200L),
                        2, "napomena", LocalDate.now()
                )
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        assertTrue(ex.getReason().toLowerCase().contains("skolskagodina"));

        verify(obnovaGodineRepository, never()).save(any());
    }

    @Test
    void addObnovaGodineNarednaGodina_whenEspbOver60_throws400() {
        StudentIndeks si = new StudentIndeks();
        si.setId(10L);
        when(studentIndeksRepository.findById(10L)).thenReturn(Optional.of(si));

        SkolskaGodina sg = new SkolskaGodina();
        sg.setId(1L);
        when(skolskaGodinaRepository.findById(1L)).thenReturn(Optional.of(sg));

        // 30 + 31 = 61
        List<SlusaPredmet> prethodni = List.of(slusaSaEspb(100L, 30), slusaSaEspb(101L, 31));
        when(slusaPredmetRepository.findByIdIn(Set.of(100L, 101L))).thenReturn(prethodni);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                service.addObnovaGodineNarednaGodina(
                        10L, 1L,
                        Set.of(100L, 101L), null,
                        2, "napomena", LocalDate.now()
                )
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertTrue(ex.getReason().contains("ESPB"));

        verify(obnovaGodineRepository, never()).save(any());
    }

    @Test
    void addObnovaGodineNarednaGodina_whenEspbExactly60_savesAndReturnsId() {
        StudentIndeks si = new StudentIndeks();
        si.setId(10L);
        when(studentIndeksRepository.findById(10L)).thenReturn(Optional.of(si));

        SkolskaGodina sg = new SkolskaGodina();
        sg.setId(1L);
        when(skolskaGodinaRepository.findById(1L)).thenReturn(Optional.of(sg));

        when(slusaPredmetRepository.findByIdIn(Set.of(100L))).thenReturn(List.of(slusaSaEspb(100L, 30)));
        when(slusaPredmetRepository.findByIdIn(Set.of(200L))).thenReturn(List.of(slusaSaEspb(200L, 30)));

        ObnovaGodine saved = new ObnovaGodine();
        saved.setId(555L);
        when(obnovaGodineRepository.save(any(ObnovaGodine.class))).thenReturn(saved);

        Long id = service.addObnovaGodineNarednaGodina(
                10L, 1L,
                Set.of(100L), Set.of(200L),
                2, "napomena", LocalDate.of(2025, 1, 1)
        );

        assertEquals(555L, id);

        ArgumentCaptor<ObnovaGodine> captor = ArgumentCaptor.forClass(ObnovaGodine.class);
        verify(obnovaGodineRepository).save(captor.capture());

        ObnovaGodine toSave = captor.getValue();
        assertEquals(si, toSave.getStudentIndeks());
        assertEquals(sg, toSave.getSkolskaGodina());
        assertEquals(2, toSave.getGodinaStudija());
        assertEquals(LocalDate.of(2025, 1, 1), toSave.getDatum());
        assertEquals("napomena", toSave.getNapomena());
        assertNotNull(toSave.getPredmetiKojeObnavlja());
        assertEquals(2, toSave.getPredmetiKojeObnavlja().size());
    }

    @Test
    void addObnovaGodineNarednaGodina_whenNoPredmeti_setsEmptyAndSaves() {
        StudentIndeks si = new StudentIndeks();
        si.setId(10L);
        when(studentIndeksRepository.findById(10L)).thenReturn(Optional.of(si));

        SkolskaGodina sg = new SkolskaGodina();
        sg.setId(1L);
        when(skolskaGodinaRepository.findById(1L)).thenReturn(Optional.of(sg));

        ObnovaGodine saved = new ObnovaGodine();
        saved.setId(123L);
        when(obnovaGodineRepository.save(any(ObnovaGodine.class))).thenReturn(saved);

        Long id = service.addObnovaGodineNarednaGodina(
                10L, 1L,
                null, null,
                2, "napomena", LocalDate.of(2025, 1, 1)
        );

        assertEquals(123L, id);

        ArgumentCaptor<ObnovaGodine> captor = ArgumentCaptor.forClass(ObnovaGodine.class);
        verify(obnovaGodineRepository).save(captor.capture());

        ObnovaGodine toSave = captor.getValue();
        assertNotNull(toSave.getPredmetiKojeObnavlja());
        assertEquals(0, toSave.getPredmetiKojeObnavlja().size());
    }

    @Test
    void addObnovaGodineNarednaGodina_whenSameSlusaInBothSets_deduplicates() {
        StudentIndeks si = new StudentIndeks(); si.setId(10L);
        when(studentIndeksRepository.findById(10L)).thenReturn(Optional.of(si));

        SkolskaGodina sg = new SkolskaGodina(); sg.setId(1L);
        when(skolskaGodinaRepository.findById(1L)).thenReturn(Optional.of(sg));

        SlusaPredmet sp = slusaSaEspb(100L, 30);

        when(slusaPredmetRepository.findByIdIn(Set.of(100L))).thenReturn(List.of(sp));
        when(slusaPredmetRepository.findByIdIn(Set.of(200L))).thenReturn(List.of(sp)); // isti slusa (isti id)

        ObnovaGodine saved = new ObnovaGodine(); saved.setId(777L);
        when(obnovaGodineRepository.save(any())).thenReturn(saved);

        Long id = service.addObnovaGodineNarednaGodina(
                10L, 1L,
                Set.of(100L), Set.of(200L),
                2, null, LocalDate.now()
        );

        assertEquals(777L, id);

        ArgumentCaptor<ObnovaGodine> captor = ArgumentCaptor.forClass(ObnovaGodine.class);
        verify(obnovaGodineRepository).save(captor.capture());
        assertEquals(1, captor.getValue().getPredmetiKojeObnavlja().size());
    }

    @Test
    void addObnovaGodineNarednaGodina_when60AcrossBothSets_saves() {
        StudentIndeks si = new StudentIndeks(); si.setId(10L);
        when(studentIndeksRepository.findById(10L)).thenReturn(Optional.of(si));

        SkolskaGodina sg = new SkolskaGodina(); sg.setId(1L);
        when(skolskaGodinaRepository.findById(1L)).thenReturn(Optional.of(sg));

        when(slusaPredmetRepository.findByIdIn(Set.of(100L, 101L)))
                .thenReturn(List.of(slusaSaEspb(100L, 40), slusaSaEspb(101L, 10)));
        when(slusaPredmetRepository.findByIdIn(Set.of(200L)))
                .thenReturn(List.of(slusaSaEspb(200L, 10)));

        ObnovaGodine saved = new ObnovaGodine(); saved.setId(999L);
        when(obnovaGodineRepository.save(any())).thenReturn(saved);

        Long id = service.addObnovaGodineNarednaGodina(
                10L, 1L,
                Set.of(100L, 101L), Set.of(200L),
                2, "x", LocalDate.now()
        );

        assertEquals(999L, id);
        verify(obnovaGodineRepository).save(any());
    }

    // -------------------- helper --------------------

    private static SlusaPredmet slusaSaEspb(Long slusaId, int espb) {
        StudijskiProgram sp = new StudijskiProgram();
        sp.setOznaka("RI");

        Predmet p = new Predmet();
        p.setId(999L);
        p.setSifra("X");
        p.setNaziv("X");
        p.setEspb(espb);
        p.setSemestar(1);
        p.setStudProgram(sp);

        DrziPredmet dp = new DrziPredmet();
        dp.setId(888L);
        dp.setPredmet(p);

        SlusaPredmet slusa = new SlusaPredmet();
        slusa.setId(slusaId);
        slusa.setDrziPredmet(dp);
        return slusa;
    }
}
