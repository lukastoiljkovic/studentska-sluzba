package org.raflab.studsluzba.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.raflab.studsluzba.dtos.StudentPodaciResponse;
import org.raflab.studsluzba.model.entities.*;
import org.raflab.studsluzba.repositories.*;
import org.raflab.studsluzba.utils.EntityMappers;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock StudentPodaciRepository studentPodaciRepository;
    @Mock StudentIndeksRepository studentIndeksRepository;
    @Mock EntityMappers entityMappers;
    @Mock PolozenPredmetRepository polozenPredmetRepository;
    @Mock IspitPrijavaRepository ispitPrijavaRepository;
    @Mock SlusaPredmetRepository slusaPredmetRepository;
    @Mock PredispitnaIzlazakRepository predispitnaIzlazakRepository;
    @Mock IspitIzlazakRepository ispitIzlazakRepository;

    @InjectMocks StudentPodaciService studentPodaciService;

    // ---------- helpers ----------
    private static StudentPodaci student(Long id) {
        StudentPodaci s = new StudentPodaci();
        s.setId(id);
        s.setIme("Ana");
        s.setPrezime("Anić");
        s.setSrednjeIme("A");
        s.setJmbg("123");
        s.setPol('Z');
        s.setDatumRodjenja(LocalDate.of(2000, 1, 1));
        s.setDrzavaRodjenja("SRB");
        s.setMestoRodjenja("BG");
        s.setDrzavljanstvo("SRB");
        s.setMestoPrebivalista("BG");
        s.setAdresaPrebivalista("Ulica 1");
        s.setEmailFakultetski("a@raf.rs");
        s.setEmailPrivatni("a@gmail.com");
        s.setBrojLicneKarte("111");
        s.setLicnuKartuIzdao("MUP");
        return s;
    }

    private static StudentIndeks indeks(Long id, StudentPodaci s) {
        StudentIndeks si = new StudentIndeks();
        si.setId(id);
        si.setStudent(s);
        return si;
    }

    private static SlusaPredmet slusaPredmet(Long id) {
        SlusaPredmet sp = new SlusaPredmet();
        sp.setId(id);
        return sp;
    }

    private static PredispitnaIzlazak predispitniIzlazak(Long id, SlusaPredmet sp) {
        PredispitnaIzlazak pi = new PredispitnaIzlazak();
        pi.setId(id);
        pi.setSlusaPredmet(sp);
        return pi;
    }

    private static IspitIzlazak ispitIzlazak(Long id) {
        IspitIzlazak ii = new IspitIzlazak();
        ii.setId(id);
        return ii;
    }

    private static IspitPrijava prijava(Long id, StudentIndeks si, IspitIzlazak izlazak) {
        IspitPrijava ip = new IspitPrijava();
        ip.setId(id);
        ip.setStudentIndeks(si);
        ip.setIspitIzlazak(izlazak);
        return ip;
    }

    private static PolozenPredmet polozen(Long id, StudentIndeks si) {
        PolozenPredmet pp = new PolozenPredmet();
        pp.setId(id);
        pp.setStudentIndeks(si);
        return pp;
    }

    // ================== TESTS ==================

    @Test
    @DisplayName("deleteById: student ne postoji -> 404")
    void deleteById_notFound_404() {
        when(studentPodaciRepository.existsById(1L)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> studentPodaciService.deleteById(1L));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
        verify(studentPodaciRepository, never()).deleteById(anyLong());
        verifyNoInteractions(studentIndeksRepository, slusaPredmetRepository,
                predispitnaIzlazakRepository, ispitPrijavaRepository, ispitIzlazakRepository, polozenPredmetRepository);
    }

    @Test
    @DisplayName("deleteById: postoji student -> briše sve indekse i njihove veze + briše studenta")
    void deleteById_cascadeDeletes_andDeletesStudent() {
        Long studentId = 10L;
        Long indeksId = 20L;

        when(studentPodaciRepository.existsById(studentId)).thenReturn(true);

        StudentPodaci s = student(studentId);
        StudentIndeks si = indeks(indeksId, s);

        // 1) indeksi za studenta
        when(studentIndeksRepository.findStudentIndeksiForStudentPodaciId(studentId))
                .thenReturn(List.of(si));

        // 2) slusaPredmet-i u aktivnoj godini za indeks
        SlusaPredmet sp = slusaPredmet(30L);
        when(slusaPredmetRepository.getSlusaPredmetForIndeksAktivnaGodina(indeksId))
                .thenReturn(List.of(sp));

        // 3) predispitni izlasci (servis radi findAll + filter)
        PredispitnaIzlazak pi1 = predispitniIzlazak(40L, sp);
        PredispitnaIzlazak pi2 = predispitniIzlazak(41L, sp);
        when(predispitnaIzlazakRepository.findAll())
                .thenReturn(List.of(pi1, pi2));

        // 4) ispit prijave (findAll + filter), sa ispitIzlazak
        IspitIzlazak izl = ispitIzlazak(50L);
        IspitPrijava ip = prijava(60L, si, izl);
        when(ispitPrijavaRepository.findAll())
                .thenReturn(List.of(ip));

        // 5) polozeni predmeti (findAll + filter)
        PolozenPredmet pp = polozen(70L, si);
        when(polozenPredmetRepository.findAll())
                .thenReturn(List.of(pp));

        // act
        studentPodaciService.deleteById(studentId);

        // assert: predispitni izlasci obrisani
        verify(predispitnaIzlazakRepository).deleteById(40L);
        verify(predispitnaIzlazakRepository).deleteById(41L);

        // assert: slusaPredmet obrisan
        verify(slusaPredmetRepository).deleteById(30L);

        // assert: ispit izlazak pa prijava obrisani
        verify(ispitIzlazakRepository).deleteById(50L);
        verify(ispitPrijavaRepository).deleteById(60L);

        // assert: polozen predmet obrisan
        verify(polozenPredmetRepository).deleteById(70L);

        // assert: indeks obrisan
        verify(studentIndeksRepository).deleteById(indeksId);

        // assert: student obrisan
        verify(studentPodaciRepository).deleteById(studentId);
    }

    @Test
    @DisplayName("getAllStudentPodaciPaginated: mapira preko EntityMappers")
    void getAllStudentPodaciPaginated_maps() {
        int page = 0, size = 2;

        StudentPodaci s1 = student(1L);
        StudentPodaci s2 = student(2L);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<StudentPodaci> repoPage = new PageImpl<>(List.of(s2, s1), pageable, 2);

        when(studentPodaciRepository.findAll(any(Pageable.class))).thenReturn(repoPage);

        StudentPodaciResponse r1 = new StudentPodaciResponse(); r1.setId(1L);
        StudentPodaciResponse r2 = new StudentPodaciResponse(); r2.setId(2L);

        when(entityMappers.fromStudentPodaciToResponse(s1)).thenReturn(r1);
        when(entityMappers.fromStudentPodaciToResponse(s2)).thenReturn(r2);

        Page<StudentPodaciResponse> out = studentPodaciService.getAllStudentPodaciPaginated(page, size);

        assertEquals(2, out.getTotalElements());
        assertEquals(2, out.getContent().size());
        assertEquals(2L, out.getContent().get(0).getId()); // jer sort desc po id (repoPage već došao kao [s1,s2] ali mi ovde testiramo mapiranje)
        verify(studentPodaciRepository).findAll(any(Pageable.class));
        verify(entityMappers).fromStudentPodaciToResponse(s1);
        verify(entityMappers).fromStudentPodaciToResponse(s2);
    }
}
