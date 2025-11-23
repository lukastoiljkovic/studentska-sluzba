package org.raflab.studsluzba.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.raflab.studsluzba.controllers.request.DrziPredmetNewRequest;
import org.raflab.studsluzba.controllers.request.DrziPredmetRequest;
import org.raflab.studsluzba.model.entities.DrziPredmet;
import org.raflab.studsluzba.model.entities.Nastavnik;
import org.raflab.studsluzba.model.entities.Predmet;
import org.raflab.studsluzba.repositories.DrziPredmetRepository;
import org.raflab.studsluzba.repositories.NastavnikRepository;
import org.raflab.studsluzba.repositories.PredmetRepository;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DrziPredmetServiceTest {

    @Mock DrziPredmetRepository drziPredmetRepository;
    @Mock PredmetRepository predmetRepository;
    @Mock NastavnikRepository nastavnikRepository;

    @InjectMocks DrziPredmetService service;

    @Captor ArgumentCaptor<List<DrziPredmet>> drziPredmetListCaptor;

    private Predmet predmet(long id, String naziv) {
        Predmet p = new Predmet();
        p.setId(id);
        p.setNaziv(naziv);
        return p;
    }

    private Nastavnik nastavnik(String email) {
        Nastavnik n = new Nastavnik();
        n.setEmail(email);
        return n;
    }

    private DrziPredmetNewRequest reqById(long predmetId, String email) {
        DrziPredmetNewRequest r = new DrziPredmetNewRequest();
        r.setPredmetId(predmetId);
        r.setEmailNastavnik(email);
        r.setPredmetNaziv(""); // nije bitno u ovom slučaju
        return r;
    }

    private DrziPredmetNewRequest reqByNaziv(String predmetNaziv, String email) {
        DrziPredmetNewRequest r = new DrziPredmetNewRequest();
        r.setPredmetNaziv(predmetNaziv);
        r.setEmailNastavnik(email);
        return r;
    }

    private DrziPredmetRequest makeRequest(List<DrziPredmetNewRequest> existing,
                                           List<DrziPredmetNewRequest> fresh) {
        DrziPredmetRequest r = new DrziPredmetRequest();
        r.setDrziPredmet(existing != null ? existing : new ArrayList<>());
        r.setNewDrziPredmet(fresh != null ? fresh : new ArrayList<>());
        return r;
    }

    @Nested
    class HappyPath {

        @Test
        @DisplayName("Saves valid pairs from both existing and new predmet lists")
        void savesValidPairs() {
            var existing = List.of(reqById(10L, "prof1@raf.rs"));
            var fresh    = List.of(reqByNaziv("Matematika 1", "prof2@raf.rs"));

            var request = makeRequest(existing, fresh);

            when(predmetRepository.findByIdIn(List.of(10L)))
                    .thenReturn(List.of(predmet(10L, "Programiranje 1")));
            when(predmetRepository.findByNazivIn(List.of("Matematika 1")))
                    .thenReturn(List.of(predmet(20L, "Matematika 1")));

            when(nastavnikRepository.findByEmailIn(
                    argThat(emails -> emails.containsAll(Set.of("prof1@raf.rs", "prof2@raf.rs"))
                            && emails.size() == 2)))
                    .thenReturn(List.of(nastavnik("prof1@raf.rs"), nastavnik("prof2@raf.rs")));

            service.saveDrziPredmet(request);

            verify(predmetRepository).findByIdIn(List.of(10L));
            verify(predmetRepository).findByNazivIn(List.of("Matematika 1"));
            verify(nastavnikRepository).findByEmailIn(
                    argThat(list -> list.containsAll(Set.of("prof1@raf.rs", "prof2@raf.rs")))
            );
            verify(drziPredmetRepository).saveAll(drziPredmetListCaptor.capture());

            var saved = drziPredmetListCaptor.getValue();
            assertEquals(2, saved.size(), "Should save exactly two DrziPredmet entries");

            Map<String, String> mapPredmetToNastavnik = saved.stream()
                    .collect(Collectors.toMap(
                            dp -> dp.getPredmet().getNaziv(),
                            dp -> dp.getNastavnik().getEmail()
                    ));

            assertEquals("prof1@raf.rs", mapPredmetToNastavnik.get("Programiranje 1"));
            assertEquals("prof2@raf.rs", mapPredmetToNastavnik.get("Matematika 1"));
        }
    }

    @Nested
    class SkipsInvalidEntries {

        @Test
        @DisplayName("Skips when Predmet missing or Nastavnik missing")
        void skipsMissingPredmetOrNastavnik() {
            var existing = List.of(
                    reqById(1L, "a@raf.rs"),       // valid
                    reqById(2L, "missing@raf.rs")  // nastavnik missing
            );
            var fresh = List.of(
                    reqByNaziv("Unknown predmet", "a@raf.rs") // predmet by naziv not found
            );
            var request = makeRequest(existing, fresh);

            when(predmetRepository.findByIdIn(List.of(1L, 2L)))
                    .thenReturn(List.of(predmet(1L, "Algoritmi")));
            when(predmetRepository.findByNazivIn(List.of("Unknown predmet")))
                    .thenReturn(List.of());
            when(nastavnikRepository.findByEmailIn(List.of("a@raf.rs", "missing@raf.rs")))
                    .thenReturn(List.of(nastavnik("a@raf.rs")));

            service.saveDrziPredmet(request);

            verify(drziPredmetRepository).saveAll(drziPredmetListCaptor.capture());
            var saved = drziPredmetListCaptor.getValue();

            assertEquals(1, saved.size());
            assertEquals("Algoritmi", saved.get(0).getPredmet().getNaziv());
            assertEquals("a@raf.rs", saved.get(0).getNastavnik().getEmail());
        }

        @Test
        @DisplayName("Empty lists → still calls saveAll with empty list")
        void handlesEmpty() {
            var request = makeRequest(Collections.emptyList(), Collections.emptyList());

            when(predmetRepository.findByIdIn(Collections.emptyList())).thenReturn(Collections.emptyList());
            when(predmetRepository.findByNazivIn(Collections.emptyList())).thenReturn(Collections.emptyList());
            when(nastavnikRepository.findByEmailIn(Collections.emptyList())).thenReturn(Collections.emptyList());

            service.saveDrziPredmet(request);

            verify(drziPredmetRepository).saveAll(drziPredmetListCaptor.capture());
            assertTrue(drziPredmetListCaptor.getValue().isEmpty());
        }
    }
}