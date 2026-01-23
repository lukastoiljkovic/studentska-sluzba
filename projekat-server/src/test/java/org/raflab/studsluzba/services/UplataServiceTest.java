package org.raflab.studsluzba.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.raflab.studsluzba.model.entities.UpisGodine;
import org.raflab.studsluzba.model.entities.Uplata;
import org.raflab.studsluzba.repositories.UpisGodineRepository;
import org.raflab.studsluzba.repositories.UplataRepository;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UplataServiceTest {

    @Mock UplataRepository uplataRepository;
    @Mock UpisGodineRepository upisGodineRepository;

    @Mock RestTemplate restTemplate;

    UplataService service;

    @Captor ArgumentCaptor<Uplata> uplataCaptor;

    private static final String KURS_URL = "https://kurs.resenje.org/api/v1/currencies/eur/rates/today";

    private void initService() {
        service = new UplataService(uplataRepository, upisGodineRepository);

        // Ako ovo pukne zbog "final", onda moraš refaktor da RestTemplate bude injected (gore).
        ReflectionTestUtils.setField(service, "restTemplate", restTemplate);
    }

    private static UpisGodine upis(Long id) {
        UpisGodine u = new UpisGodine();
        u.setId(id);
        return u;
    }

    private static Uplata uplata(double eur, double rsd, double kurs) {
        Uplata u = new Uplata();
        u.setIznosEUR(eur);
        u.setIznosRSD(rsd);
        u.setKurs(kurs);
        u.setDatum(LocalDate.of(2025, 1, 1));
        return u;
    }

    // -------------------- dodajUplatu --------------------

    @Nested
    class DodajUplatu {

        @Test
        @DisplayName("Upis godine ne postoji -> 404")
        void upisNotFound_throws404() {
            initService();

            when(upisGodineRepository.findById(10L)).thenReturn(Optional.empty());

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.dodajUplatu(10L, 100.0));

            assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
            assertNotNull(ex.getReason());
            assertTrue(ex.getReason().toLowerCase().contains("upis"));

            verify(uplataRepository, never()).save(any());
        }

        @Test
        @DisplayName("API vrati null -> 503")
        void apiReturnsNull_throws503() {
            initService();

            when(upisGodineRepository.findById(1L)).thenReturn(Optional.of(upis(1L)));
            when(restTemplate.getForObject(KURS_URL, Map.class)).thenReturn(null);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.dodajUplatu(1L, 50.0));

            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatus());
            assertNotNull(ex.getReason());

            verify(uplataRepository, never()).save(any());
        }

        @Test
        @DisplayName("API nema exchange_middle -> 503")
        void missingExchangeMiddle_throws503() {
            initService();

            when(upisGodineRepository.findById(1L)).thenReturn(Optional.of(upis(1L)));
            when(restTemplate.getForObject(KURS_URL, Map.class)).thenReturn(Map.of("x", 1));

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.dodajUplatu(1L, 50.0));

            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatus());
            assertNotNull(ex.getReason());

            verify(uplataRepository, never()).save(any());
        }

        @Test
        @DisplayName("exchange_middle je Number -> snimi uplatu sa iznosRSD=iznosEUR*kurs")
        void exchangeMiddleNumber_savesCorrectly() {
            initService();

            UpisGodine upis = upis(5L);
            when(upisGodineRepository.findById(5L)).thenReturn(Optional.of(upis));

            when(restTemplate.getForObject(KURS_URL, Map.class))
                    .thenReturn(Map.of("exchange_middle", 117.5));

            Uplata saved = new Uplata();
            saved.setId(123); // int!
            when(uplataRepository.save(any(Uplata.class))).thenReturn(saved);

            Uplata out = service.dodajUplatu(5L, 10.0);

            assertNotNull(out);
            assertEquals(123, out.getId()); // int!

            verify(uplataRepository).save(uplataCaptor.capture());
            Uplata toSave = uplataCaptor.getValue();

            assertEquals(upis, toSave.getUpisGodine());
            assertEquals(10.0, toSave.getIznosEUR());
            assertEquals(117.5, toSave.getKurs());
            assertEquals(10.0 * 117.5, toSave.getIznosRSD(), 0.000001);
            assertEquals(LocalDate.now(), toSave.getDatum());
        }

        @Test
        @DisplayName("exchange_middle je String -> parsira i snimi uplatu")
        void exchangeMiddleString_parsesAndSaves() {
            initService();

            UpisGodine upis = upis(7L);
            when(upisGodineRepository.findById(7L)).thenReturn(Optional.of(upis));

            when(restTemplate.getForObject(KURS_URL, Map.class))
                    .thenReturn(Map.of("exchange_middle", "120.25"));

            when(uplataRepository.save(any(Uplata.class)))
                    .thenAnswer(inv -> inv.getArgument(0, Uplata.class));

            Uplata out = service.dodajUplatu(7L, 2.0);

            assertNotNull(out);
            assertEquals(120.25, out.getKurs());
            assertEquals(2.0 * 120.25, out.getIznosRSD(), 0.000001);
        }

        @Test
        @DisplayName("exchange_middle je pogrešan tip -> 503")
        void exchangeMiddleInvalidType_throws503() {
            initService();

            when(upisGodineRepository.findById(1L)).thenReturn(Optional.of(upis(1L)));

            Map<String, Object> resp = new HashMap<>();
            resp.put("exchange_middle", List.of(1, 2, 3));
            when(restTemplate.getForObject(KURS_URL, Map.class)).thenReturn(resp);

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.dodajUplatu(1L, 10.0));

            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatus());
            assertNotNull(ex.getReason());
            verify(uplataRepository, never()).save(any());
        }

        @Test
        @DisplayName("RestTemplate baci exception -> wrapuje u 503")
        void restTemplateThrows_throws503() {
            initService();

            when(upisGodineRepository.findById(1L)).thenReturn(Optional.of(upis(1L)));
            when(restTemplate.getForObject(KURS_URL, Map.class)).thenThrow(new RuntimeException("boom"));

            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> service.dodajUplatu(1L, 10.0));

            assertEquals(HttpStatus.SERVICE_UNAVAILABLE, ex.getStatus());
            assertNotNull(ex.getReason());
            verify(uplataRepository, never()).save(any());
        }
    }

    // -------------------- preostaliIznosEUR --------------------

    @Nested
    class PreostaliIznosEUR {

        @Test
        @DisplayName("Nema uplata -> 3000")
        void noPayments_returns3000() {
            initService();

            when(uplataRepository.findByUpisGodineId(1L)).thenReturn(List.of());

            double out = service.preostaliIznosEUR(1L);
            assertEquals(3000.0, out, 0.000001);
        }

        @Test
        @DisplayName("Suma EUR iz iznosEUR + fallback iz RSD/kurs -> oduzme od 3000")
        void sumsMixedPayments_correct() {
            initService();

            Uplata u1 = new Uplata();
            u1.setIznosEUR(100.0);
            u1.setIznosRSD(11750.0);
            u1.setKurs(117.5);

            Uplata u2 = new Uplata();
            u2.setIznosEUR(null);
            u2.setIznosRSD(11750.0);
            u2.setKurs(117.5);

            when(uplataRepository.findByUpisGodineId(5L)).thenReturn(List.of(u1, u2));

            double out = service.preostaliIznosEUR(5L);
            assertEquals(2800.0, out, 0.000001);
        }

        @Test
        @DisplayName("Ako je preplaćeno -> vraća 0")
        void overpaid_returns0() {
            initService();

            Uplata u1 = new Uplata();
            u1.setIznosEUR(4000.0);
            u1.setIznosRSD(0.0);
            u1.setKurs(120.0);

            when(uplataRepository.findByUpisGodineId(9L)).thenReturn(List.of(u1));

            double out = service.preostaliIznosEUR(9L);
            assertEquals(0.0, out, 0.000001);
        }
    }

    // -------------------- preostaliIznosRSD --------------------

    @Nested
    class PreostaliIznosRSD {

        @Test
        @DisplayName("Nema uplata -> 0")
        void noPayments_returns0() {
            initService();

            when(uplataRepository.findByUpisGodineId(1L)).thenReturn(List.of());

            double out = service.preostaliIznosRSD(1L);
            assertEquals(0.0, out, 0.000001);
        }

        @Test
        @DisplayName("Koristi poslednji kurs i sumu RSD")
        void usesLastRate_andSumsRsd() {
            initService();

            Uplata u1 = uplata(100.0, 100.0 * 117.5, 117.5);
            Uplata u2 = uplata(50.0, 50.0 * 120.0, 120.0); // last kurs = 120

            when(uplataRepository.findByUpisGodineId(2L)).thenReturn(List.of(u1, u2));

            double sumaRSD = u1.getIznosRSD() + u2.getIznosRSD();
            double expected = (3000.0 * 120.0) - sumaRSD;

            double out = service.preostaliIznosRSD(2L);
            assertEquals(expected, out, 0.000001);
        }

        @Test
        @DisplayName("Ako je preplaćeno u RSD -> vraća 0")
        void overpaid_returns0() {
            initService();

            Uplata u1 = new Uplata();
            u1.setIznosRSD(500000.0);
            u1.setKurs(120.0);

            when(uplataRepository.findByUpisGodineId(3L)).thenReturn(List.of(u1));

            double out = service.preostaliIznosRSD(3L);
            assertEquals(0.0, out, 0.000001);
        }
    }
}
