package org.raflab.studsluzba.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.raflab.studsluzba.dtos.NastavnikZvanjeRequest;
import org.raflab.studsluzba.model.entities.Nastavnik;
import org.raflab.studsluzba.model.entities.NastavnikZvanje;
import org.raflab.studsluzba.repositories.DrziPredmetRepository;
import org.raflab.studsluzba.repositories.IspitRepository;
import org.raflab.studsluzba.repositories.NastavnikRepository;
import org.raflab.studsluzba.repositories.NastavnikZvanjeRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NastavnikServiceTest {

    @Mock NastavnikRepository nastavnikRepository;
    @Mock NastavnikZvanjeRepository nastavnikZvanjeRepository;
    @Mock DrziPredmetRepository drziPredmetRepository;
    @Mock IspitRepository ispitRepository;

    @InjectMocks NastavnikService nastavnikService;

    @InjectMocks NastavnikZvanjeService nastavnikZvanjeService;

    private static Nastavnik nastavnikEntity() {
        Nastavnik n = new Nastavnik();
        n.setIme("Pera");
        n.setPrezime("Perić");
        n.setSrednjeIme("M");
        n.setEmail("pera@raf.rs");
        n.setDatumRodjenja(LocalDate.of(1980, 1, 1));
        n.setPol('M');
        n.setJmbg("1234567890123");
        return n;
    }

    private static NastavnikZvanjeRequest zvanjeReq(Long nastavnikId) {
        NastavnikZvanjeRequest r = new NastavnikZvanjeRequest();
        r.setNastavnikId(nastavnikId);
        r.setDatumIzbora(LocalDate.of(2024, 10, 1));
        r.setZvanje("Docent");
        r.setNaucnaOblast("Računarske nauke");
        r.setUzaNaucnaOblast("Softversko inženjerstvo");
        r.setAktivno(true);
        return r;
    }

    @Test
    @DisplayName("Combo: addNastavnik pa addZvanje (bez izmjene klasa) -> oba save + veza preko nastavnikId")
    void combo_addNastavnik_then_addZvanje() {
        // 1) stub save nastavnika
        Nastavnik savedN = nastavnikEntity();
        savedN.setId(10L);
        when(nastavnikRepository.save(any(Nastavnik.class))).thenReturn(savedN);

        // 2) stub findById u zvanjeService (on trazi nastavnika po id)
        when(nastavnikRepository.findById(10L)).thenReturn(Optional.of(savedN));

        // 3) stub save zvanja
        NastavnikZvanje savedZ = new NastavnikZvanje();
        savedZ.setId(99L);
        when(nastavnikZvanjeRepository.save(any(NastavnikZvanje.class))).thenReturn(savedZ);

        // ACT: "combo" flow
        Long nastavnikId = nastavnikService.addNastavnik(nastavnikEntity());
        Long zvanjeId = nastavnikZvanjeService.add(zvanjeReq(nastavnikId));

        // ASSERT
        assertEquals(10L, nastavnikId);
        assertEquals(99L, zvanjeId);

        verify(nastavnikRepository, times(1)).save(any(Nastavnik.class));
        verify(nastavnikRepository, times(1)).findById(10L);
        verify(nastavnikZvanjeRepository, times(1)).save(any(NastavnikZvanje.class));

        verifyNoMoreInteractions(nastavnikZvanjeRepository);
    }
}
