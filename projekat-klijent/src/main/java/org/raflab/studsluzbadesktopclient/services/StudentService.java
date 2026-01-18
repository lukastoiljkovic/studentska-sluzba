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
                                             Integer broj, String srednjaSkola,
                                             Integer page, Integer size) {
        return searchPage(ime, prezime, studProgram, godina, broj, srednjaSkola, page, size)
                .map(PageResponse::getContent);
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

    public Mono<org.raflab.studsluzbadesktopclient.dtos.StudentProfileDTO> getStudentProfile(Long studentIndeksId) {
        Mono<org.raflab.studsluzba.dtos.StudentProfileDTO> serverProfileMono = webClient.get()
                .uri("/api/student/profile/{studentIndeksId}", studentIndeksId)
                .retrieve()
                .bodyToMono(org.raflab.studsluzba.dtos.StudentProfileDTO.class);

        Mono<StudentIndeksResponse> indeksResponseMono = webClient.get()
                .uri("/api/student/indeks/{id}", studentIndeksId)
                .retrieve()
                .bodyToMono(StudentIndeksResponse.class);

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

        Mono<PageResponse<PolozenPredmetResponse>> polozeniMono = getPolozeniIspiti(studentIndeksId, 0, 1000);
        Mono<PageResponse<NepolozenPredmetResponse>> nepolozeniMono = getNepolozeniIspiti(studentIndeksId, 0, 1000);

        Mono<List<UpisGodineResponse>> upisiMono = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/upis-godine/all")
                        .queryParam("studentIndeksId", studentIndeksId)
                        .build())
                .retrieve()
                .bodyToFlux(UpisGodineResponse.class)
                .collectList();

        Mono<List<ObnovaGodineResponse>> obnoveMono = webClient.get()
                .uri("/api/obnova/student/{studentIndeksId}", studentIndeksId)
                .retrieve()
                .bodyToFlux(ObnovaGodineResponse.class)
                .collectList();

        Mono<List<UplataResponse>> uplateMono = Mono.just(java.util.Collections.emptyList());

        return Mono.zip(
                serverProfileMono,
                indeksResponseMono,
                podaciResponseMono,
                polozeniMono.map(PageResponse::getContent),
                nepolozeniMono.map(PageResponse::getContent),
                upisiMono,
                obnoveMono,
                uplateMono
        ).map(tuple -> profileMapper.mapToClientDTO(
                tuple.getT1(),
                tuple.getT2(),
                tuple.getT3(),
                tuple.getT4(),
                tuple.getT5(),
                tuple.getT6(),
                tuple.getT7(),
                tuple.getT8()
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

    // ========== POLOŽENI I NEPOLOŽENI ISPITI ==========

    public Mono<PageResponse<PolozenPredmetResponse>> getPolozeniIspiti(Long studentIndeksId, int page, int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/predmet/polozen/polozeni/{studentIndeksId}")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build(studentIndeksId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageResponse<PolozenPredmetResponse>>() {});
    }

    public Mono<PageResponse<NepolozenPredmetResponse>> getNepolozeniIspiti(Long studentIndeksId, int page, int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/predmet/polozen/nepolozeni/{studentIndeksId}")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build(studentIndeksId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageResponse<NepolozenPredmetResponse>>() {});
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


    public Mono<Long> saveStudentPodaci(StudentPodaciCreateRequest student) {
        return webClient.post()
                .uri("/api/student/add")
                .bodyValue(student)
                .retrieve()
                .bodyToMono(Long.class);
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


    public Mono<PageResponse<StudentDTO>> searchPage(
            String ime,
            String prezime,
            String studProgram,
            Integer godina,
            Integer broj,
            String srednjaSkola,
            Integer page,
            Integer size) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/student/search")
                        .queryParam("ime", ime)
                        .queryParam("prezime", prezime)
                        .queryParam("studProgram", studProgram)
                        .queryParam("godina", godina)
                        .queryParam("broj", broj)
                        .queryParam("srednjaSkola", srednjaSkola)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<PageResponse<StudentDTO>>() {});
    }

}
