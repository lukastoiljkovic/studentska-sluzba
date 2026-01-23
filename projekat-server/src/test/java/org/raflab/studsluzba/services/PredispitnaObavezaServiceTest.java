package org.raflab.studsluzba.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.raflab.studsluzba.dtos.PredispitnaObavezaRequest;
import org.raflab.studsluzba.model.entities.PredispitnaIzlazak;
import org.raflab.studsluzba.model.entities.PredispitnaObaveza;
import org.raflab.studsluzba.model.entities.Predmet;
import org.raflab.studsluzba.repositories.PredispitnaIzlazakRepository;
import org.raflab.studsluzba.repositories.PredispitnaObavezaRepository;
import org.raflab.studsluzba.repositories.PredmetRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PredispitnaObavezaServiceTest {

    @Mock PredispitnaObavezaRepository predispitnaObavezaRepository;
    @Mock PredmetRepository predmetRepository;
    @Mock PredispitnaIzlazakRepository predispitnaIzlazakRepository;

    @InjectMocks PredispitnaObavezaService service;

    @Captor ArgumentCaptor<PredispitnaObaveza> obavezaCaptor;

    // -------- helperi --------

    private static Predmet predmet(Long id) {
        Predmet p = new Predmet();
        p.setId(id);
        p.setNaziv("Mat");
        p.setSifra("MAT-1");
        p.setEspb(6);
        p.setSemestar(1);
        p.setObavezan(true);
        return p;
    }

    private static PredispitnaObavezaRequest req(Long predmetId) {
        PredispitnaObavezaRequest r = new PredispitnaObavezaRequest();
        r.setVrsta("Kolokvijum");
        r.setMaxPoena(50);
        r.setPredmetId(predmetId);
        return r;
    }

    private static PredispitnaObaveza obaveza(Long id) {
        PredispitnaObaveza po = new PredispitnaObaveza();
        po.setId(id);
        po.setVrsta("Kolokvijum");
        po.setMaxPoena(50);
        po.setPredmet(predmet(1L));
        return po;
    }

    private static PredispitnaIzlazak izlazak(Long id, PredispitnaObaveza po) {
        PredispitnaIzlazak pi = new PredispitnaIzlazak();
        pi.setId(id);
        pi.setPredispitnaObaveza(po);
        return pi;
    }

    // -------- addPredispitnaObaveza --------

    @Test
    @DisplayName("addPredispitnaObaveza -> učita predmet, mapira preko Converters i snimi")
    void addPredispitnaObaveza_saves() {
        Predmet p = predmet(10L);
        when(predmetRepository.findById(10L)).thenReturn(Optional.of(p));

        PredispitnaObaveza saved = new PredispitnaObaveza();
        saved.setId(77L);
        when(predispitnaObavezaRepository.save(any(PredispitnaObaveza.class))).thenReturn(saved);

        Long outId = service.addPredispitnaObaveza(req(10L));
        assertEquals(77L, outId);

        verify(predispitnaObavezaRepository).save(obavezaCaptor.capture());
        PredispitnaObaveza toSave = obavezaCaptor.getValue();

        assertEquals("Kolokvijum", toSave.getVrsta());
        assertEquals(50, toSave.getMaxPoena());
        assertEquals(p, toSave.getPredmet());
    }

    // -------- findById / findAll --------

    @Test
    @DisplayName("findById delegira na repo")
    void findById_delegates() {
        PredispitnaObaveza po = new PredispitnaObaveza();
        po.setId(1L);
        when(predispitnaObavezaRepository.findById(1L)).thenReturn(Optional.of(po));

        Optional<PredispitnaObaveza> out = service.findById(1L);

        assertTrue(out.isPresent());
        assertEquals(1L, out.get().getId());
        verify(predispitnaObavezaRepository).findById(1L);
    }

    @Test
    @DisplayName("findAll delegira na repo")
    void findAll_delegates() {
        PredispitnaObaveza a = new PredispitnaObaveza(); a.setId(1L);
        PredispitnaObaveza b = new PredispitnaObaveza(); b.setId(2L);

        when(predispitnaObavezaRepository.findAll()).thenReturn(List.of(a, b));

        List<PredispitnaObaveza> out = service.findAll();

        assertEquals(2, out.size());
        verify(predispitnaObavezaRepository).findAll();
    }

    // -------- deleteById (cascade) --------

    @Nested
    class DeleteTests {

        @Test
        @DisplayName("Ako predispitna obaveza ne postoji -> 404")
        void delete_notFound_404() {
            when(predispitnaObavezaRepository.existsById(10L)).thenReturn(false);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.deleteById(10L));

            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
            verify(predispitnaObavezaRepository, never()).deleteById(anyLong());
            verifyNoInteractions(predispitnaIzlazakRepository);
        }

        @Test
        @DisplayName("Happy path: obriše sve izlazke za obavezu pa obavezu")
        void delete_cascades_izlasci_then_obaveza() {
            when(predispitnaObavezaRepository.existsById(5L)).thenReturn(true);

            PredispitnaObaveza target = obaveza(5L);
            PredispitnaObaveza other = obaveza(99L);

            PredispitnaIzlazak pi1 = izlazak(101L, target);
            PredispitnaIzlazak pi2 = izlazak(102L, target);
            PredispitnaIzlazak piOther = izlazak(999L, other);

            when(predispitnaIzlazakRepository.findAll()).thenReturn(List.of(pi1, pi2, piOther));

            service.deleteById(5L);

            verify(predispitnaIzlazakRepository).deleteById(101L);
            verify(predispitnaIzlazakRepository).deleteById(102L);
            verify(predispitnaIzlazakRepository, never()).deleteById(999L);

            verify(predispitnaObavezaRepository).deleteById(5L);
        }

        @Test
        @DisplayName("Kad nema izlazaka -> obriše samo obavezu")
        void delete_no_izlasci_deletes_only_obaveza() {
            when(predispitnaObavezaRepository.existsById(5L)).thenReturn(true);
            when(predispitnaIzlazakRepository.findAll()).thenReturn(Collections.emptyList());

            service.deleteById(5L);

            verify(predispitnaIzlazakRepository, never()).deleteById(anyLong());
            verify(predispitnaObavezaRepository).deleteById(5L);
        }

        @Test
        @DisplayName("Ako DataIntegrityViolation -> 409")
        void delete_conflict_409() {
            when(predispitnaObavezaRepository.existsById(5L)).thenReturn(true);
            when(predispitnaIzlazakRepository.findAll()).thenReturn(Collections.emptyList());

            doThrow(new DataIntegrityViolationException("fk"))
                    .when(predispitnaObavezaRepository).deleteById(5L);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.deleteById(5L));

            assertEquals(HttpStatus.CONFLICT, ex.getStatus());
            assertNotNull(ex.getReason());
        }
    }
}
