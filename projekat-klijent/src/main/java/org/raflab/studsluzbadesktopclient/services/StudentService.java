package org.raflab.studsluzbadesktopclient.services;

import lombok.RequiredArgsConstructor;
import org.raflab.studsluzba.dtos.*;
import org.raflab.studsluzbadesktopclient.utils.StudentProfileMapper;
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
    private final StudentProfileMapper profileMapper;
    private final PolozenPredmetService polozenPredmetService;

    // ========== PRETRAGA ==========

    public Flux<StudentDTO> search(String ime, String prezime,
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
                .bodyToFlux(StudentDTO.class);
    }

    public Mono<List<StudentDTO>> searchSync(String ime, String prezime,
                                             String studProgram, Integer godina,
                                             Integer broj, Integer page, Integer size) {
        return search(ime, prezime, studProgram, godina, broj, page, size)
                .collectList();
    }

    public Mono<StudentIndeksResponse> fastSearch(String indeksShort) {
        return webClient.get()
                .uri("/api/student/fastsearch?indeksShort=" + indeksShort)
                .retrieve()
                .bodyToMono(StudentIndeksResponse.class);
    }

    public Mono<StudentIndeksResponse> emailSearch(String studEmail) {
        return webClient.get()
                .uri("/api/student/emailsearch?studEmail=" + studEmail)
                .retrieve()
                .bodyToMono(StudentIndeksResponse.class);
    }

    // ========== PROFIL ==========

    /**
     * Učitava kompletan profil studenta kombinirajući više API poziva
     */
    public Mono<org.raflab.studsluzbadesktopclient.dtos.StudentProfileDTO> getStudentProfile(Long studentIndeksId) {
        // 1. Učitaj osnovni profil sa servera
        Mono<org.raflab.studsluzba.dtos.StudentProfileDTO> serverProfileMono = webClient.get()
                .uri("/api/student/profile/{studentIndeksId}", studentIndeksId)
                .retrieve()
                .bodyToMono(org.raflab.studsluzba.dtos.StudentProfileDTO.class);

        // 2. Učitaj StudentIndeksResponse
        Mono<StudentIndeksResponse> indeksResponseMono = webClient.get()
                .uri("/api/student/indeks/{id}", studentIndeksId)
                .retrieve()
                .bodyToMono(StudentIndeksResponse.class);

        // 3. Učitaj StudentPodaciResponse (potreban je ID studenta iz indeksa)
        Mono<StudentPodaciResponse> podaciResponseMono = indeksResponseMono
                .flatMap(indeks -> {
                    if (indeks.getStudent() != null && indeks.getStudent().getId() != null) {
                        return webClient.get()
                                .uri("/api/student/podaci/{id}", indeks.getStudent().getId())
                                .retrieve()
                                .bodyToMono(StudentPodaciResponse.class);
                    }
                    return Mono.empty();
                });

        // 4. Učitaj položene predmete
        Mono<List<PolozenPredmetResponse>> polozeniMono = polozenPredmetService
                .getPolozeniIspiti(studentIndeksId, 0, 1000)
                .map(PageResponse::getContent);

        // 5. Učitaj nepoložene predmete
        Mono<List<NepolozenPredmetResponse>> nepolozeniMono = polozenPredmetService
                .getNepolozeniIspiti(studentIndeksId, 0, 1000)
                .map(PageResponse::getContent);

        // 6. Učitaj upisane godine
        Mono<List<UpisGodineResponse>> upisiMono = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/upis-godine/all")
                        .queryParam("studentIndeksId", studentIndeksId)
                        .build())
                .retrieve()
                .bodyToFlux(UpisGodineResponse.class)
                .collectList();

        // 7. Učitaj obnove godine
        Mono<List<ObnovaGodineResponse>> obnoveMono = webClient.get()
                .uri("/api/obnova/student/{studentIndeksId}", studentIndeksId)
                .retrieve()
                .bodyToFlux(ObnovaGodineResponse.class)
                .collectList();

        Mono<List<UplataResponse>> uplateMono = upisiMono.flatMapMany(upisi ->
                Flux.fromIterable(upisi)
                        .flatMap(upis ->
                                webClient.get()
                                        .uri("/api/uplate/upis-godine/{id}", upis.getId())
                                        .retrieve()
                                        .bodyToFlux(UplataResponse.class)
                        )
        ).collectList();

        // Kombinuj sve Mono objekte
        return Mono.zip(
                serverProfileMono,
                indeksResponseMono,
                podaciResponseMono,
                polozeniMono,
                nepolozeniMono,
                upisiMono,
                obnoveMono,
                uplateMono
        ).map(tuple -> profileMapper.mapToClientDTO(
                tuple.getT1(), // server profile
                tuple.getT2(), // indeks response
                tuple.getT3(), // podaci response
                tuple.getT4(), // polozeni
                tuple.getT5(), // nepolozeni
                tuple.getT6(), // upisi
                tuple.getT7(), // obnove
                tuple.getT8()  // uplate
        ));
    }

    public Mono<StudentWebProfileDTO> getStudentWebProfile(Long studentIndeksId) {
        return webClient.get()
                .uri("/api/student/webprofile/{studentIndeksId}", studentIndeksId)
                .retrieve()
                .bodyToMono(StudentWebProfileDTO.class);
    }

    public Mono<StudentWebProfileDTO> getStudentWebProfileForEmail(String studEmail) {
        return webClient.get()
                .uri("/api/student/webprofile/email?studEmail=" + studEmail)
                .retrieve()
                .bodyToMono(StudentWebProfileDTO.class);
    }

    // ========== CRUD ==========

    public Mono<StudentPodaciResponse> getStudentPodaciById(Long id) {
        return webClient.get()
                .uri("/api/student/podaci/{id}", id)
                .retrieve()
                .bodyToMono(StudentPodaciResponse.class);
    }

    public Mono<StudentIndeksResponse> getStudentIndeksById(Long id) {
        return webClient.get()
                .uri("/api/student/indeks/{id}", id)
                .retrieve()
                .bodyToMono(StudentIndeksResponse.class);
    }

    public Flux<StudentIndeksResponse> getIndeksiForStudent(Long studentPodaciId) {
        return webClient.get()
                .uri("/api/student/indeksi/{idStudentPodaci}", studentPodaciId)
                .retrieve()
                .bodyToFlux(StudentIndeksResponse.class);
    }

    public Mono<Long> saveStudentPodaci(StudentPodaciResponse student) {
        return webClient.post()
                .uri("/api/student/add")
                .bodyValue(student)
                .retrieve()
                .bodyToMono(Long.class);
    }

    public Mono<Long> saveIndeks(StudentIndeksRequest indeks) {
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

    // ========== UPLATE ==========

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

    public Mono<Long> saveStudentPodaci(StudentPodaciCreateRequest request) {
        return webClient.post()
                .uri("/api/student/add")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Long.class);
    }


}