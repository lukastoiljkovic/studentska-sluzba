package org.raflab.studsluzbadesktopclient.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzbadesktopclient.dto.StudentIndeksDTO;
import org.raflab.studsluzbadesktopclient.dto.StudentPodaciDTO;
import org.raflab.studsluzbadesktopclient.dto.StudentSearchResultDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final WebClient webClient;

    // ========== PRETRAGA ==========

    public Flux<StudentSearchResultDTO> search(String ime, String prezime,
                                               String studProgram, Integer godina,
                                               Integer broj, Integer page, Integer size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/student/search")
                        .queryParam("ime", ime)
                        .queryParam("prezime", prezime)
                        .queryParam("studProgram", studProgram)
                        .queryParam("godina", godina)
                        .queryParam("broj", broj)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .bodyToFlux(StudentSearchResultDTO.class);
    }

    public Mono<List<StudentSearchResultDTO>> searchSync(String ime, String prezime,
                                                         String studProgram, Integer godina,
                                                         Integer broj, Integer page, Integer size) {
        return search(ime, prezime, studProgram, godina, broj, page, size)
                .collectList();
    }

    public Mono<StudentIndeksDTO> fastSearch(String indeksShort) {
        return webClient.get()
                .uri("/api/student/fastsearch?indeksShort=" + indeksShort)
                .retrieve()
                .bodyToMono(StudentIndeksDTO.class);
    }

    // ========== CRUD ==========

    public Mono<StudentPodaciDTO> getStudentPodaciById(Long id) {
        return webClient.get()
                .uri("/api/student/podaci/{id}", id)
                .retrieve()
                .bodyToMono(StudentPodaciDTO.class);
    }

    public Mono<StudentIndeksDTO> getStudentIndeksById(Long id) {
        return webClient.get()
                .uri("/api/student/indeks/{id}", id)
                .retrieve()
                .bodyToMono(StudentIndeksDTO.class);
    }

    public Flux<StudentIndeksDTO> getIndeksiForStudent(Long studentPodaciId) {
        return webClient.get()
                .uri("/api/student/indeksi/{idStudentPodaci}", studentPodaciId)
                .retrieve()
                .bodyToFlux(StudentIndeksDTO.class);
    }

    public Mono<Long> saveStudentPodaci(StudentPodaciDTO student) {
        return webClient.post()
                .uri("/api/student/add")
                .bodyValue(student)
                .retrieve()
                .bodyToMono(Long.class);
    }

    public Mono<Long> saveIndeks(StudentIndeksDTO indeks) {
        return webClient.post()
                .uri("/api/student/saveindeks")
                .bodyValue(indeks)
                .retrieve()
                .bodyToMono(Long.class);
    }

    public Mono<Void> deleteStudent(Long id) {
        return webClient.delete()
                .uri("/api/student/{id}", id)
                .retrieve()
                .bodyToMono(Void.class);
    }

    // ========== DODATNE FUNKCIJE ==========

    public Mono<Void> dodajUplatu(Long studentId, Double iznosEUR) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/student/{studentId}/uplata")
                        .queryParam("iznosEUR", iznosEUR)
                        .build(studentId))
                .retrieve()
                .bodyToMono(Void.class);
    }

    public Mono<Map<String, Double>> getPreostaliIznos(Long upisGodineId) {
        return webClient.get()
                .uri("/api/student/{upisGodineId}/preostalo", upisGodineId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Double>>() {});
    }
}