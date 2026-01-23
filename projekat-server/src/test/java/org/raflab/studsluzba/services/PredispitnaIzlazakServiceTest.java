package org.raflab.studsluzba.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.raflab.studsluzba.dtos.PredispitnaIzlazakRequest;
import org.raflab.studsluzba.model.entities.PredispitnaIzlazak;
import org.raflab.studsluzba.model.entities.PredispitnaObaveza;
import org.raflab.studsluzba.model.entities.SlusaPredmet;
import org.raflab.studsluzba.repositories.PredispitnaIzlazakRepository;
import org.raflab.studsluzba.repositories.PredispitnaObavezaRepository;
import org.raflab.studsluzba.repositories.SlusaPredmetRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PredispitnaIzlazakServiceTest {

    @Mock PredispitnaIzlazakRepository izlazakRepository;
    @Mock SlusaPredmetRepository slusaPredmetRepository;
    @Mock PredispitnaObavezaRepository predispitnaObavezaRepository;

    @InjectMocks PredispitnaIzlazakService service;

    @Captor ArgumentCaptor<PredispitnaIzlazak> izlazakCaptor;

    // -------- helperi --------

    private static SlusaPredmet slusaPredmet(Long id) {
        SlusaPredmet sp = new SlusaPredmet();
        sp.setId(id);
        return sp;
    }

    private static PredispitnaObaveza obaveza(Long id) {
        PredispitnaObaveza po = new PredispitnaObaveza();
        po.setId(id);
        po.setVrsta("Kolokvijum");
        po.setMaxPoena(50);
        return po;
    }

    private static PredispitnaIzlazakRequest req(Long spId, Long poId, int poeni) {
        PredispitnaIzlazakRequest r = new PredispitnaIzlazakRequest();
        r.setSlusaPredmetId(spId);
        r.setPredispitnaObavezaId(poId);
        r.setPoeni(poeni);
        r.setDatum(LocalDate.of(2026, 1, 15));
        return r;
    }

    // -------- addPredispitnaIzlazak --------

    @Test
    @DisplayName("addPredispitnaIzlazak -> učita sp + po, mapira preko Converters i snimi")
    void addPredispitnaIzlazak_saves() {
        SlusaPredmet sp = slusaPredmet(10L);
        PredispitnaObaveza po = obaveza(20L);

        when(slusaPredmetRepository.findById(10L)).thenReturn(Optional.of(sp));
        when(predispitnaObavezaRepository.findById(20L)).thenReturn(Optional.of(po));

        PredispitnaIzlazak saved = new PredispitnaIzlazak();
        saved.setId(99L);
        when(izlazakRepository.save(any(PredispitnaIzlazak.class))).thenReturn(saved);

        Long outId = service.addPredispitnaIzlazak(req(10L, 20L, 37));
        assertEquals(99L, outId);

        verify(izlazakRepository).save(izlazakCaptor.capture());
        PredispitnaIzlazak toSave = izlazakCaptor.getValue();

        assertEquals(sp, toSave.getSlusaPredmet());
        assertEquals(po, toSave.getPredispitnaObaveza());
        assertEquals(37, toSave.getPoeni());
        assertEquals(LocalDate.of(2026, 1, 15), toSave.getDatum());
    }

    // -------- findById / findAll --------

    @Test
    @DisplayName("findById -> vraća entitet ako postoji")
    void findById_found() {
        PredispitnaIzlazak pi = new PredispitnaIzlazak();
        pi.setId(1L);
        when(izlazakRepository.findById(1L)).thenReturn(Optional.of(pi));

        PredispitnaIzlazak out = service.findById(1L);

        assertNotNull(out);
        assertEquals(1L, out.getId());
        verify(izlazakRepository).findById(1L);
    }

    @Test
    @DisplayName("findById -> vraća null ako ne postoji")
    void findById_missing_returnsNull() {
        when(izlazakRepository.findById(123L)).thenReturn(Optional.empty());

        PredispitnaIzlazak out = service.findById(123L);

        assertNull(out);
        verify(izlazakRepository).findById(123L);
    }

    @Test
    @DisplayName("findAll -> delegira na repo")
    void findAll_delegates() {
        PredispitnaIzlazak a = new PredispitnaIzlazak(); a.setId(1L);
        PredispitnaIzlazak b = new PredispitnaIzlazak(); b.setId(2L);

        when(izlazakRepository.findAll()).thenReturn(List.of(a, b));

        List<PredispitnaIzlazak> out = service.findAll();

        assertEquals(2, out.size());
        assertEquals(1L, out.get(0).getId());
        assertEquals(2L, out.get(1).getId());
        verify(izlazakRepository).findAll();
    }

    // -------- deleteById --------

    @Nested
    class DeleteTests {

        @Test
        @DisplayName("deleteById -> ako ne postoji -> 404")
        void delete_notFound_404() {
            when(izlazakRepository.existsById(10L)).thenReturn(false);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.deleteById(10L));

            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
            verify(izlazakRepository, never()).deleteById(anyLong());
        }

        @Test
        @DisplayName("deleteById -> happy path -> briše")
        void delete_ok() {
            when(izlazakRepository.existsById(10L)).thenReturn(true);

            service.deleteById(10L);

            verify(izlazakRepository).deleteById(10L);
        }

        @Test
        @DisplayName("deleteById -> DataIntegrityViolation -> 409")
        void delete_conflict_409() {
            when(izlazakRepository.existsById(10L)).thenReturn(true);
            doThrow(new DataIntegrityViolationException("fk"))
                    .when(izlazakRepository).deleteById(10L);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.deleteById(10L));

            assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        }
    }
}
