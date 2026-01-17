package org.raflab.studsluzba.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.raflab.studsluzba.model.entities.StudentIndeks;
import org.raflab.studsluzba.repositories.StudentIndeksRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentIndeksServiceTest {

    @Mock StudentIndeksRepository studentIndeksRepository;
    @Mock StudijskiProgramiService studijskiProgramiService; // nije korišćen ovde, ali servis ga ima u konstruktoru

    @InjectMocks StudentIndeksService service;

    @Nested
    class FindBrojTests {

        @Test
        @DisplayName("Empty list → returns 1")
        void emptyListReturns1() {
            when(studentIndeksRepository.findBrojeviByGodinaAndStudProgramOznaka(2025, "RN"))
                    .thenReturn(List.of());

            int next = service.findBroj(2025, "RN");
            assertEquals(1, next);
        }

        @Test
        @DisplayName("Contiguous sequence starting at 1 → returns last+1")
        void contiguousReturnsNext() {
            when(studentIndeksRepository.findBrojeviByGodinaAndStudProgramOznaka(2025, "RN"))
                    .thenReturn(List.of(1, 2, 3));

            int next = service.findBroj(2025, "RN");
            assertEquals(4, next);
        }

        @Test
        @DisplayName("Gap in the middle → returns first missing")
        void gapInMiddle() {
            when(studentIndeksRepository.findBrojeviByGodinaAndStudProgramOznaka(2025, "RN"))
                    .thenReturn(List.of(1, 2, 4, 5));

            int next = service.findBroj(2025, "RN");
            assertEquals(3, next);
        }

        @Test
        @DisplayName("Sequence not starting at 1 → returns 1")
        void notStartingAtOne() {
            when(studentIndeksRepository.findBrojeviByGodinaAndStudProgramOznaka(2025, "RN"))
                    .thenReturn(List.of(2, 3, 4));

            int next = service.findBroj(2025, "RN");
            assertEquals(1, next);
        }

        @Test
        @DisplayName("Unordered input → still works (sorts internally)")
        void unorderedInput() {
            when(studentIndeksRepository.findBrojeviByGodinaAndStudProgramOznaka(2025, "RN"))
                    .thenReturn(List.of(5, 3, 1, 2));

            int next = service.findBroj(2025, "RN");
            assertEquals(4, next);
        }
    }

    @Test
    @DisplayName("findByStudentIdAndAktivan delegates to repository and returns entity")
    void findByStudentIdAndAktivan() {
        Long studentId = 42L;
        StudentIndeks indeks = new StudentIndeks();
        indeks.setId(7L);

        when(studentIndeksRepository.findAktivanStudentIndeksiByStudentPodaciId(studentId))
                .thenReturn(indeks);

        StudentIndeks out = service.findByStudentIdAndAktivan(studentId);

        assertNotNull(out);
        assertEquals(7L, out.getId());
        verify(studentIndeksRepository).findAktivanStudentIndeksiByStudentPodaciId(studentId);
        verifyNoMoreInteractions(studentIndeksRepository);
    }
}